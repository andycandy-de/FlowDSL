package de.andycandy.flow.task

abstract class Task {
	
	def input;
	
	def output;
	
	private boolean hasInput = false
	
	private boolean hasOutput = false
	
	void setInput(input) {
		hasInput = true
		this.input = input
	}
	
	def getInput() {
		if (!hasInput) throw new IllegalStateException('No input defined!')
			
		input
	}
	
	void setOutput(output) {
		hasOutput = true
		this.output = output
	}
	
	def getOutput() {
		if (!hasOutput) throw new IllegalStateException('No output defined!')
			
		output
	}
	
	boolean hasInput() {
		hasInput
	}
	
	boolean hasOutput() {
		hasOutput
	}
	
	void clearInput() {
		hasInput = false
		
		input = null
	}
	
	void clearOutput() {
		hasOutput = false
		
		output = null
	}
	
	abstract void call()
}
