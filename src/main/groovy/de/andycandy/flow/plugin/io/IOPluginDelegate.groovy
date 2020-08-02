package de.andycandy.flow.plugin.io

import de.andycandy.flow.plugin.io.ls.LSTaskDelegate
import de.andycandy.flow.plugin.io.read.ReadTaskDelegate
import de.andycandy.flow.plugin.io.write.WriteTaskDelegate

interface IOPluginDelegate {
	
	void ls(@DelegatesTo(value = LSTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void write(@DelegatesTo(value = WriteTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
	
	void read(@DelegatesTo(value = ReadTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure)
}
