package de.andycandy.flow

import de.andycandy.flow.task.flow.FlowTask

interface FlowPlugin {
	
	String getName()
	
	void setFlowTask(FlowTask flowTask)
}
