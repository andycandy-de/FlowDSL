package de.andycandy.flow

import de.andycandy.flow.task.StepTask

interface FlowPlugin {
	
	String getName()
	
	void setStepTask(StepTask stepTask)
	
	FlowPlugin createInstance()
}
