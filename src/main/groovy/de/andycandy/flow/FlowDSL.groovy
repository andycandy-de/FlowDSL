package de.andycandy.flow

import de.andycandy.flow.task.StepTask

class FlowDSL {
	
	static StepTask flow(@DelegatesTo(value = StepTaskDSL, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		
		StepTaskDSL stepTaskDSL = new StepTaskDSL()
		
		return stepTaskDSL.createStepTask(closure)
	}
}
