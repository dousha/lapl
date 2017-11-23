package edu.guet.gnuforce

enum class DataType {
	NULL, VALUE, NAME, POINTER
}

data class Data(private val type: DataType, private val content: Any){
	fun getData(): Any = when (type) {
		DataType.VALUE -> content as Double
		DataType.NAME -> VariablePool.getGlobal(content as String)
		DataType.POINTER -> (content as Node).eval(null) // TODO: ???
		else -> throw RuntimeException()
	}
}