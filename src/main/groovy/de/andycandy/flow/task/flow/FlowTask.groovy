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
		
		closure.delegate = this.toProtectedFlowTaskDelegate()
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()
	}
	
	@Override
	public ContextTask context(Closure closure) {
		
		ContextTask contextTask = new ContextTask()
		contextTask.closure = closure
		
		executeTask(contextTask)
		
		return contextTask
	}
	
	@Protect
	@Override
	public ValueHolder valueHolder(Closure closure = null) {
		
		ValueHolderTask valueHolderTask = new ValueHolderTask()
		valueHolderTask.closure = closure
		
		executeTask(valueHolderTask)
		
		return valueHolderTask;
	}
	
	@Override
	public void plugins(Closure closure) {
		
		PluginTask pluginTask = new PluginTask()
		pluginTask.flowTask = this
		pluginTask.closure = closure
		
		executeTask(pluginTask)
	}
	
	@Override
	public void forEach(Closure closure) {
		
		ForEachTask forEachTask = new ForEachTask()
		forEachTask.task = createFlowTask(closure, this)
		
		executeTask(forEachTask)
	}
	
	@Override
	public void map(Closure closure) {
		
		MapTask mapTask = new MapTask()
		mapTask.closure = closure
		
		executeTask(mapTask)
	}
	
	@Override
	public void filter(Closure closure) {
		
		FilterTask filterTask = new FilterTask()
		filterTask.closure = closure
		
		executeTask(filterTask)
	}
	
	public void input(Closure closure) {
		
		InputTask inputTask = new InputTask()
		inputTask.closure = closure
		
		executeTask(inputTask)
	}
	
	void code(Closure closure) {
		
		CodeTask codeTask = new CodeTask()
		codeTask.closure = closure
		
		executeTask(codeTask)		
	}
	
	void flow(Closure closure) {
		executeTask(createFlowTask(closure, this))
	}
	
	@Override
	public void conditional(Closure condition, Closure closure) {
		
		ConditionalTask conditionalTask = new ConditionalTask()
		conditionalTask.condition = condition
		conditionalTask.task = createFlowTask(closure, this)
		
		executeTask(conditionalTask)		
	}
	
	void mapValue() {		
		executeTask(new MapValueTask())
	}
	
	void mapKey() {		
		executeTask(new MapKeyTask())
	}
	
	void flatten(int deep = 1) {
		
		FlattenTask flattenTask = new FlattenTask()
		flattenTask.deep = deep
		
		executeTask(flattenTask)
	}
	
	@Override
	public Object propertyMissing(String name) {
		return findFlowPlugin(name)
	}
	
	protected FlowPlugin findFlowPlugin(String name) {
		
		if (plugins.containsKey(name)) {
			plugins[name].flowTask = this
			return plugins[name]
		}
		
		if (parent != null) {
			FlowPlugin flowPlugin = parent.findFlowPlugin(name)
			if (flowPlugin != null) {
				flowPlugin.flowTask = this
				return flowPlugin
			}
		}
		
		throw new MissingPropertyException(name)
	}
	
	protected void executeTask(Task task) {
		
		TaskUtil.passOutputToInput(this, task)
		task.call()
		TaskUtil.passOutputToOutput(task, this)
	}
	
	protected FlowTask createFlowTask(@DelegatesTo(value = FlowTaskDelegate, strategy = Closure.DELEGATE_FIRST) Closure closure, FlowTask parent = null) {
		
		FlowTask flowTask = new FlowTask()
		flowTask.parent = parent		
		flowTask.closure = closure
		
		flowTask
	}
}