package de.andycandy.flow.task

abstract class AutoCleanTask extends Task {

	@Override
	public void call() {
		
		clearOutput()
		
		callWithClean()
		
		clearInput()
	}
	
	abstract void callWithClean();
}
