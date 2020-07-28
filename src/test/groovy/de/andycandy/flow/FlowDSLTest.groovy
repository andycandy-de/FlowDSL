package de.andycandy.flow

import static de.andycandy.flow.FlowDSL.flow

import de.andycandy.flow.task.Task
import spock.lang.Specification
import de.andycandy.flow.plugin.io.IOPlugin

class FlowDSLTest extends Specification {
	
	def 'test flow'() {
		when:
		Task task = flow {
			
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
		Task task = flow {
			
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
		Task task = flow {
			
			input { (0 .. 10) }
			
			forEach {
				
				map { entry -> entry.value }
				
				filter { value -> value % 2 == 0 }
				
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
		Task task = flow {
			
			context {
				multipy = 10
			}
			
			input { (0 .. 10) }
			
			forEach {
				
				map { entry -> entry.value }
				
				filter { value -> value % 2 == 1 }
				
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
		Task task = flow {
			
			context {
				dir = 'src/test/resources/de/andycandy/flow/dir'
			}
			
			plugins {
				register IOPlugin.create()
			}
			
			io.ls {
				dir context.dir
			}
			
			forEach {
				
				map { input.value.name }
			}
		}
		
		task.call()
		
		then:
		['test1', 'test2', 'test3'] == task.output
	}
	
	
	def 'test flow with plugin write'() {
		when:
		Task task = flow {
			
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
			
			step {
				
				io.write {
					file = 'test.txt'
				}
			}
		}
		
		task.call()
		
		then:
		(new File('test.txt')).exists()
		
		cleanup:
		(new File('test.txt')).delete()
	}
	
	def 'test flow with plugin read'() {
		when:
		Task task = flow {
			
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
					file = input.value
				}
				
				filter {input.length() > 0}
			}
		}
		
		task.call()
		
		then:
		['Test2Content', 'Test3Content'] == task.output
	}
}
