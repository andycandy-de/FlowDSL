package de.andycandy.flow

import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.flow.task.flow.FlowTaskDelegate

class FlowDSL {
	
	static FlowTask createFlow(@DelegatesTo(value = FlowTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		
		FlowTask stepTask = new FlowTask()		
		stepTask.closure = closure
		
		return stepTask
	}
}
