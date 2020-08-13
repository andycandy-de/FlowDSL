createFlow {

	plugins { register pluginRegistry.create('script') }
	
	input { (1 .. 3) }
	
	forEach {
		mapValue()
		
		script.sayHello()
		
		conditional { input % 2 == 1 } {
		
			script.sayHelloToOutput {
			
				name "AnyName${input}"		
			}
		}
		
		conditional { input == 2 } {
			
			script.anyTask()
		}
	}
}