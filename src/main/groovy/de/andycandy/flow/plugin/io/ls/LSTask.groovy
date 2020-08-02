package de.andycandy.flow.plugin.io.ls

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [LSTaskDelegate])
class LSTask extends AutoCleanTask implements LSTaskDelegate {

	List<File> dirs = []

	Closure closure

	@Override
	public void callWithClean() {
		
		closure.delegate = this.toProtectedLSTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		List<File> result = []
		
		dirs.each { dir -> dir.listFiles().each { result << it } }
		
		output = result
	}

	@Override
	public void dir(String dir) {
		this.dir(new File(dir))
	}

	@Override
	public void dir(File dir) {
		dirs << dir
	}
}
