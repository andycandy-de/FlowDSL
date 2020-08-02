package de.andycandy.flow.task.context

interface ContextTaskDelegate {
	
	boolean hasInput()
	
	Object getInput()
	
	Object propertyMissing(String name, Object value)
	
	Object propertyMissing(String name)
}
