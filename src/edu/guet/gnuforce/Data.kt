package edu.guet.gnuforce

enum class DataType {
	VALUE, NAME, POINTER, ARRAY, HANDLER
}

data class Data(private val type: DataType, private val content: Any){
	fun type() = type
	fun number() = when (type) {
		DataType.VALUE -> content as Double
		DataType.POINTER -> (content as NodeGroup).eval(null)
		else -> throw RuntimeException()
	}
	fun name() = if (type == DataType.NAME) content as String else throw RuntimeException()
	fun pointer() = if (type == DataType.POINTER) content as NodeGroup else throw RuntimeException()
	fun array() = if (type == DataType.ARRAY) content as LAPLArray else throw RuntimeException()
	fun handler() = if (type == DataType.HANDLER) content as Handler else throw RuntimeException()
}