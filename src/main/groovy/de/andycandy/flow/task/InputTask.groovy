package de.andycandy.flow.task

class InputTask extends Task {
	
	Closure closure

	@Override
	public void call() {
		output = closure.call()
	}
}
