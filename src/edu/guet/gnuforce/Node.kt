package edu.guet.gnuforce

enum class NodeType {
	VALUE, NAME, POINTER
}

class Node(private val type: NodeType, private val content: Any){

	fun eval(map: HashMap<String, Data>?): Data{
		return when (type) {
			NodeType.VALUE -> Data(DataType.VALUE, content)
			NodeType.POINTER -> Data(DataType.VALUE, (content as NodeGroup).eval(map))
			else -> {
				return map?.get(content as String) ?: VariablePool.get(content as String)
			}
		}
	}

	fun name(): String = if (type == NodeType.NAME) content as String else throw RuntimeException()

	fun pointer(): NodeGroup = if (type == NodeType.POINTER) content as NodeGroup else throw RuntimeException()

	fun type() = type

	override fun toString(): String = when(type){
		NodeType.NAME -> content as String
		NodeType.VALUE -> (content as Double).toString()
		NodeType.POINTER -> (content as NodeGroup).toString()
	}
}