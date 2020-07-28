package de.andycandy.flow.plugin.io

import de.andycandy.flow.task.Task

class LSTask extends Task {
	
	Closure closure
	
	List<File> dirs = []

	@Override
	public void call() {
		
		closure.call()
		
		List<File> result = []
		
		dirs.each { dir -> dir.listFiles().each { result << it } }
		
		output = result
	}
	
	void dir(String dir) {
		dirs << new File(dir)
	}
	
	void setClosure(Closure closure) {
		
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		this.closure = closure
	}
}
