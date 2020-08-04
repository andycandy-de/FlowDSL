package de.andycandy.flow.task.for_each

interface ToMapTaskDelegate {
	
	boolean hasInput()
	
	Object getInput()
	
	void key(Object key)
	
	void value(Object value)
}
