package de.andycandy.flow.task.map

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.FlowDSL
import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [MapTaskDelegate])
class MapTask extends AutoCleanTask implements MapTaskDelegate {

	Closure closure
	
	private boolean onlySingle = false

	@Override
	public void callWithClean() {
		
		if (onlySingle) {
			mapForSingle()
		} else if (isCollection(input)) {
			mapForCollection()
		}
		else if (isMap(input)) {
			mapForMap()
		}
		else {
			mapForSingle()
		}
	}

	void mapForCollection() {
		
		MapTask mapTask = new MapTask()
		mapTask.closure = closure
		mapTask.onlySingle = true
		
		FlowTask flowTask = FlowDSL.createFlow { 
			
			forEach {
				
				mapValue()
				
				task mapTask
			}
		}
		
		passInputToInput(this, flowTask)
		flowTask.call()
		passOutputToOutput(flowTask, this)
	}
	
	void mapForMap() {
		
		MapTask mapTask = new MapTask()
		mapTask.closure = closure
		mapTask.onlySingle = true
		
		FlowTask flowTask = FlowDSL.createFlow {
			
			forEach {
				task mapTask
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
