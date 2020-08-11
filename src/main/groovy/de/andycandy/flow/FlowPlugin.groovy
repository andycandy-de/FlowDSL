package de.andycandy.flow

import de.andycandy.flow.task.flow.FlowTask

interface FlowPlugin {
	
	String getName()
	
	Object createDelegate(FlowTask flowTask)
}