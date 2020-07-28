package de.andycandy.flow.task

class StepTask extends Task {

	List<Task> tasks = []
	
	@Override
	public void call() {
		
		TaskUtil.passInputToOutput(this)
		
		 tasks.each {
			 
			 it.context = context
			 
			 TaskUtil.passOutputToInput(this, it)
			 
			 it.call()
			 
			 TaskUtil.passOutputToOutput(it, this)
		 }
	}
}
