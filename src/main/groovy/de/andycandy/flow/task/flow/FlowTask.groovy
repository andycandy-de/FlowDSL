package de.andycandy.flow.task.flow

import org.apache.tools.ant.taskdefs.Javac.ImplementationSpecificArgument

import de.andycandy.flow.FlowPlugin
import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.Task
import de.andycandy.flow.task.TaskUtil
import de.andycandy.flow.task.code.CodeTask
import de.andycandy.flow.task.code.CodeTaskDelegate
import de.andycandy.flow.task.conditional.ConditionalTask
import de.andycandy.flow.task.context.ContextTask
import de.andycandy.flow.task.filter.FilterTask
import de.andycandy.flow.task.flatten.FlattenTask
import de.andycandy.flow.task.for_each.ForEachFlowTask
import de.andycandy.flow.task.for_each.ForEachFlowTaskDelegate
import de.andycandy.flow.task.for_each.ForEachTask
import de.andycandy.flow.task.for_each.MapKeyTask
import de.andycandy.flow.task.for_each.MapValueTask
import de.andycandy.flow.task.input.InputTask
import de.andycandy.flow.task.map.MapTask
import de.andycandy.flow.task.plugins.PluginTask
import de.andycandy.flow.task.value_holder.ValueHolder
import de.andycandy.flow.task.value_holder.ValueHolderTask
import de.andycandy.protect_me.ast.Protect
import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return
import groovyjarjarantlr4.v4.parse.ANTLRParser.parserRule_return

@Protect(classes = [FlowTaskDelegate])
class FlowTask extends AutoCleanTask implements FlowTaskDelegate {
	
	FlowTask parent
	
	Map<String, FlowPlugin> plugins

	Closure closure
	
	@Override
	public void callWithClean() {
		
		plugins = [:]
		
		TaskUtil.passInputToOutput(this)
		
		closure.delegate = getFlowTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()
	}
	
	@Override
	public ContextTask context(Closure closure) {
		
		ContextTask contextTask = new ContextTask()
		contextTask.closure = closure
		
		task(contextTask)
		
		return contextTask
	}
	
	@Protect
	@Override
	public ValueHolder valueHolder(Closure closure = null) {
		
		ValueHolderTask valueHolderTask = new ValueHolderTask()
		valueHolderTask.closure = closure
		
		task(valueHolderTask)
		
		return valueHolderTask;
	}
	
	@Override
	public void plugins(Closure closure) {
		
		PluginTask pluginTask = new PluginTask()
		pluginTask.flowTask = this
		pluginTask.closure = closure
		
		task(pluginTask)
	}
	
	@Override
	public void forEach(Closure closure) {
		
		ForEachTask forEachTask = new ForEachTask()
		forEachTask.task = createForEachFlowTask(closure, this)
		
		task(forEachTask)
	}
	
	@Override
	public void task(Task task) {
		
		TaskUtil.passOutputToInput(this, task)
		task.call()
		TaskUtil.passOutputToOutput(task, this)
	}
	
	@Override
	public void map(Closure closure) {
		
		MapTask mapTask = new MapTask()
		mapTask.closure = closure
		
		task(mapTask)
	}
	
	@Override
	public void filter(Closure closure) {
		
		FilterTask filterTask = new FilterTask()
		filterTask.closure = closure
		
		task(filterTask)
	}
	
	public void input(Closure closure) {
		
		InputTask inputTask = new InputTask()
		inputTask.closure = closure
		
		task(inputTask)
	}
	
	void code(Closure closure) {
		
		CodeTask codeTask = new CodeTask()
		codeTask.closure = closure
		
		task(codeTask)		
	}
	
	void flow(Closure closure) {
		task(createFlowTask(closure, this))
	}
	
	@Override
	public void conditional(Closure condition, Closure closure) {
		
		ConditionalTask conditionalTask = new ConditionalTask()
		conditionalTask.condition = condition
		conditionalTask.task = createFlowTask(closure, this)
		
		task(conditionalTask)		
	}
	
	void flatten(int deep = 1) {
		
		FlattenTask flattenTask = new FlattenTask()
		flattenTask.deep = deep
		
		task(flattenTask)
	}
	
	@Override
	public Object propertyMissing(String name) {
		return getPluginDelegate(name)
	}
	
	protected Object getPluginDelegate(String name) {
		
		FlowPlugin flowPlugin = findFlowPlugin(name)
		flowPlugin.flowTask = this
		return flowPlugin.delegate	
	}
	
	protected FlowPlugin findFlowPlugin(String name) {
		
		if (plugins.containsKey(name)) {
			return plugins[name]
		}
		
		if (parent != null) {
			return parent.findFlowPlugin(name)
		}
		
		throw new MissingPropertyException(name)
	}
	
	protected FlowTaskDelegate getFlowTaskDelegate() {
		return this.toProtectedFlowTaskDelegate()
	}
	
	protected FlowTask createFlowTask(@DelegatesTo(value = FlowTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure, FlowTask parent = null) {
		
		FlowTask flowTask = new FlowTask()
		flowTask.parent = parent
		flowTask.closure = closure
		
		flowTask
	}
	
	protected FlowTask createForEachFlowTask(@DelegatesTo(value = ForEachFlowTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure, FlowTask parent = null) {
		
		ForEachFlowTask flowTask = new ForEachFlowTask()
		flowTask.parent = parent		
		flowTask.closure = closure
		
		flowTask
	}
}