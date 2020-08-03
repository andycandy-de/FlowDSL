package de.andycandy.flow.plugin.io.read

import java.nio.charset.Charset

interface ReadTaskDelegate {
	
	Object getInput()
	
	boolean hasInput()
	
	void file(String file)
	
	void file(File file)
	
	void charset(String string)
	
	void charset(Charset charset)
}
