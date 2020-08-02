package de.andycandy.flow.task.value_holder

import static de.andycandy.flow.task.TaskUtil.passInputToOutput

import de.andycandy.flow.task.AutoCleanTask
import de.andycandy.flow.task.TaskUtil
import de.andycandy.protect_me.ast.Protect

@Protect(classes = [ValueHolderTaskDelegate])
class ValueHolderTask extends AutoCleanTask implements ValueHolderTaskDelegate, ValueHolder {

	Object value

	Closure closure

	@Override
	public void callWithClean() {

		passInputToOutput(this)

		if (closure != null) {

			closure.delegate = this.toProtectedValueHolderTaskDelegate()
			closure.resolveStrategy = Closure.DELEGATE_FIRST

			value = closure.call()
		}
	}
}
