package de.andycandy.flow.task.for_each

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.input.InputTask

class MapValueTask extends AutoCleanTask {

	@Override
	public void callWithClean() {
		
		if (!isMapEntryInput(input) && !isCollectionEntryInput(input)) {			
			throw new IllegalArgumentException("Input '${input}' is not instance of CollectionEntryInput or MapEntryInput")
		}
		
		output = input.value		
	}
	
	
}
