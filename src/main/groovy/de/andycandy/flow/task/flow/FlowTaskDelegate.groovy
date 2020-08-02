package de.andycandy.flow.task.flow

import de.andycandy.flow.task.code.CodeTaskDelegate
import de.andycandy.flow.task.conditional.ConditionalTask
import de.andycandy.flow.task.context.ContextTask
import de.andycandy.flow.task.context.ContextTaskDelegate
import de.andycandy.flow.task.filter.FilterTaskDelegate
import de.andycandy.flow.task.map.MapTaskDelegate
import de.andycandy.flow.task.plugins.PluginTaskDelegate
import de.andycandy.flow.task.value_holder.ValueHolder
import de.andycandy.flow.task.value_holder.ValueHolderTaskDelegate

interface FlowTaskDelegate {
	
	void plugins(@DelegatesTo(value = PluginTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void forEach(@DelegatesTo(value = FlowTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void map(@DelegatesTo(value = MapTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void filter(@DelegatesTo(value = FilterTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
		
	void code(@DelegatesTo(value = CodeTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void flow(@DelegatesTo(value = FlowTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void input(Closure closure)
	
	void conditional(@DelegatesTo(value = ConditionalTask, strategy = Closure.DELEGATE_FIRST) Closure condition, @DelegatesTo(value = FlowTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
		
	void mapValue()
	
	void mapKey()
	
	void flatten()
	
	void flatten(int deep)
	
	ContextTask context(@DelegatesTo(value = ContextTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	ValueHolder valueHolder()
	
	ValueHolder valueHolder(@DelegatesTo(value = ValueHolderTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	Object propertyMissing(String name)
}
