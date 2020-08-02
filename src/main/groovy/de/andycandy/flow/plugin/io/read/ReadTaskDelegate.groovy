package de.andycandy.flow.plugin.io.read

interface ReadTaskDelegate {
	
	Object getInput()
	
	boolean hasInput()
	
	void file(String file)
	
	void file(File file)
}
