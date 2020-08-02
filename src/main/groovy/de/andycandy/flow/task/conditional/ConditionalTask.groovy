package de.andycandy.flow.task.conditional

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.Task
import de.andycandy.flow.task.TaskUtil
import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [ConditionalTaskDelegate])
class ConditionalTask extends AutoCleanTask implements ConditionalTaskDelegate {
	
	Closure condition
	
	Task task

	@Override
	public void callWithClean() {
		
		condition.delegate = this.toProtectedConditionalTaskDelegate()
		condition.resolveStrategy = Closure.DELEGATE_FIRST
		
		if (condition.call()) {
			passInputToInput(this, task)
			task.call()
			passOutputToOutput(task, this)
		}
		else {
			passInputToOutput(this)
		}
	}
}
