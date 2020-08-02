 package de.andycandy.flow.plugin.io

import de.andycandy.flow.FlowPlugin
import de.andycandy.flow.plugin.io.ls.LSTask
import de.andycandy.flow.plugin.io.ls.LSTaskDelegate
import de.andycandy.flow.plugin.io.read.ReadTask
import de.andycandy.flow.plugin.io.write.WriteTask
import de.andycandy.flow.task.flow.FlowTask

class IOPlugin implements FlowPlugin, IOPluginDelegate {

	FlowTask flowTask
	
	@Override
	public void ls(Closure closure) {
		
		LSTask lsTask = new LSTask()
		lsTask.closure = closure
		
		flowTask.executeTask(lsTask)
	}
	
	@Override
	public void write(Closure closure) {
		
		WriteTask writeTask = new WriteTask()
		writeTask.closure = closure
		
		flowTask.executeTask(writeTask)
	}
	
	@Override
	public void read(Closure closure) {

		ReadTask readTask = new ReadTask()
		readTask.closure = closure
		
		flowTask.executeTask(readTask)
	}
	
	@Override
	public String getName() {
		return 'io';
	}
	
	static IOPlugin create() {
		return new IOPlugin()
	}
}
