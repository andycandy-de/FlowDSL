package de.andycandy.flow.task.code

interface CodeTaskDelegate {
	
	Object getInput()
	
	boolean hasInput()
	
	void setOutput(Object object)
	
	Object getOutput()
	
	void clearOutput()
	
	void passInputToOutput()
}
