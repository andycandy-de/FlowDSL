package de.andycandy.flow.plugin.io.write

interface WriteTaskDelegate {
	
	Object getInput()
	
	void override()
	
	void file(String file)
	
	void file(File file)
}
