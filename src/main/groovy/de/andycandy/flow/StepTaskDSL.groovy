package de.andycandy.flow

import de.andycandy.flow.task.CodeTask
import de.andycandy.flow.task.ConditionalTask
import de.andycandy.flow.task.Context
import de.andycandy.flow.task.ContextTask
import de.andycandy.flow.task.EachTask
import de.andycandy.flow.task.FilterTask
import de.andycandy.flow.task.FlattenTask
import de.andycandy.flow.task.InputTask
import de.andycandy.flow.task.MapTask
import de.andycandy.flow.task.StepTask

class StepTaskDSL {
	
	Map<String, FlowPlugin> plugins = [:]
	
	StepTask stepTask = new StepTask()
	
	Context context = new Context()
	
	void plugins(Closure closure) {
		
		PluginDelegate pluginDelegate = new PluginDelegate()
		
		pluginDelegate.stepTaskDSL = this
		
		closure.delegate = pluginDelegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
	}
	
	void input(Closure closure) {
		InputTask inputTask = new InputTask()
		inputTask.closure = closure
		stepTask.tasks << inputTask
	}
	
	void step(@DelegatesTo(value = StepTaskDSL, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		stepTask.tasks << createStepTask(closure, this)
	}
	
	void code(@DelegatesTo(value = CodeTask, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		CodeTask codeTask = new CodeTask()
		codeTask.closure = closure
		stepTask.tasks << codeTask
	}
	
	void forEach(@DelegatesTo(value = StepTaskDSL, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		EachTask eachTask = new EachTask()
		eachTask.task = createStepTask(closure, this)
		stepTask.tasks << eachTask
	}
	
	void flatten(int deep = 1) {
		FlattenTask flattenTask = new FlattenTask()
		flattenTask.deep = deep
		stepTask.tasks << flattenTask
	}
	
	void conditional(@DelegatesTo(value = CodeTask, strategy = Closure.DELEGATE_FIRST) Closure condition, @DelegatesTo(value = StepTaskDSL, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		ConditionalTask conditionalTask = new ConditionalTask()
		conditionalTask.condition = condition
		conditionalTask.task = createStepTask(closure, this)
		stepTask.tasks << conditionalTask
	}
	
	void map(Closure closure) {
		MapTask mapTask = new MapTask()
		mapTask.closure = closure
		stepTask.tasks << mapTask
	}
	
	void filter(Closure closure) {
		FilterTask filterTask = new FilterTask()
		filterTask.closure = closure
		stepTask.tasks << filterTask
	}
	
	void context(Closure closure) {
		ContextTask contextTask = new ContextTask()
		contextTask.closure = closure
		stepTask.tasks << contextTask
	}
	
	protected StepTask createStepTask(@DelegatesTo(value = StepTaskDSL, strategy = Closure.DELEGATE_FIRST) Closure closure, StepTaskDSL parent = null) {
		
		StepTaskDSL stepTaskDSL = new StepTaskDSL()
		
		stepTaskDSL.plugins = [:]
		
		if (parent != null) {
			parent.plugins.each { stepTaskDSL.registerPlugin(it.value.createInstance()) }
			stepTaskDSL.context = parent.context
		}
		
		closure.delegate = stepTaskDSL
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		
		closure.call()
		
		stepTaskDSL.stepTask.context = stepTaskDSL.context
		stepTaskDSL.stepTask
	}
	
	def propertyMissing(String name) {
		
		if (plugins.containsKey(name)) {
			return plugins[name]
		}
		
		throw new MissingPropertyException(name);
	}
	
	protected registerPlugin(FlowPlugin plugin) {
		
		if (hasProperty(plugin.name)) {
			throw new IllegalStateException("The plugin name '${plugin.name}' is not allowed!")
		}
		
		plugins[plugin.name] = plugin
		plugin.stepTask = this.stepTask
	}
	
	private boolean hasProperty(name) {
		try {
			StepTaskDSL.getDeclaredField(name)
			return true
		} catch (NoSuchFieldException e ) {
			return false
		}
	}
	
	static class PluginDelegate {
		
		StepTaskDSL stepTaskDSL
		
		void register(FlowPlugin plugin) {
			stepTaskDSL.registerPlugin(plugin)
		}
	}
}