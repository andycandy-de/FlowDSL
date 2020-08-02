package de.andycandy.flow.task

import de.andycandy.flow.task.for_each.CollectionEntryInput
import de.andycandy.flow.task.for_each.MapEntryInput

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
	
	static boolean isCollection(Object object) {
		return isInstanceOf(Collection, object)
	}
	
	static boolean isMap(Object object) {
		return isInstanceOf(Map, object)
	}
	
	static boolean isMapEntryInput(Object object) {
		return isInstanceOf(MapEntryInput, object)
	}
	
	static boolean isCollectionEntryInput(Object object) {
		return isInstanceOf(CollectionEntryInput, object)
	}
	
	static boolean isInstanceOf(Class clazz, Object object) {
		return clazz.isInstance(object)
	}
	
}
