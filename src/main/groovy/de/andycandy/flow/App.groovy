package de.andycandy.flow

import java.util.concurrent.Callable
import java.util.jar.JarEntry
import java.util.jar.JarInputStream

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import de.andycandy.flow.task.Task

@picocli.CommandLine.Command(name = Constants.NAME, version = Constants.VERSION)
class App implements Callable<Integer> {
	
	@picocli.CommandLine.Parameters(index = '0', description = 'The file which creates a flow')
	File scriptFile
	
	@picocli.CommandLine.Option(names = [ '-pd', '--pluginDir' ], paramLabel = "Plugin Directory", description = 'Directory with Plugins')
	File pluginDir
	
	@picocli.CommandLine.Option(names = [ '-p', '--plugin' ], paramLabel = "Plugin", description = 'Plugin')
	List<File> plugins = []
	
	@Override
	public Integer call() throws Exception {
		
		if (scriptFile == null) {
			this.printUsage()
			return -1
		}
		
		if (!scriptFile.isFile()) {
			this.printErr("File '${scriptFile.absolutePath}' not exists!")
			return -1
		}
		
		println execute()
		
		return 0;
	}

    static void main(String[] args) {
		System.exit((new picocli.CommandLine(new App())).execute(args))
    }
	
	Object execute() {
		
		if (pluginDir != null) {
			pluginDir.listFiles().each { plugins << it }
		}
		
		ClassLoader classLoaderWithPlugins = createClassLoaderWithPlugins()
		
		PluginRegistry pluginRegistry = new PluginRegistry()
		findPluginsInGroovyScripts(pluginRegistry)
		
		ImportCustomizer importCustomizer = new ImportCustomizer()
		importCustomizer.addStaticImport('de.andycandy.flow.FlowDSL', 'createFlow')
		List<String> pluginImports = getPluginImports(classLoaderWithPlugins)
		
		if (!pluginImports.isEmpty()) {
			importCustomizer.addImports((String[])pluginImports.toArray())
		}
		
		CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
		compilerConfiguration.addCompilationCustomizers(importCustomizer)
		
		GroovyShell groovyShell = new GroovyShell(classLoaderWithPlugins, new Binding([pluginRegistry: pluginRegistry]), compilerConfiguration)
		
		Task task = groovyShell.evaluate(scriptFile)
		
		task.call()
		
		if (task.hasOutput()) {			
			return task.output
		}
		
		return null
	}
	
	void findPluginsInGroovyScripts(PluginRegistry pluginRegistry) {
		
		if (plugins.isEmpty()) {
			return
		}
		
		ScriptPluginHelper scriptPluginHelper = new ScriptPluginHelper()
		scriptPluginHelper.pluginRegistry = pluginRegistry
		
		ImportCustomizer importCustomizer = new ImportCustomizer()
		importCustomizer.addStaticImport('de.andycandy.flow.FlowDSL', 'createFlow')
		importCustomizer.addImports('de.andycandy.flow.task.Task', 'de.andycandy.flow.task.AutoCleanTask', 'de.andycandy.flow.task.TaskUtil')
		importCustomizer.addStaticImport('de.andycandy.flow.DynamicCreator', 'createDynamic')
		
		CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
		compilerConfiguration.addCompilationCustomizers(importCustomizer)
		
		GroovyShell groovyShell = new GroovyShell(this.getClass().getClassLoader(), new Binding([plugin: scriptPluginHelper]), compilerConfiguration)
		
		plugins.findAll { isPluginScriptFile(it) }.each { 
			groovyShell.evaluate(it)
		}
	}
	
	boolean isPluginScriptFile(File file) {
		
		if (!file.isFile()) {
			return false
		}
		
		return endsWithAny(file.name, '.script', '.groovy', 'plugin')
	}
	
	boolean endsWithAny(String name, String... suffix) {
		for (s in suffix) {
			if (name.endsWith(s)) {
				return true
			}
		}
		return false
	}
	
	List<String> getPluginImports(ClassLoader classLoaderWithPlugins) {
		
		List<String> list = []
		
		if (!plugins.isEmpty()) {			
			plugins.findAll { it.name.endsWith('.jar') }.each { list += getPluginImports(classLoaderWithPlugins, it) }
		}
		
		return list
	}
	
	List<String> getPluginImports(ClassLoader classLoaderWithPlugins, File file) {
		
		FileInputStream fileInputStream = new FileInputStream(file)
		JarInputStream jarInputStream = new JarInputStream(fileInputStream)
		
		List<String> list = []
		jarInputStream.withCloseable { 
			JarEntry next = it.getNextJarEntry()
			while (next != null) {
				if (isPlugin(classLoaderWithPlugins, next)) {
					list << getClassName(next)
				}
				next = it.getNextJarEntry()
			}
		}
		
		return list
	}
	
	boolean isPlugin(ClassLoader classLoaderWithPlugins, JarEntry jarEntry) {
		
		if (!jarEntry.name.endsWith('Plugin.class')) {
			return false
		}
		
		if (isInnerClass(jarEntry)) {
			return false
		}
		
		Class clazz = classLoaderWithPlugins.loadClass(getClassName(jarEntry))
		if (!clazz.isAnnotationPresent(AutoImportPlugin)) {
			return false
		}
		
		if (!FlowPlugin.isAssignableFrom(clazz)) {
			return false
		}
		
		return true
	}
	
	ClassLoader createClassLoaderWithPlugins() {
		
		List<URL> urls = []
		if (!plugins.isEmpty()) {
			urls += getUrlList(plugins)
		}
		
		if (urls.isEmpty()) {
			return this.getClass().getClassLoader()
		}
		
		return new URLClassLoader((URL[])urls.toArray(), this.getClass().getClassLoader())
	}
	
	List<URL> getUrlList(List<File> fileList) {
		
		List<URL> urlList = []
		fileList
			.findAll { it.name.endsWith('.jar') }
			.each { urlList <<  it.toURI().toURL() }
		return urlList
	}
	
	String getClassName(JarEntry jarEntry) {
		
		String className = jarEntry.name.substring(0, jarEntry.name.length() - '.class'.length());
		return className.replace('/', '.');
	}
	
	boolean isInnerClass(JarEntry jarEntry) {
		return getClassName(jarEntry).contains('$')
	}
	
	void printUsage() {
		(new picocli.CommandLine(new App())).usage(System.out);
	}
	
	void printErr(String text) {
		System.err.println(text)
	}

}
