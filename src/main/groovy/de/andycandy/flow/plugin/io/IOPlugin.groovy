package de.andycandy.flow.plugin.io

import de.andycandy.flow.FlowDSL
import de.andycandy.flow.FlowPlugin
import de.andycandy.flow.task.StepTask

class IOPlugin implements FlowPlugin {
	
	StepTask stepTask
	
	void ls(Closure closure) {
		
		LSTask lsTask = new LSTask()
		lsTask.closure = closure
		stepTask.tasks << lsTask
	}
	
	void write(Closure closure) {
		
		WriteFileTask writeFileTask = new WriteFileTask()
		writeFileTask.closure = closure
		stepTask.tasks << writeFileTask
	}
	
	void read(Closure closure) {
		
		ReadFileTask readFileTask = new ReadFileTask()
		readFileTask.closure = closure
		stepTask.tasks << readFileTask
	}
	
	String getName() {
		"io"
	}

	FlowPlugin createInstance() {
		return new IOPlugin()
	}
	
	static create() {
		return new IOPlugin()
	}
}
