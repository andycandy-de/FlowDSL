package de.andycandy.flow.task.for_each

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.task.AutoCleanTask

class MapKeyTask extends AutoCleanTask {

	@Override
	public void callWithClean() {
		
		if(!isMapEntryInput(input)) {
			throw new IllegalArgumentException("Input type '${input.class.simpleName}' is not instance of MapEntryInput")
		}
		
		output = input.key
	}
}
