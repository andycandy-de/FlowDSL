package de.andycandy.flow.task.context

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.TaskUtil
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [ContextTaskDelegate])
class ContextTask extends AutoCleanTask implements ContextTaskDelegate {
	
	Closure closure
	
	Map properties
	
	@Override
	public void callWithClean() {
		
		TaskUtil.passInputToOutput(this)
		
		closure.delegate = this.toProtectedContextTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		properties = [:]
		
		closure.call()
	}
	
	@Override
	public Object propertyMissing(String name, Object value) {
		
		if (properties.containsKey(name)) {
			throw new ReadOnlyPropertyException(value)
		}
		
		properties[name] = value
	}

	@Override
	public Object propertyMissing(String name) {
		
		if (!properties.containsKey(name)) {
			throw new MissingPropertyException(name)
		}

		properties[name]
	}

}
