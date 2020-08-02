package de.andycandy.flow

import static de.andycandy.flow.FlowDSL.createFlow

import de.andycandy.flow.task.Task
import spock.lang.Specification
import de.andycandy.flow.plugin.io.IOPlugin

class FlowDSLTest extends Specification {
	
	def 'test flow'() {
		when:
		Task task = createFlow {
			
			forEach {
				
				code {
					output = input.value * 2
				}
				
				code {
					output = []
					for (int i = 0; i < input; ++i) {
						output << input
					}
				}
			}
			
			flatten()
		}
		
		task.input = [1, 2, 3]
		
		task.call()
		
		then:
		[2, 2, 4, 4, 4, 4 ,6 , 6, 6, 6, 6, 6] == task.output
	}
	
	
	def 'test flow input'() {
		when:
		Task task = createFlow {
			
			input { 200 }
			
			code {
				output = (110 .. input)
			}
			
			forEach {
				
				code {
					output = [:]
					output[input.index + 11] = input.value
				}
			}
			
			flatten()
			
			forEach {
				
				code {
					if (input.key % 9 == 3 && input.key % 5 == 2) {
						int key = input.key
						output = [:]
						output[input.key] = input.value
					}
				}
			}
			
			flatten()
		}
		
		task.call()
		
		then:
		[12:111, 57:156] == task.output
	}
	
	
	def 'test flow with conditional'() {
		when:
		Task task = createFlow {
			
			input { (0 .. 10) }
			
			forEach {
				
				mapValue()
				
				filter { input % 2 == 0 }
				
				conditional { hasInput() } {
					
					map { input * 2}
				}
				
				conditional { !hasInput() } {
					
					input { -1 }
				}
			}
		}
		
		task.call()
		
		then:
		[0, -1, 4, -1, 8, -1, 12, -1, 16, -1, 20] == task.output
	}
	
	def 'test flow with context'() {
		when:
		Task task = createFlow {
			
			def context = context {
				multipy = 10
			}
			
			input { (0 .. 10) }
			
			forEach {
				
				mapValue()
				
				filter { input % 2 == 1 }
				
				conditional { hasInput() } {
					
					code { output = input * context.multipy }
				}	
			}
		}
		
		task.call()
		
		then:
		[10, 30, 50, 70, 90] == task.output
	}
	
	def 'test flow with plugin'() {
		when:
		Task task = createFlow {
			
			def context = context {
				dir = 'src/test/resources/de/andycandy/flow/dir'
			}
			
			plugins {
				register IOPlugin.create()
			}
			
			io.ls {
				dir context.dir
			}
			
			forEach {
				
				mapValue()
				
				map { input.name }
			}
		}
		
		task.call()
		
		then:
		['test1', 'test2', 'test3'] == task.output
	}
	
	
	def 'test flow with plugin write'() {
		when:
		Task task = createFlow {
			
			plugins {
				register IOPlugin.create()
			}
			
			input {
				'''
				Das ist der Output Text
				Über meherere Zeilen
				TEST

				'''
			}
			
			flow {
				
				io.write {						
					file 'test.txt'
				}
			}
		}
		
		task.call()
		
		then:
		(new File('test.txt')).exists()
		
		cleanup:
		(new File('test.txt')).delete()
	}
	
	
	def 'test flow with plugin write no override'() {
		when:
		Task task = createFlow {
			
			plugins {
				register IOPlugin.create()
			}
			
			input {
				'''
				Das ist der Output Text
				Über meherere Zeilen
				TEST

				'''
			}
			
			io.write {
				file 'test.txt'
			}
			
			io.write {
				file 'test.txt'
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'The file \'test.txt\' already exists!' == exception.message
		
		cleanup:
		(new File('test.txt')).delete()
	}
	
	def 'test flow with plugin write override'() {
		when:
		Task task = createFlow {
			
			plugins {
				register IOPlugin.create()
			}
			
			input { 'testcontent' }
			
			io.write {
				file 'test.txt'
			}
			
			input { 'testcontent2' }
			
			io.write {
				override()
				file 'test.txt'
			}
		}
		
		task.call()
		
		File testFile = new File('test.txt')
		
		then:
		testFile.exists()
		'testcontent2' == testFile.text
		
		cleanup:
		testFile.delete()
	}
	
	def 'test flow with plugin read'() {
		when:
		Task task = createFlow {
			
			plugins {
				register IOPlugin.create()
			}
			
			input {
				'src/test/resources/de/andycandy/flow/dir'
			}
			
			io.ls {
				dir input
			}
			
			forEach {
				
				io.read {
					file input.value
				}
				
				filter {input.length() > 0}
			}
		}
		
		task.call()
		
		then:
		['Test2Content', 'Test3Content'] == task.output
	}
	
	def 'test value holder'() {
		when:
		Task task = createFlow {
			
			input {
				(3 .. 6)
			}
			
			def anyHolder = valueHolder()
			
			code {
				passInputToOutput()
				anyHolder.value = []
			}
			
			forEach {
				
				code {
					passInputToOutput()
					anyHolder.value << (input.index * input.index)
				}
				
				map {
					def map = [:]
					map[input.index] = input.value
					map
				}
			}
			flatten()
			
			forEach {
				
				def keyHolder = valueHolder { input.key }
				
				mapValue()
				
				map { input * 2 }
				
				def valueHolder = valueHolder { input }
				
				input { anyHolder.value }
				
				forEach {
					
					mapValue()
					
					map { input + keyHolder.value + valueHolder.value }
				}
			}			
			flatten()
		}
		
		task.call()
		
		then:
		[6, 7, 10, 15, 9, 10, 13, 18, 12, 13, 16, 21, 15, 16, 19, 24] == task.output
	}
	
	def 'test filter list'() {
		when:
		Task task = createFlow {
			
			input {
				(0 .. 6)
			}
			
			filter { input % 2 == 1 }
		}
		
		task.call()
		
		then:
		[1, 3, 5] == task.output
	}
	
	
	
	def 'test map list'() {
		when:
		Task task = createFlow {
			
			input {
				(0 .. 3)
			}
			
			map { input * 2 }
		}
		
		task.call()
		
		then:
		[0, 2, 4, 6] == task.output
	}
}
