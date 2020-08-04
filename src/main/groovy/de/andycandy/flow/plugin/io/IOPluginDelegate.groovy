package de.andycandy.flow.plugin.io

import de.andycandy.flow.plugin.io.ls.LSTaskDelegate
import de.andycandy.flow.plugin.io.read.ReadTaskDelegate
import de.andycandy.flow.plugin.io.write.WriteTaskDelegate
import de.andycandy.flow.task.OutputMapperDelegate

interface IOPluginDelegate {
	
	void ls(@DelegatesTo(value = LSTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void ls(@DelegatesTo(value = LSTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure, @DelegatesTo(value = OutputMapperDelegate, strategy = Closure.DELEGATE_FIRST) Closure outputMapper)
	
	void write(@DelegatesTo(value = WriteTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void read(@DelegatesTo(value = ReadTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void read(@DelegatesTo(value = ReadTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure, @DelegatesTo(value = OutputMapperDelegate, strategy = Closure.DELEGATE_FIRST) Closure outputMapper)
}
