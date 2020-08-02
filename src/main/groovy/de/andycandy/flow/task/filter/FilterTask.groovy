package de.andycandy.flow.task.filter

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.FlowDSL
import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.TaskUtil
import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.flow.task.for_each.ForEachTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [FilterTaskDelegate])
class FilterTask extends AutoCleanTask implements FilterTaskDelegate {

	Closure closure
	
	@Override
	public void callWithClean() {
		
		if (isCollection(input)) {
			filterForCollection()
		}
		else {
			filterForSingle()
		}
	}

	void filterForCollection() {
		
		FlowTask flowTask = FlowDSL.createFlow { 
			
			forEach {
				
				mapValue()
				
				filter(closure)
			}
		}
		
		passInputToInput(this, flowTask)
		flowTask.call()
		passOutputToOutput(flowTask, this)
	}

	void filterForSingle() {
		
		closure.delegate = this.toProtectedFilterTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST

		if (closure.call()) {
			passInputToOutput(this)
		}
	}
}
