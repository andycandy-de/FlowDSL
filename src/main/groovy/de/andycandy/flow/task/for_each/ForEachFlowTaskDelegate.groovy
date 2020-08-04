package de.andycandy.flow.task.for_each

import de.andycandy.flow.task.flow.FlowTaskDelegate

interface ForEachFlowTaskDelegate extends FlowTaskDelegate {
		
	void mapValue()
	
	void mapKey()
	
	void toMap(@DelegatesTo(value = ToMapTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
}
