package de.andycandy.flow.task

class FilterTask extends Task {
	
	Closure closure

	@Override
	public void call() {
		
		if (closure.call(input)) {
			output = input
		}
		else {
			clearOutput()
		}
	}
	
	def setClosure(Closure closure) {
		
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		this.closure = closure		
	}
}
