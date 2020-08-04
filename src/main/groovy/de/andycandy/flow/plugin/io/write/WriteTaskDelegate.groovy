package de.andycandy.flow.plugin.io.write

import java.nio.charset.Charset

interface WriteTaskDelegate {
	
	Object getInput()
	
	void override()
	
	void file(String file)
	
	void file(File file)
	
	void charset(String string)
	
	void charset(Charset charset)
}
