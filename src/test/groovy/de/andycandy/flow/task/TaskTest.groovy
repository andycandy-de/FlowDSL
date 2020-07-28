package de.andycandy.flow.task

import de.andycandy.flow.task.CodeTask
import de.andycandy.flow.task.EachTask
import de.andycandy.flow.task.FlattenTask
import spock.lang.Specification

class TaskTest extends Specification {
	
	
	def funTaskTest(Closure closure) {
		
		CodeTask codeTask = new CodeTask()
		codeTask.closure = closure
		
		closure.delegate = codeTask
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		codeTask.input = 'test'
		
		codeTask.call()
		
		codeTask.output
	}
	
	
	def 'test task'() {
		expect:
		'TEST' == funTaskTest {
			output = input.toUpperCase()
		}
	}
	
	def funEachTaskTest(input, Closure closure) {
		
		CodeTask codeTask = new CodeTask()
		codeTask.closure = closure
		
		closure.delegate = codeTask
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		EachTask eachTask = new EachTask()
		eachTask.input = input
		eachTask.task = codeTask
		
		eachTask.call()
		
		eachTask.output
	}
	
	def 'test each task with list'() {
		expect:
		[1, 3] == funEachTaskTest([0, 1, 2, 3, 4]) { 
			if (input.value % 2 == 1) {
				output = input.value
			}
		}
	}
	
	def 'test each task with map'() {
		expect:
		['ha', 'l', 'lo'] == funEachTaskTest([0 : 'ha', 1 : 'He', 2 : 'l', 3 : 'la', 4 : 'lo']) {
			if (input.key % 2 == 0) {
				output = input.value
			}
		}
	}
	
	def 'test each task with error'() {
		when:
		funEachTaskTest(1) { }
		
		then:
		final IllegalStateException exception = thrown()
		'Input must be an instance of Collection or Map' == exception.message
	}
	
	
	def 'test task flatten'() {
		when:
		FlattenTask flattenTask = new FlattenTask()
		flattenTask.input = [[1, 2], [2, 3]]
		flattenTask.call()
		
		then:
		[1, 2, 2, 3] == flattenTask.output
	}
	
	def 'test task flatten map'() {
		when:
		FlattenTask flattenTask = new FlattenTask()
		flattenTask.input = [[[1 : 2, 2 : 3], [3 : 4]], [[4:5]]]
		flattenTask.deep = 2
		flattenTask.call()
		
		then:
		[1:2, 2:3, 3:4, 4:5] == flattenTask.output
	}
	
	def 'test step task'() {
		when:
		StepTask stepTask = new StepTask()
		stepTask.context = ['map':'val']
		stepTask.input = 99
		
		CodeTask codeTask1 = new CodeTask()
		codeTask1.closure = {
			context.test = 'HEY'
			output = "$input ${context.map}"
		}
		stepTask.tasks << codeTask1
		
		CodeTask codeTask2 = new CodeTask()
		codeTask2.closure = {
			output = input + input + context.test
		}
		stepTask.tasks << codeTask2
		
		stepTask.call()
		
		then:
		'99 val99 valHEY' == stepTask.output
	}
	
	
	def 'test context'() {
		when:
		Context context = new Context()
		context.test = '123'
		
		then:
		'123' == context.test
	}
	
	
	def 'test context missing property'() {
		when:
		Context context = new Context()
		context.test
		
		then:
		final MissingPropertyException exeption = thrown()
	}
	
	def 'test context readonly property'() {
		when:
		Context context = new Context()
		context.test = '123'
		context.test = '234'
				
		then:
		final ReadOnlyPropertyException exeption = thrown()
	}
	
}
