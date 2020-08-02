package de.andycandy.flow.task.code

import static de.andycandy.flow.task.TaskUtil.passInputToOutput

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.TaskUtil
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [CodeTaskDelegate])
class CodeTask extends AutoCleanTask implements CodeTaskDelegate {

	Closure closure
	
	@Override
	public void callWithClean() {
		
		closure.delegate = this.toProtectedCodeTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
	}
	
	@Override
	public void passInputToOutput() {
		passInputToOutput(this)
	}
}
