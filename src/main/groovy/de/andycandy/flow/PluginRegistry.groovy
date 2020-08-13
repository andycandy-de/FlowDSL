package de.andycandy.flow

class PluginRegistry {
	
	Map<String, Closure> pluginCreators = [:]
	
	void register(String name, Closure createPlugin) {
		
		if (pluginCreators[name] != null) {
			throw new IllegalStateException("There is already a plugin with the name ${name} registered!")
		}
		
		pluginCreators[name] = createPlugin
	}
	
	FlowPlugin create(String name) {
		
		if (pluginCreators[name] == null) {
			throw new IllegalStateException("There is no plugin with the name ${name} registered!")
		}
		
		return pluginCreators[name]()
	}
}
