package de.andycandy.flow

import static de.andycandy.flow.FlowDSL.createFlow

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.junit.Test

import de.andycandy.flow.task.Task
import spock.lang.Specification
import de.andycandy.flow.plugin.io.IOPlugin

class FlowDSLTest extends Specification {
	
	@Test
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
	
	@Test	
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
	
	@Test	
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
	
	@Test
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
	
	@Test
	def 'test flow with plugin'() {
		setup:
		def temp = createTemp('de/andycandy/flow/dir')
		
		when:
		Task task = createFlow {
			
			def context = context {
				dir = temp.toString()
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
		
		cleanup:
		deleteTempDir(temp)
	}
	
	@Test	
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
	
	@Test	
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
	
	@Test
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
	
	@Test
	def 'test flow with plugin read'() {
		
		setup:
		def temp = createTemp('de/andycandy/flow/dir')
		
		when:
		Task task = createFlow {
			
			plugins {
				register IOPlugin.create()
			}
			
			input {
				temp.toString()
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
		
		cleanup:
		deleteTempDir(temp)
	}
	
	@Test
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
	
	@Test
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
	
	@Test
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
	
	@Test
	def 'test plugin with name'() {
		when:
		
		def writeFile = new File('test.txt')
		def fileText = 'AnyText'
		
		Task task = createFlow {
			
			plugins {
				
				register IOPlugin.create() withName 'data'
			}
			
			input { fileText }
			
			data.write {
				
				file writeFile
			}
		}
		
		task.call()
		
		then:
		writeFile.exists()
		fileText == writeFile.text
		
		cleanup:
		writeFile.delete()
	}
	
	@Test
	def 'test write duplicate file'() {
		when:
		
		def writeFile = new File('test.txt')
		def fileText = 'AnyText'
		
		Task task = createFlow {
			
			plugins {
				
				register IOPlugin.create() withName 'data'
			}
			
			input { fileText }
			
			data.write {
				
				file writeFile
				file writeFile
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'It not allowed to define multiple files!' == exception.message
	}
	
	@Test
	def 'test parent is a file'() {
		when:
		
		def writeFile = new File('test/test.txt')
		def parent = new File('test')
		def fileText = 'AnyText'
		
		parent.createNewFile()
		
		Task task = createFlow {
			
			plugins {
				
				register IOPlugin.create() withName 'data'
			}
			
			input { fileText }
			
			data.write {
				
				file writeFile
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'\'test\' is not a directory!' == exception.message
		
		cleanup:
		parent.delete()
	}
	
	@Test
	def 'test create parent error'() {
		when:
		def parent = getUnreachableDir()
		def writeFile = new File(parent, 'test.txt')
		def fileText = 'AnyText'
		
		Task task = createFlow {
			
			plugins {
				
				register IOPlugin.create() withName 'data'
			}
			
			input { fileText }
			
			data.write {
				
				file writeFile
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		"Cannot create directory '${parent}'!" == exception.message
	}
	
	@Test
	def 'test create parent'() {
		when:
		
		def parentFile = new File('test')
		def writeFile = new File(parentFile, 'test.txt')
		def fileText = 'AnyText'
		
		Task task = createFlow {
			
			plugins {
				
				register IOPlugin.create() withName 'data'
			}
			
			input { fileText }
			
			data.write {
				
				file writeFile
			}
		}
		
		task.call()
		
		then:
		parentFile.exists()
		writeFile.exists()
		fileText == writeFile.text
		
		cleanup:
		writeFile.delete()
		parentFile.delete()
	}
	
	@Test
	def 'test map key'() {
		when:
		Task task = createFlow {
						
			input { [test : 1, das : 99, mal : 123] }
			
			forEach {
				
				mapKey()
			}
		}
		
		task.call()
		
		then:
		['test', 'das', 'mal'] == task.output
	}
	
	@Test
	def 'test map key error'() {
		when:
		Task task = createFlow {
						
			input { (0 .. 3) }
			
			forEach {
				
				mapKey()
			}
		}
		
		task.call()
		
		then:
		final IllegalArgumentException exception = thrown()
		'Input type \'CollectionEntryInput\' is not instance of MapEntryInput' == exception.message
	}
	
	@Test
	def 'test map value collection'() {
		when:
		Task task = createFlow {
						
			input { (0 .. 3) }
			
			forEach {
				
				mapValue()
				
				filter { input % 2 == 1 }
			}
		}
		
		task.call()
		
		then:
		[1, 3] == task.output
	}
	
	@Test
	def 'test map value map'() {
		when:
		Task task = createFlow {
						
			input { [0: 3, 4 : 8] }
			
			forEach {
				
				mapValue()
			}
		}
		
		task.call()
		
		then:
		[3, 8] == task.output
	}
	
	@Test
	def 'test map value error'() {
		when:
		Task task = createFlow {
						
			input { (0 .. 3) }
			
			forEach {
				
				mapValue()
				
				mapValue()
			}
		}
		
		task.call()
		
		then:
		final IllegalArgumentException exception = thrown()
		'Input type \'Integer\' is not instance of CollectionEntryInput or MapEntryInput' == exception.message
	}
	
	@Test
	def 'test flatten deep'() {
		when:
		Task task = createFlow {
						
			input { (0 .. 1) }
			
			forEach {
				
				mapValue()
				
				map { (0 .. input + 1) }
				
				forEach {
					
					mapValue()
					
					map { (0 .. input + 1) }
				}			
			}
			
			flatten 2
		}
		
		task.call()
		
		then:
		[0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 0, 1, 2, 3] == task.output
	}
	
	@Test
	def 'test flatten no collection exception'() {
		when:
		Task task = createFlow {
						
			input { 1 }
			
			flatten()
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'Input must be an instance of Collection' == exception.message
	}
	
	@Test
	def 'test flatten no collection or map in collection exception'() {
		when:
		Task task = createFlow {
						
			input { ( 0 .. 2 ) }
			
			flatten()
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'Input must contain Collections or Maps' == exception.message
	}
	
	@Test
	def 'test flatten deep exception'() {
		when:
		Task task = createFlow {
						
			input { ( 0 .. 2 ) }
			
			flatten 0
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'Deep cannot be lower than 1' == exception.message
	}
	
	@Test
	def 'test flatten no collection in collection exception'() {
		when:
		Task task = createFlow {
						
			input { [[0, 1], [1:1,2:2]] }
			
			flatten()
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'Input types are incompatible! Input collection must contain only collections or only lists!' == exception.message
	}
	
	@Test
	def 'test flatten no map in collection exception'() {
		when:
		Task task = createFlow {
						
			input { [[1:1,2:2], [0, 1]] }
			
			flatten()
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'Input types are incompatible! Input collection must contain only collections or only lists!' == exception.message
	}
	
	@Test
	def 'test read multiple files error'() {
		
		def readFile = new File('anyFile')
		when:
		Task task = createFlow {
						
			plugins {
				
				register IOPlugin.create()
			}
			
			io.read {
				
				file readFile
				file readFile
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'It not allowed to define multiple files!' == exception.message
	}
	
	@Test
	def 'test read file'() {
		
		setup:
		def temp = createTemp('de/andycandy/flow/dir')
		
		when:
		Task task = createFlow {
						
			plugins {
				
				register IOPlugin.create()
			}
			
			io.read {
				
				file "${temp}/test2"
			}
		}
		
		task.call()
		
		then:
		'Test2Content' == task.output
		
		cleanup:
		deleteTempDir(temp)
	}
	
	@Test
	def 'test context readonly error'() {
		
		when:
		Task task = createFlow {
			
			def mainContext = context {
				contextVal = '123'
			}
			
			code {
				mainContext.contextVal = '234'
			}
		}
		
		task.call()
		
		then:
		final ReadOnlyPropertyException exception = thrown()
		'Cannot set readonly property: 234 for class: de.andycandy.flow.task.context.ContextTask' == exception.message
	}
	
	@Test
	def 'test context missing property error'() {
		
		when:
		Task task = createFlow {
			
			def mainContext = context {
				contextVal1 = '123'
			}
			
			code {
				mainContext.contextVal2
			}
		}
		
		task.call()
		
		then:
		final MissingPropertyException exception = thrown()
		'No such property: contextVal2 for class: de.andycandy.flow.task.context.ContextTask' == exception.message
	}
	
	@Test
	def 'test forEach error'() {
		
		when:
		Task task = createFlow {
			
			input { 1 }
			
			forEach {
				
				mapValue()
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'Input must be an instance of Collection or Map' == exception.message
	}
	
	@Test
	def 'test read with charset'() {
		
		setup:
		def temp = createTemp('de/andycandy/flow/dir')
		
		when:
		Task task = createFlow {
						
			plugins {
				
				register IOPlugin.create()
			}
			
			io.read {
				
				file "${temp}/test2"
				charset 'UTF-8'
			}
		}
		
		task.call()
		
		then:
		'Test2Content' == task.output
		
		cleanup:
		deleteTempDir(temp)
	}
	
	@Test
	def 'test read with multiple charset error'() {
		
		setup:
		def anyDir = 'anyDir'
		
		when:
		Task task = createFlow {
						
			plugins {
				
				register IOPlugin.create()
			}
			
			io.read {
				
				file anyDir
				charset 'UTF-8'
				charset 'UTF-16'
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'It not allowed to define multiple charsets!' == exception.message
	}
	
	@Test
	def 'test ls with multiple dirs error'() {
		
		setup:
		def anyDir = 'anyDir'
				
		when:
		Task task = createFlow {
						
			plugins {
				
				register IOPlugin.create()
			}
			
			io.ls {
				
				dir anyDir
				dir anyDir
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'It not allowed to define multiple dirs!' == exception.message
	}
	
	@Test
	def 'test ls with output mapper'() {
		
		setup:
		def temp = createTemp('de/andycandy/flow/dir')
		def expected = [:]
		expected[temp.toString()] = ['test1', 'test2', 'test3']
				
		when:
		Task task = createFlow {
			
			input { temp.toString() }
			
			plugins { register IOPlugin.create() }
			
			io.ls { dir input } {
				
				def fileNames = []
				output.each { fileNames << it.name }
				
				output = [:]
				output[input] = fileNames
			}
		}
		
		task.call()
		
		then:
		expected == task.output
		
		cleanup:
		deleteTempDir(temp)
	}
	
	@Test
	def 'test read with output mapper'() {
		
		setup:
		def temp = createTemp('de/andycandy/flow/dir')
				
		when:
		Task task = createFlow {
			
			input { temp.toString() }
			
			plugins { register IOPlugin.create() }
			
			io.ls { dir input }
			
			forEach {
				mapValue()
				
				io.read { file input } {
					
					def newOutput = []
					newOutput << input.name << output
					output = newOutput
				}
				
				toMap {
					key input[0]
					value input[1]
				}
			}
			
			filter { !input.value.isEmpty() }
		}
		
		task.call()
		
		then:
		[test2 : 'Test2Content', test3 : 'Test3Content'] == task.output
		
		cleanup:
		deleteTempDir(temp)
	}
	
	@Test
	def 'test flow write with charset'() {
		setup:
		def temp = createTemp()
		def testFile = new File(temp.toFile(), 'test.txt')
		
		when:
		Task task = createFlow {
			
			plugins { register IOPlugin.create() }
			
			input { 'testText' }
			
			flow {
				
				io.write {
					file testFile
					charset 'UTF-8'
				}
			}
		}
		
		task.call()
		
		then:
		testFile.exists()
		'testText' == testFile.text
		
		cleanup:
		deleteTempDir(temp)
	}
	
	@Test
	def 'test flow write with charset error'() {
		setup:
		def testFile = new File('anytest.txt')
		
		when:
		Task task = createFlow {
			
			plugins { register IOPlugin.create() }
			
			input { 'testText' }
			
			flow {
				
				io.write {
					file testFile
					charset 'UTF-8'
					charset 'UTF-16'
				}
			}
		}
		
		task.call()
		
		then:
		final IllegalStateException exception = thrown()
		'It\'s not allowed to define multiple charsets!' == exception.message
	}
	
	@Test
	def 'test flow map for map'() {
				
		when:
		Task task = createFlow {
			
			input { [1:2, 3:4] }
			
			map { input.key }
		}
		
		task.call()
		
		then:
		[1, 3] == task.output
	}
	
	@Test
	def 'test flow forEach toList'() {
				
		when:
		Task task = createFlow {
			
			input { [1, 2, 3, 4] }
			
			forEach {
				filter { input.value % 2 == 0 }
			}
		}
		
		task.call()
		
		then:
		[2, 4] == task.output
	}
	
	@Test
	def 'test flow forEach empty list'() {
				
		when:
		Task task = createFlow {
			
			input { [1, 2, 3, 4] }
			
			forEach {
				filter { input.value > 5 }
			}
		}
		
		task.call()
		
		then:
		[] == task.output
	}
	
	def getUnreachableDir() {
		
		if (System.getProperty("os.name").startsWithIgnoreCase('windows')) {
			return new File('ABC:/test/')
		}
		else {
			return new File('/root/anyDir/')
		}
	}
	
	def createTemp(String path) {
		
		Path temp = Files.createTempDirectory('test')
		Files.createDirectories(temp)
		
		def files = (new File(Thread.currentThread().getContextClassLoader().getResource(path).path)).listFiles()
		
		files.each {
			Files.copy(it.toPath(), Paths.get(temp.toString(), it.name))
		}
		
		return temp
	}
	
	def createTemp() {
		
		Path temp = Files.createTempDirectory('test')
		Files.createDirectories(temp)
		
		return temp
	}
	
	def deleteTempDir(Path temp) {
		
		Files.walk(temp) \
			.sorted(Comparator.reverseOrder()) \
			.map { it.toFile() } \
			.forEach { it.delete() }
	}
}
