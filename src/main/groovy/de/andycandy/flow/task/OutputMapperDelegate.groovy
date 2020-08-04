package de.andycandy.flow.task

interface OutputMapperDelegate {
	
	boolean hasInput()
	
	Object getInput()
	
	boolean hasOutput()
	
	Object getOutput()
	
	void setOutput(Object output)
	
	void clearOutput()
}
