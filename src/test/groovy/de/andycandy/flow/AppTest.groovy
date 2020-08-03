/*
 * This Spock specification was generated by the Gradle 'init' task.
 */
package de.andycandy.flow

import org.junit.Test

import de.andycandy.flow.App
import spock.lang.Specification

class AppTest extends Specification {
    
	
	@Test
	def "test app evaluate"() {
		setup:
		App app = new App()
		File file = new File(Thread.currentThread().getContextClassLoader().getResource('de/andycandy/flow/flow.dsl').path)
		
		when:
		def result = app.evaluate(file)
		
		then:
		result == [0, 1, 4, 9]
	}
	
	
	@Test
	def "test app evaluate no output"() {
		setup:
		App app = new App()
		File file = new File(Thread.currentThread().getContextClassLoader().getResource('de/andycandy/flow/flow_no_output.dsl').path)
		
		when:
		def result = app.evaluate(file)
		
		then:
		result == null
	}
	
	@Test
	def "test app exit on usage"() {
		setup:
		def systemExit = false
		def error = ''
		App app = new App() {
			@Override
			public void systemExit() {
				systemExit = true
			}
			
			@Override
			public void printErr(String text) {
				error = text
			}
		}
		
		when:
		app.start(new String[0])
		
		then:
		systemExit
		error == 'Usage: FlowDSL [file]'
	}
	
	@Test
	def "test app exit on no file"() {
		setup:
		def systemExit = false
		def error = ''
		App app = new App() {
			@Override
			public void systemExit() {
				systemExit = true
			}
			
			@Override
			public void printErr(String text) {
				error = text
			}
		}
		def file = new File('dafuq')
		
		when:
		app.start([file.absolutePath].toArray(new String[1]))
		
		then:
		systemExit
		error == "File ${file.absolutePath} not exists!"
	}
	
	@Test
	def "test app start"() {
		setup:
		def evaluateFile
		
		App app = new App() {
			
			@Override
			public Object evaluate(File file) {
				evaluateFile = file
			}
		}
		
		File file = new File(Thread.currentThread().getContextClassLoader().getResource('de/andycandy/flow/flow_no_output.dsl').path)
		
		
		when:
		app.start([file.absolutePath].toArray(new String[1]))
		
		then:
		evaluateFile == file
	}
}