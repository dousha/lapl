package edu.guet.gnuforce

enum class DataType {
	VALUE, NAME, POINTER, ARRAY, HANDLER, CHAR, NULL, TYPE, BOOL
}

data class Data(private val type: DataType, private val content: Any){
	fun type() = (content as? Data)?.type ?: type
	fun number(): Double {
		return if (content is Data) content.number()
		else when (type) {
			DataType.VALUE -> content as Double
			DataType.POINTER -> (content as NodeGroup).eval(null).number()
			else -> throw RuntimeException()
		}
	}
	fun name() = if (type == DataType.NAME) content as String else throw RuntimeException()
	fun pointer() = if (type == DataType.POINTER) content as Node else throw RuntimeException()
	fun array() = if (type == DataType.ARRAY) content as LAPLArray else throw RuntimeException()
	fun char() = if (type == DataType.CHAR) content as Char else throw RuntimeException()
	fun handler() = if (type == DataType.HANDLER) content as Handler else throw RuntimeException()
	fun bool() = if (type == DataType.BOOL) content as Boolean else throw RuntimeException()

	fun toString(param: HashMap<String, Data>?): String {
		return if(content is Data) content.toString(param)
		else when (type) {
			DataType.VALUE -> (content as Double).toString()
			DataType.POINTER -> (content as NodeGroup).eval(param).toString()
			DataType.NAME -> VariablePool.get(content as String).toString(param)
			DataType.ARRAY -> (content as LAPLArray).toString(param)
			DataType.HANDLER -> (content as Handler).toString()
			DataType.CHAR -> (content as Char).toString()
			DataType.NULL -> "(null)"
			DataType.TYPE -> (content as DataType).toString()
			DataType.BOOL -> (content as Boolean).toString()
		}
	}
}

val NullData = Data(DataType.NULL, 0)
val TrueData = Data(DataType.BOOL, true)
val FalseData = Data(DataType.BOOL, false)