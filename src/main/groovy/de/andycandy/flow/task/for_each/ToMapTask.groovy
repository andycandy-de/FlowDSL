package de.andycandy.flow.task.for_each

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [ToMapTaskDelegate])
class ToMapTask extends AutoCleanTask implements ToMapTaskDelegate {

	Closure closure
	
	MapEntry mapEntryOutput
	
	private boolean keyDefined = false
	
	private boolean valueDefined = false
	
	@Override
	public void callWithClean() {
		
		closure.delegate = this.toProtectedToMapTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		mapEntryOutput = new MapEntry()
		
		closure.call()
		
		if (!keyDefined || !valueDefined) {
			throw new IllegalStateException('Key and Value must be defined!')
		}
		
		output = mapEntryOutput
	}
	
	@Override
	public void key(Object key) {
		
		if (keyDefined) {
			throw new IllegalStateException('It\'s not possible to define multiple keys!')
		}
		
		mapEntryOutput.key = key
		keyDefined = true
	}

	@Override
	public void value(Object value) {
		
		if (valueDefined) {
			throw new IllegalStateException('It\'s not possible to define multiple values!')
		}
		
		mapEntryOutput.value = value
		valueDefined = true
	}

}
