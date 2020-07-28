package de.andycandy.flow.task

class Context {

	def properties = [:]

	def propertyMissing(String name, value) {
		
		if (properties.containsKey(name)) {
			throw new ReadOnlyPropertyException("value", Context.class)
		}
		
		properties[name] = value
	}

	def propertyMissing(String name) {
		
		if (!properties.containsKey(name)) {
			throw new MissingPropertyException("value", Context.class)
		}

		properties[name]
	}
}
