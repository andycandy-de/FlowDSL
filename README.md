# FlowDSL
This is a simple tool to implement a data flow. This tool is designed in tasks and each task can has an input and an output. The main task (FlowTask) can create various tasks which are implemented in the core or in some plugins. The FlowTask takes an input and pass it through all tasks. The subtasks can handle the passed data, e.g. write it to the storage or can create new data, e.g. read files from storage.

The example below shows a simple script which filters odd numbers.

```
createFlow {

	input { ( 1 .. 7 ) }
	
	forEach {
	
		mapValue()
		
		code {
			println "Receiving $input"
			if (input % 2 == 1) {
				println "Passing $input to output"
				output = input
			}
		}
	}
}
```

The InputTask (input) ignores the passed input and create a new input. The ForEachTask (forEach) expects a collection or a map and executes each element with an inner FlowTask. 

Execution:

```
$ ./FlowDSL/bin/FlowDSL flow.dsl
Receiving 1
Passing 1 to output
Receiving 2
Receiving 3
Passing 3 to output
Receiving 4
Receiving 5
Passing 5 to output
Receiving 6
Receiving 7
Passing 7 to output
[1, 3, 5, 7]
```

Output from the code task:

```
Receiving 1
Passing 1 to output
Receiving 2
Receiving 3
Passing 3 to output
Receiving 4
Receiving 5
Passing 5 to output
Receiving 6
Receiving 7
Passing 7 to output
```

Final program output:

```
[1, 3, 5, 7]
```

This DSL will be interesting because of the plugin feature. So it's possible to add tasks to the DSL which can access a webservice or a database. This tool is very helpful to create a simple script, loading and updating data from different sources in a simple DSL.

## License

MIT License

Copyright (c) 2020 andycandy-de

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.