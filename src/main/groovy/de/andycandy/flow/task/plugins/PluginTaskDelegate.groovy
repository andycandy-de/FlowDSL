package de.andycandy.flow.task.plugins

import de.andycandy.flow.FlowPlugin

interface PluginTaskDelegate {
	
	PluginRegisterName register(FlowPlugin flowPlugin)
	
}
