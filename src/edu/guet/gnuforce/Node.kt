package edu.guet.gnuforce

enum class NodeType {
	NUMBER_LITERAL, STRING_TOKEN, NODE_GROUP_POINTER
}

class Node(private val type: NodeType, private val content: Any){

	fun eval(map: HashMap<String, Data>?): Data{
		return when (type) {
			NodeType.NUMBER_LITERAL -> Data(DataType.VALUE, content)
			NodeType.NODE_GROUP_POINTER -> (content as NodeGroup).eval(map)
			else -> {
				return map?.get(content as String) ?: VariablePool.get(content as String)
			}
		}
	}

	fun name(): String = if (type == NodeType.STRING_TOKEN) content as String else throw RuntimeException()

	fun pointer(): NodeGroup = if (type == NodeType.NODE_GROUP_POINTER) content as NodeGroup else throw RuntimeException()

	fun type() = type

	override fun toString(): String = when(type){
		NodeType.STRING_TOKEN -> content as String
		NodeType.NUMBER_LITERAL -> (content as Double).toString()
		NodeType.NODE_GROUP_POINTER -> (content as NodeGroup).toString()
	}

	fun toString(param: HashMap<String, Data>?): String = when(type){
			NodeType.STRING_TOKEN -> if ((content as String).startsWith('$')) VariablePool.get(this).toString(param) else content
			NodeType.NUMBER_LITERAL -> (content as Double).toString()
			NodeType.NODE_GROUP_POINTER -> (content as NodeGroup).eval(param).toString(param)
		}
}