package de.andycandy.flow.plugin.io

import de.andycandy.flow.task.Task
import de.andycandy.flow.task.TaskUtil

class ReadFileTask extends Task {
	
	Closure closure
	
	File file

	@Override
	public void call() {
		
		closure.call()
		
		output = file.text
	}
	
	void setFile(String string) {
		this.file = new File(string)
	}
	
	void setClosure(Closure closure) {
		
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		this.closure = closure
		
	}
}
