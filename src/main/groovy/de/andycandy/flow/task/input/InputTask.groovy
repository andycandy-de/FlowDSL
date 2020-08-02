package de.andycandy.flow.task.input

import de.andycandy.flow.task.AutoCleanTask

class InputTask extends AutoCleanTask {

	Closure closure
	
	@Override
	public void callWithClean() {
		output = closure.call()
	}
}
