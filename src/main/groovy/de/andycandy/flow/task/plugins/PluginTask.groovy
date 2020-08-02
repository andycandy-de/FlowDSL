package de.andycandy.flow.task.plugins

import de.andycandy.flow.FlowPlugin
import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.TaskUtil
import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [PluginTaskDelegate])
class PluginTask extends AutoCleanTask implements PluginTaskDelegate {
	
	FlowTask flowTask
	
	Closure closure
	
	List<PluginRegister> pluginRegisterList

	@Override
	public void callWithClean() {
		
		TaskUtil.passInputToOutput(this)
		
		closure.delegate = this.toProtectedPluginTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		pluginRegisterList = []
		
		closure.call()
		
		pluginRegisterList.each {
			flowTask.plugins[it.name] = it.flowPlugin
			it.flowPlugin.flowTask = flowTask
		}
	}
	
	@Protect
	@Override
	public PluginRegisterName register(FlowPlugin flowPlugin) {
		
		PluginRegister pluginRegister = new PluginRegister()
		pluginRegister.name = flowPlugin.name
		pluginRegister.flowPlugin = flowPlugin
		
		pluginRegisterList << pluginRegister
		
		return [withName : { String name -> pluginRegister.name = name}] as PluginRegisterName;
	}
}

class PluginRegister {
	
	String name
	
	FlowPlugin flowPlugin
}