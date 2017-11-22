package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException

enum class NodeType {
	VALUE, NAME, POINTER
}

class Node(type: NodeType, content: Any){

	fun eval(map: HashMap<String, Double>?): Double{
		return when (type) {
			NodeType.VALUE -> content as Double
			NodeType.POINTER -> (content as NodeGroup).eval(map)
			else -> {
				try {
					VariablePool.getGlobal(content as String)
				} catch (rex: NameNotDefinedException) {
					if(map == null) throw NameNotDefinedException(rex.name)
					if(map.containsKey(content as String))
						map[content]!!
					else
						throw NameNotDefinedException(rex.name)
				}
			}
		};
	}

	fun name(): String = if (type == NodeType.NAME) content as String else throw RuntimeException()

	fun pointer(): NodeGroup = if (type == NodeType.POINTER) content as NodeGroup else throw RuntimeException()

	fun type() = type

	override fun toString(): String {
		return when(type){
			NodeType.NAME -> content as String
			NodeType.VALUE -> (content as Double).toString()
			NodeType.POINTER -> (content as NodeGroup).toString()
		}
	}

	private val type = type
	private val content = content
}