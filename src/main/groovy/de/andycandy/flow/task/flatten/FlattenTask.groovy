package de.andycandy.flow.task.flatten

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.task.AutoCleanTask

class FlattenTask extends AutoCleanTask {
	
	int deep = 1

	@Override
	public void callWithClean() {
		
		if (!isCollection(input)) {
			throw new IllegalStateException('Input must be an instance of Collection')
		}
		
		flatten(input, deep)
	}
	
	void flatten(input, int deep) {
		
		if (deep == 0) {
			append(input)
			return
		}
		
		if (!isCollection(input) && !isMap(input)) {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
		
		input.each { flatten(it, deep - 1) }
	}
	
	void append(input) {
		
		if (isCollection(input)) {
			appendList(input)
		}
		else if (isMap(input)) {
			appendMap(input)
		}
		else {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
	}
	
	void appendList(Collection collection) {
		if (!hasOutput()) {
			output = []
		}
		if (!output instanceof List) {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
		output += collection
	}
	
	void appendMap(Map map) {
		if (!hasOutput()) {
			output = [:]
		}
		if (!output instanceof Map) {
			throw new IllegalStateException('Input must contain Collections or Maps')
		}
		output += map
	}
}
