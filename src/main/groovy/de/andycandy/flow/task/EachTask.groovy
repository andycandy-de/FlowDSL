package de.andycandy.flow.task

class EachTask extends Task {
	
	Task task
	
	@Override
	public void call() {
		
		task.context = context
		output = []
		
		if (input instanceof Collection) {
			
			callForInput { index, input ->
				
				CollectionEntryInput collectionEntryInput = new CollectionEntryInput()
				collectionEntryInput.index = index
				collectionEntryInput.value = input
				collectionEntryInput
			}
		} else if (input instanceof Map) {
			
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
	}
}
