package de.andycandy.flow.task

class TaskUtil {
	
	
	static void passInputToOutput(Task task) {
		passInputToOutput(task, task)
	}
	
	static void passInputToOutput(Task from, Task to) {
		if (from.hasInput()) {
			to.output = from.input
		}
		else {
			to.clearOutput()
		}
	}
	
	static void passOutputToInput(Task from, Task to) {
		if (from.hasOutput()) {
			to.input = from.output
		}
		else {
			to.clearInput()
		}
	}
	
	static void passInputToInput(Task from, Task to) {
		if (from.hasInput()) {
			to.input = from.input
		}
		else {
			to.clearInput()
		}
	}
	
	static void passOutputToOutput(Task from, Task to) {
		if (from.hasOutput()) {
			to.output = from.output
		}
		else {
			to.clearOutput()
		}
	}
	
}
