package de.andycandy.flow.task

class CodeTask extends Task {
	
	Closure closure

	@Override
	public void call() {
		closure.call()
	}
	
	void passInputToOutput() {
		TaskUtil.passInputToOutput(this)
	}
	
	def setClosure(Closure closure) {
		
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		this.closure = closure
	}
}
