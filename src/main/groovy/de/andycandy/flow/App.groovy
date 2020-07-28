package de.andycandy.flow

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import de.andycandy.flow.task.Task
import groovy.transform.NullCheck

@NullCheck
class App {

    static void main(String[] args) {
		
		App app = new App()
		
		println app.start(args)
    }
	
	Object start(String[] args) {
		
		if (args.length != 1) {
			printErr('Usage: FlowDSL [file]')
			systemExit()
			return
		}
		
		File file = new File(args[0])
		if (!file.isFile()) {
			printErr("File ${file.absolutePath} not exists!")
			systemExit()
			return
		}
		
		return evaluate(file)
	}
	
	Object evaluate(File file) {
		
		ImportCustomizer importCustomizer = new ImportCustomizer()
		importCustomizer.addStarImports('de.andycandy.flow')
		importCustomizer.addStarImports('de.andycandy.flow.task')
		importCustomizer.addStaticImport('de.andycandy.flow.FlowDSL', 'flow')
		
		CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
		compilerConfiguration.addCompilationCustomizers(importCustomizer)
		
		GroovyShell groovyShell = new GroovyShell(App.class.getClassLoader(), new Binding(), compilerConfiguration)
		
		Task task = groovyShell.evaluate(file)
		
		task.call()
		task.output
	}
	
	void printErr(String text) {
		System.err.println(text)
	}
	
	void systemExit() {
		System.exit(-1)
	}
}