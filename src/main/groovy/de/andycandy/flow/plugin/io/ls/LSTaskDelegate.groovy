package de.andycandy.flow.plugin.io.ls

interface LSTaskDelegate {
	
	boolean hasInput()
	
	Object getInput()
	
	void dir(String dir)
	
	void dir(File dir)
}
