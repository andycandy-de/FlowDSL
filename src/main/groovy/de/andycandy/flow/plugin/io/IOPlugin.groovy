package de.andycandy.flow.plugin.io

import de.andycandy.flow.FlowPlugin
import de.andycandy.flow.plugin.io.ls.LSTask
import de.andycandy.flow.plugin.io.ls.LSTaskDelegate
import de.andycandy.flow.plugin.io.read.ReadTask
import de.andycandy.flow.plugin.io.write.WriteTask
import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.protect_me.ast.Protect
import groovy.transform.TupleConstructor

class IOPlugin implements FlowPlugin {
	
	@Override
	public String getName() {
		return 'io';
	}
	
	@Override
	public Object createDelegate(FlowTask flowTask) {
		Plugin.create(flowTask)
	}
	
	static class Plugin implements IOPluginDelegate {
		
		private FlowTask flowTask
			
		@Override
		public void ls(Closure closure, Closure outputMapper = null) {
			
			LSTask lsTask = new LSTask()
			lsTask.closure = closure
			lsTask.outputMapper = outputMapper
			
			flowTask.task(lsTask)
		}
		
		@Override
		public void write(Closure closure) {
			
			WriteTask writeTask = new WriteTask()
			writeTask.closure = closure
			
			flowTask.task(writeTask)
		}
		
		@Override
		public void read(Closure closure, Closure outputMapper = null) {
			
			ReadTask readTask = new ReadTask()
			readTask.closure = closure
			readTask.outputMapper = outputMapper
			
			flowTask.task(readTask)
		}
		
		@Protect
		static IOPluginDelegate create(FlowTask flowTask) {
			def plugin = new Plugin()
			plugin.flowTask = flowTask
			return plugin
		}
	}
}
