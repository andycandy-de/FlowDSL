package de.andycandy.flow.task

class ConditionalTask extends Task {
	
	Closure condition
	
	Task task

	@Override
	public void call() {
		
		task.context = context
		
		if (condition.call()) {
			
			TaskUtil.passInputToInput(this, task)
			
			task.call()
			
			TaskUtil.passOutputToOutput(task, this)
		}
		else {
			
			TaskUtil.passInputToOutput(this)
		}
	}
	
	def setCondition(Closure closure) {
		
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		this.condition = closure
	}
}
