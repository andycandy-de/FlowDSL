package de.andycandy.flow.task.map

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.FlowDSL
import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [MapTaskDelegate])
class MapTask extends AutoCleanTask implements MapTaskDelegate {

	Closure closure

	@Override
	public void callWithClean() {

		if (isCollection(input)) {
			mapForCollection()
		}
		else {
			mapForSingle()
		}
	}

	void mapForCollection() {
		
		FlowTask flowTask = FlowDSL.createFlow { 
			
			forEach {
				
				mapValue()
				
				map(closure)
			}
		}
		
		passInputToInput(this, flowTask)
		flowTask.call()
		passOutputToOutput(flowTask, this)
	}

	void mapForSingle() {
		
		closure.delegate = this.toProtectedMapTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST

		output = closure.call()
	}
}
