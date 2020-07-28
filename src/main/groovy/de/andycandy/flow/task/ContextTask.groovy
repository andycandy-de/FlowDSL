package de.andycandy.flow.task

class ContextTask extends Task {
	
	Closure closure

	@Override
	public void call() {
		
		TaskUtil.passInputToOutput(this)
		
		closure.delegate = context
		closure.call()
	}
	
	def setClosure(Closure closure) {
		
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		this.closure = closure
	}
	
}
