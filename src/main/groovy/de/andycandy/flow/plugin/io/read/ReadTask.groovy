package de.andycandy.flow.plugin.io.read

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [ReadTaskDelegate])
class ReadTask extends AutoCleanTask implements ReadTaskDelegate {
	
	Closure closure
	
	File readFile
	
	@Override
	public void callWithClean() {
		
		closure.delegate = this.toProtectedReadTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		output = readFile.text
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
}
