package de.andycandy.flow.task.for_each

import static de.andycandy.flow.task.TaskUtil.*

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.Task
import de.andycandy.flow.task.TaskUtil

class ForEachTask extends AutoCleanTask {
	
	Task task

	@Override
	public void callWithClean() {
		
		if (isCollection(input)) {
			
			callForInput { index, input ->
				
				CollectionEntryInput collectionEntryInput = new CollectionEntryInput()
				collectionEntryInput.index = index
				collectionEntryInput.value = input
				collectionEntryInput
			}
		} else if (isMap(input)) {
			
			callForInput { index, input ->
				
				MapEntryInput mapEntryInput = new MapEntryInput()
				mapEntryInput.index = index
				mapEntryInput.key = input.key
				mapEntryInput.value = input.value
				mapEntryInput
			}
		}
		else {
			throw new IllegalStateException('Input must be an instance of Collection or Map')
		}
	}
	
	void callForInput(Closure closure) {
		
		output = []
		
		int index = 0
		input.each {
			
			task.input = closure.call(index, it)
			
			task.clearOutput()
			task.call()
			
			if (task.hasOutput()) {
				output << task.output
			}
			++index
		}
		
		correctOutput()
	}
	
	void correctOutput() {
		if (output.isEmpty()) {
			return
		}
		
		if (isCollectionEntryInput(output[0])) {
			toList()
		}
		else if (isMapEntry(output[0])) {
			toMap()
		}
	}
	
	void toList() {
		
		def list = []
		output.each { list << it.value }
		
		output = list
	}
	
	void toMap() {
		
		def map = [:]
		output.each { map[it.key] = it.value }
		
		output = map
	}
}
