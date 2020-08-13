package de.andycandy.flow

import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.flow.task.flow.TaskExecutor

interface FlowPlugin {
	
	String getName()
	
	Object createDelegate(TaskExecutor taskExecutor)
}