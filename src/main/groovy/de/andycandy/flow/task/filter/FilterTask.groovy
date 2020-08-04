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
	
	private boolean onlySingle = false
	
	@Override
	public void callWithClean() {
		
		if (onlySingle) {
			filterForSingle()
		} else if (isCollection(input)) {
			filterForCollection()
		}
		else if (isMap(input)) {
			filterForMap()
		}
		else {
			filterForSingle()
		}
	}

	void filterForCollection() {
		
		FilterTask filterTask = new FilterTask()
		filterTask.closure = closure
		filterTask.onlySingle = true
		
		FlowTask flowTask = FlowDSL.createFlow { 
			
			forEach {
				
				mapValue()
				
				task filterTask
			}
		}
		
		passInputToInput(this, flowTask)
		flowTask.call()
		passOutputToOutput(flowTask, this)
	}
	
	void filterForMap() {
		
		FilterTask filterTask = new FilterTask()
		filterTask.closure = closure
		filterTask.onlySingle = true
		
		FlowTask flowTask = FlowDSL.createFlow {
			
			forEach {				
				task filterTask
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
