flow {
	
	input { 3 }
	
	code {
		output = (0 .. input)
	}
	
	forEach {
		code {
			output = input.value * input.value
		}
	}
}