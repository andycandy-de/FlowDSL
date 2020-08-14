package de.andycandy.flow

import de.andycandy.flow.task.Task
import org.junit.Test

import spock.lang.Specification

class ScriptPluginHelperTest extends Specification {
	
	@Test
	def 'test create dynamic'() {
		setup:
		def plugin = new ScriptPluginHelper()
		
		def dynamic = plugin.createDynamic { 
			
			method test: {
				return 'asd'
			}
		}
		
		when:
		def result = dynamic.test()
		
		then:
		'asd' == result
	}
	
	@Test
	def 'test create dynamic object class'() {
		setup:
		def plugin = new ScriptPluginHelper()
		
		def dynamic = plugin.createDynamic {
			
			method test: {
				return 'asd'
			}
		}
		
		when:
		def obj = new Object()
		obj.test()
		
		then:
		final MissingMethodException exception = thrown()
	}
	
	@Test
	def 'test create dynamic with args'() {
		setup:
		def plugin = new ScriptPluginHelper()
		
		def dynamic = plugin.createDynamic {
			
			method test: { String s ->
				return "asd+$s"
			}
		}
		
		when:
		def result = dynamic.test('dsa')
		
		then:
		'asd+dsa' == result
	}
	
	@Test
	def 'test create dynamic with read property'() {
		setup:
		def plugin = new ScriptPluginHelper()
		
		def dynamic = plugin.createDynamic {
			
			method getInput: { '123' }
		}
		
		when:
		def result = dynamic.input
		
		then:
		'123' == result
	}
	
	@Test
	def 'test create dynamic with read property boolean'() {
		setup:
		def plugin = new ScriptPluginHelper()
		
		def dynamic = plugin.createDynamic {
			
			method isInput: { true }
		}
		
		when:
		def result = dynamic.input
		
		then:
		result
	}
	
	@Test
	def 'test create dynamic with set property'() {
		setup:
		def plugin = new ScriptPluginHelper()
		def val = ''
		
		def dynamic = plugin.createDynamic {
			
			method setOutput: { String s -> val = s }
		}
		
		when:
		dynamic.output = '321'
		
		then:
		'321' == val
	}
	
	@Test
	def 'test create dynamic method overloading'() {
		setup:
		def plugin = new ScriptPluginHelper()
		def vals = []
		def someSting = 'asd'
		
		def dynamic = plugin.createDynamic {
			method test: { -> vals << 'test1' }
			method test: { String s -> vals << s }
			method test: { Long n -> vals << "Long$n" }
			method test: { Integer n -> vals << "Integer$n" }
		}
		
		when:
		dynamic.test()
		dynamic.test('Hallo')
		dynamic.test("HALLO$someSting")
		dynamic.test(123)
		dynamic.test((Long)123)
		
		then:
		['test1', 'Hallo', 'HALLOasd', 'Integer123', 'Long123'] == vals
	}
	
	@Test
	def 'test create dynamic getter and setter'() {
		setup:
		def plugin = new ScriptPluginHelper()
		def setVal = false
		
		when:
		def dynamic = plugin.createDynamic {
			getter getter: { 'anyVal' }
			boolGetter boolGetter: { true }
			setter setter: {boolean b -> setVal = b }
		}
		dynamic.setter=true
		
		then:
		setVal
		dynamic.boolGetter
		'anyVal' == dynamic.getter
	}
	
	
	@Test
	def 'test create dynamic with interface'() {
		setup:
		def plugin = new ScriptPluginHelper()
		def vals = []
		
		def obj = plugin.createDynamic {
			
			method anyMethod: { vals << it }
		}
		
		when:
		obj.anyMethod(321)
		
		AnyClass anyObj = obj as AnyClass
		def result = anyObj.anyMethod('test')
		anyObj.anyMethod(123)
		
		then:
		result == null
		[321, 'test'] == vals
		final MissingMethodException exception = thrown()
	}
	
	@Test
	def 'test plugin with createTask'() {
		setup:
		def registry = new PluginRegistry()
		def plugin = new ScriptPluginHelper(registry)
		def vals = []
		
		plugin.create('testPlugin') {
			
			return plugin.createDynamic { 
				method doStuff: { Closure closure ->
					
					plugin.createTask {
						
						def name = null
					
						closure.delegate = plugin.createDynamic {
							getter input: { input }
							method name: { name = it }
						}
						closure.resolveStrategy = Closure.DELEGATE_FIRST
						
						closure.call()
						
						output = "Hello $name"
					}
				}
			}
		}
		
		when:
		Task task = FlowDSL.createFlow {
			plugins { register registry.create('testPlugin') }
			input { 'candy' }
			testPlugin.doStuff {
				name "andy$input"
			}
			
		}
		
		task.call()
		
		then:
		'Hello andycandy' == task.output
	}
	
	interface AnyClass {
		
		void anyMethod(String s)
		
	}
}
