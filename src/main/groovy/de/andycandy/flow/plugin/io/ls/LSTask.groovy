package de.andycandy.flow.plugin.io.ls

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [LSTaskDelegate])
class LSTask extends AutoCleanTask implements LSTaskDelegate {

	File lsDir

	Closure closure

	@Override
	public void callWithClean() {
		
		closure.delegate = this.toProtectedLSTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		output = lsDir.listFiles().toList().sort()
	}

	@Override
	public void dir(String dir) {
		this.dir(new File(dir))
	}

	@Override
	public void dir(File dir) {
		
		if (lsDir != null) {
			throw new IllegalStateException('It not allowed to define multiple dirs!')
		}
		
		lsDir = dir
	}
}
