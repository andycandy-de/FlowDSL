package de.andycandy.flow.task.flatten

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.TaskUtil

class FlattenTask extends AutoCleanTask {
	
	int deep = 1

	@Override
	public void callWithClean() {		
		flatten(input, deep)
	}
	
	void setDeep(int deep) {
		
		if (deep < 1) {
			throw new IllegalStateException('Deep cannot be lower than 1')
		}
		
		this.deep = deep
	}
	
	void flatten(Object input, int deep) {
		
		if (deep == 0) {
			append(input)
			return
		}
		
		if (!isCollection(input)) {
			throw new IllegalStateException('Input must be an instance of Collection')
		}
		
		input.each { flatten(it, deep - 1) }
	}
	
	void append(Object input) {
		
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
		if (!isList(output)) {
			throw new IllegalStateException('Input types are incompatible! Input collection must contain only collections or only lists!')
		}
		output += collection
	}
	
	void appendMap(Map map) {
		
		if (!hasOutput()) {
			output = [:]
		}
		if (!isMap(output)) {
			throw new IllegalStateException('Input types are incompatible! Input collection must contain only collections or only lists!')
		}
		output += map
	}
}
