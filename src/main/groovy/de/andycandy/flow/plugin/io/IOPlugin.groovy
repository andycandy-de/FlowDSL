package de.andycandy.flow.plugin.io

import de.andycandy.flow.FlowPlugin
import de.andycandy.flow.plugin.io.ls.LSTask
import de.andycandy.flow.plugin.io.ls.LSTaskDelegate
import de.andycandy.flow.plugin.io.read.ReadTask
import de.andycandy.flow.plugin.io.write.WriteTask
import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.flow.task.flow.TaskExecutor
import de.andycandy.protect_me.ast.Protect
import groovy.transform.TupleConstructor

class IOPlugin implements FlowPlugin {
	
	@Override
	public String getName() {
		return 'io';
	}
	
	@Override
	public Object createDelegate(TaskExecutor taskExecutor) {
		createProtected(taskExecutor)
	}
	
	@Protect
	IOPluginDelegate createProtected(TaskExecutor taskExecutor) {
		PluginExecution pluginExecution = new PluginExecution()
		pluginExecution.taskExecutor = taskExecutor
		return pluginExecution
	}
	
	class PluginExecution implements IOPluginDelegate {
		
		private TaskExecutor taskExecutor
			
		@Override
		public void ls(Closure closure, Closure outputMapper = null) {
			
			LSTask lsTask = new LSTask()
			lsTask.closure = closure
			lsTask.outputMapper = outputMapper
			
			taskExecutor.task(lsTask)
		}
		
		@Override
		public void write(Closure closure) {
			
			WriteTask writeTask = new WriteTask()
			writeTask.closure = closure
			
			taskExecutor.task(writeTask)
		}
		
		@Override
		public void read(Closure closure, Closure outputMapper = null) {
			
			ReadTask readTask = new ReadTask()
			readTask.closure = closure
			readTask.outputMapper = outputMapper
			
			taskExecutor.task(readTask)
		}
	}
}
