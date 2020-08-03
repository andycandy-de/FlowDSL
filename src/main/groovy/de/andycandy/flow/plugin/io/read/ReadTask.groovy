package de.andycandy.flow.plugin.io.read

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [ReadTaskDelegate])
class ReadTask extends AutoCleanTask implements ReadTaskDelegate {
	
	Closure closure
	
	File readFile
	
	Charset readCharset
	
	@Override
	public void callWithClean() {
		
		closure.delegate = this.toProtectedReadTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		def readCharset = (this.readCharset != null) ? this.readCharset : StandardCharsets.UTF_8
		output = readFile.getText(readCharset.toString())
	}
	
	@Override
	public void file(String file) {
		this.file(new File(file))
	}

	@Override
	public void file(File file) {
		
		if (readFile != null) {
			throw new IllegalStateException('It not allowed to define multiple files!')
		}
		
		readFile = file
	}

	@Override
	public void charset(String string) {
		charset(Charset.forName(string))
	}

	@Override
	public void charset(Charset charset) {
		
		if (readCharset != null) {
			throw new IllegalStateException('It not allowed to define multiple charsets!')
		}
		
		readCharset = charset
	}
}
