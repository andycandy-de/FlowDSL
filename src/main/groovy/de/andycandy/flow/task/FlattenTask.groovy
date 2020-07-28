package de.andycandy.flow.task

class FlattenTask extends Task {
	
	int deep = 1

	@Override
	public void call() {
		
		if (!(input instanceof Collection)) {
			throw new IllegalStateException('Input must be an instance of Collection')
		}
		
		flatten(input, deep)
	}
	
	def flatten(input, int deep) {
		
		if (deep == 0) {
			append(input)
			return
		}
		
		if (!(input instanceof Collection) && !(input instanceof Map)) {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
		
		input.each { flatten(it, deep - 1) }
	}
	
	def append(input) {
		
		if (input instanceof Collection) {
			appendList(input)
		}
		else if (input instanceof Map) {
			appendMap(input)
		}
		else {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
	}
	
	def appendList(Collection collection) {
		if (!hasOutput()) {
			output = []
		}
		if (!output instanceof List) {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
		output += collection
	}
	
	def appendMap(Map map) {
		if (!hasOutput()) {
			output = [:]
		}
		if (!output instanceof Map) {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
		output += map
	}
}
