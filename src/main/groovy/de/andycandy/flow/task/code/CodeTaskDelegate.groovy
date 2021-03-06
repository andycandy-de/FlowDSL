package de.andycandy.flow.task.code

interface CodeTaskDelegate {
	
	boolean hasInput()
	
	Object getInput()
	
	boolean hasOutput()
	
	Object getOutput()
	
	void setOutput(Object output)
	
	void clearOutput()
	
	void passInputToOutput()
}
