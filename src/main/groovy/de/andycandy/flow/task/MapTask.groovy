package de.andycandy.flow.task

class MapTask extends Task {
	
	Closure closure

	@Override
	public void call() {
		output = closure.call(input)
	}
	
	def setClosure(Closure closure) {
		
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		this.closure = closure		
	}
}
