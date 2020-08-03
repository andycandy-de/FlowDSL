package de.andycandy.flow.plugin.io.write

import static de.andycandy.flow.task.TaskUtil.passInputToOutput

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [WriteTaskDelegate])
class WriteTask extends AutoCleanTask implements WriteTaskDelegate {

	Closure closure
	
	boolean override = false
	
	File writeFile
	
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
		if (!parent.exists() && !parent.mkdirs()) {
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
		
		writeFile << input
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
}
