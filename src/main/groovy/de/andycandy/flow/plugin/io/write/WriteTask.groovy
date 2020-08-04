package de.andycandy.flow.plugin.io.write

import static de.andycandy.flow.task.TaskUtil.passInputToOutput

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [WriteTaskDelegate])
class WriteTask extends AutoCleanTask implements WriteTaskDelegate {

	Closure closure
	
	boolean override = false
	
	File writeFile
	
	Charset writeCharset
	
	@Override
	public void callWithClean() {
		
		passInputToOutput(this)
		
		closure.delegate = this.toProtectedWriteTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		File parent = writeFile.parentFile
		if (parent == null) {
			parent = new File('.')
		}
		
		if (parent.exists() && !parent.isDirectory()) {
			throw new IllegalStateException("'${parent}' is not a directory!")
		}
		if (!parent.exists() && !tryCreateDirs(parent)) {
			throw new IllegalStateException("Cannot create directory '${parent}'!")
		}
		if (writeFile.exists() && writeFile.isFile()) {
			if (override) {
				writeFile.delete()
			}
			else {
				throw new IllegalStateException("The file '${writeFile}' already exists!")
			}
		}
		
		def writeCharset = (this.writeCharset != null) ? this.writeCharset : StandardCharsets.UTF_8
		writeFile.newWriter(writeCharset.toString()).withCloseable { 
			it << input
		}
	}
	
	@Override
	public void override() {
		override = true
	}

	@Override
	public void file(String file) {
		this.file(new File(file))
	}

	@Override
	public void file(File file) {
		
		if (writeFile != null) {
			throw new IllegalStateException('It not allowed to define multiple files!')
		}
		
		writeFile = file
	}
	
	@Override
	public void charset(String string) {
		this.charset(Charset.forName(string))
	}

	@Override
	public void charset(Charset charset) {
		
		if (writeCharset != null) {
			throw new IllegalStateException('It\'s not allowed to define multiple charsets!')
		}
		
		writeCharset = charset
	}
	
	private boolean tryCreateDirs(File file) {
		try {
			return file.mkdirs()
		}
		catch (FileNotFoundException e) { // Linux
			return false
		}
	}
}
