createFlow {
	
	input { 3 }
	
	code {
		output = (0 .. input)
	}
	
	forEach {
	
		mapValue()
		
		code {
			output = input * input
		}
	}
}