package de.andycandy.flow.task.for_each

import de.andycandy.flow.task.flow.FlowTask
import de.andycandy.flow.task.flow.FlowTaskDelegate
import de.andycandy.protect_me.ast.Protect

@Protect(classes=[ForEachFlowTaskDelegate])
class ForEachFlowTask extends FlowTask implements ForEachFlowTaskDelegate {
	
	void mapValue() {
		task(new MapValueTask())
	}
	
	void mapKey() {
		task(new MapKeyTask())
	}
	
	@Override
	public void toMap(Closure closure) {
		
		ToMapTask toMapTask = new ToMapTask()
		
		toMapTask.closure = closure
		
		task(toMapTask)
	}
	
	@Override
	protected FlowTaskDelegate getFlowTaskDelegate() {
		this.toProtectedForEachFlowTaskDelegate()
	}
}
