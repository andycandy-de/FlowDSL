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
	
	@picocli.CommandLine.Option(names = [ '-p', '--plugins' ], paramLabel = "Plugins", description = 'Plugins')
	List<File> plugins
	
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
		
		ClassLoader classLoaderWithPlugins = createClassLoaderWithPlugins()
		
		ImportCustomizer importCustomizer = new ImportCustomizer()
		importCustomizer.addStaticImport('de.andycandy.flow.FlowDSL', 'createFlow')
		List<String> pluginImports = getPluginImports(classLoaderWithPlugins)
		
		if (!pluginImports.isEmpty()) {
			importCustomizer.addImports((String[])pluginImports.toArray())
		}
		
		CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
		compilerConfiguration.addCompilationCustomizers(importCustomizer)
		
		GroovyShell groovyShell = new GroovyShell(classLoaderWithPlugins, new Binding(), compilerConfiguration)
		
		Task task = groovyShell.evaluate(scriptFile)
		
		task.call()
		
		if (task.hasOutput()) {			
			return task.output
		}
		
		return null
	}
	
	List<String> getPluginImports(ClassLoader classLoaderWithPlugins) {
		
		List<String> list = []
		
		if (pluginDir != null) {			
			pluginDir.listFiles().each { list += getPluginImports(classLoaderWithPlugins, it) }
		}
		if (plugins != null && !plugins.isEmpty()) {			
			plugins.each { list += getPluginImports(classLoaderWithPlugins, it) }
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
		if (pluginDir != null) {
			urls += getUrlList(pluginDir.listFiles().toList())
			
		}
		if (plugins != null && !plugins.isEmpty()) {
			urls += getUrlList(plugins)
		}
		
		if (urls.isEmpty()) {
			return this.getClass().getClassLoader()
		}
		
		return new URLClassLoader((URL[])urls.toArray(), this.getClass().getClassLoader())
	}
	
	List<URL> getUrlList(List<File> fileList) {
		
		List<URL> urlList = []
		fileList.each { urlList <<  it.toURI().toURL() }
		return urlList
	}
	
	String getClassName(JarEntry jarEntry) {
		
		String className = jarEntry.name.substring(0, jarEntry.name.length() - '.class'.length());
		return className.replace('/', '.').replace('$', '.');
	}
	
	void printUsage() {
		(new picocli.CommandLine(new App())).usage(System.out);
	}
	
	void printErr(String text) {
		System.err.println(text)
	}

}
