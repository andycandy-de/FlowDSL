package de.andycandy.flow.plugin.io.ls

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.OutputMapperDelegate
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [LSTaskDelegate, OutputMapperDelegate])
class LSTask extends AutoCleanTask implements LSTaskDelegate, OutputMapperDelegate {

	File lsDir

	Closure closure
	
	Closure outputMapper

	@Override
	public void callWithClean() {
		
		closure.delegate = this.toProtectedLSTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		output = lsDir.listFiles().toList().sort()
		
		if (outputMapper != null) {
			
			outputMapper.delegate = this.toProtectedOutputMapperDelegate()
			outputMapper.resolveStrategy = Closure.DELEGATE_FIRST
			
			outputMapper.call()
		}
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
