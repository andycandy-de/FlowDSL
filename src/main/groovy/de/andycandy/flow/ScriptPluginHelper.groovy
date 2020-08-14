package de.andycandy.flow

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.Task
import de.andycandy.flow.task.TaskUtil
import de.andycandy.flow.task.flow.TaskExecutor
import de.andycandy.protect_me.ast.Protect
import groovy.transform.NamedParam
import groovy.transform.NamedParams
import groovy.transform.TupleConstructor

@TupleConstructor
class ScriptPluginHelper {
	
	PluginRegistry pluginRegistry
	
	void create(String name, Closure closure) {
		
		pluginRegistry.register(name) { 
			
			def delegateTarget = closure()
			
			def getName = { name }
			
			def createDelegate = { TaskExecutor taskExecutor ->
				return new DelegateHelper(delegateTarget: delegateTarget, taskExecutor: taskExecutor)
			}
			
			return [getName: getName, createDelegate: createDelegate] as FlowPlugin	
		}
	}
	
	Task createTask(@DelegatesTo(value=Task, strategy=Closure.DELEGATE_FIRST)Closure closure) {
		return new ScriptTask(closure)
	}
	
	Object createDynamic(@DelegatesTo(value = DynamicCreatorDelegate, strategy=Closure.DELEGATE_FIRST) Closure closure) {
		return DynamicCreator.createDynamic(closure)
	}
	
	Task passInputToOutput() {
		return new PassingTask()
	}
}

class PassingTask extends AutoCleanTask {

	@Override
	public void callWithClean() {
		TaskUtil.passInputToOutput(this)
	}
}

@TupleConstructor
class ScriptTask extends AutoCleanTask {

	Closure closure
	
	@Override
	public void callWithClean() {
		
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure()
	}
}


@TupleConstructor
class DelegateHelper {
	
	private Object delegateTarget
	
	private TaskExecutor taskExecutor
	
	def invokeMethod(String name, Object args) {
		taskExecutor.task(delegateTarget.invokeMethod(name, args))
	}
}

@Protect(classes = [DynamicCreatorDelegate])
class DynamicCreator implements DynamicCreatorDelegate {

	def obj = new Object()
	
	@Override
	public void method(String name, Closure closure) {
		obj.metaClass."$name" = closure
	}
	
	@Override
	void method(Map map) {
		executeWithMap(map) { String name, Closure closure ->
			method(name, closure)
		}
	}
	
	@Override
	public void getter(String name, Closure closure) {
		method(MetaProperty.getGetterName(name, Object), closure)
	}
	
	@Override
	public void getter(Map map) {
		executeWithMap(map) { String name, Closure closure ->
			getter(name, closure)
		}
	}
	
	@Override
	public void boolGetter(String name, Closure closure) {
		method(MetaProperty.getGetterName(name, Boolean), closure)
	}
	
	@Override
	public void boolGetter(Map map) {
		executeWithMap(map) { String name, Closure closure ->
			boolGetter(name, closure)
		}
	}
	
	@Override
	public void setter(String name, Closure closure) {
		method(MetaProperty.getSetterName(name), closure)
	}
	
	@Override
	public void setter(Map map) {
		executeWithMap(map) { String name, Closure closure ->
			setter(name, closure)
		}
	}
	
	Object createDymanic() {
		return obj
	}
	
	private executeWithMap(Map map, Closure closure) {
		
		if (map.size() != 1) {
			throw new IllegalArgumentException('Map must have exactly one argument!')
		}
		
		map.entrySet().first().with {
			closure(it.key, it.value)
		}
	}
	
	static Object createDynamic(@DelegatesTo(value = DynamicCreatorDelegate, strategy=Closure.DELEGATE_FIRST) Closure closure) {
		
		DynamicCreator dynamicCreator = new DynamicCreator()
		
		closure.delegate = dynamicCreator.toProtectedDynamicCreatorDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		return dynamicCreator.createDymanic()
	}
}

interface DynamicCreatorDelegate {
	
	void method(String name, Closure closure)
	
	void method(@NamedParams(@NamedParam(type=Closure)) Map map)
	
	void getter(String name, Closure closure)
	
	void getter(@NamedParams(@NamedParam(type=Closure)) Map map)
	
	void boolGetter(String name, Closure closure)
	
	void boolGetter(@NamedParams(@NamedParam(type=Closure)) Map map)
	
	void setter(String name, Closure closure)
	
	void setter(@NamedParams(@NamedParam(type=Closure)) Map map)
}