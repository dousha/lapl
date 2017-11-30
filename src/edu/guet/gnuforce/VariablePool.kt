package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException

object VariablePool {

	fun get(name: String): Data {
		return if (pool.containsKey(name))
			pool[name]!!
		else throw NameNotDefinedException(name)
	}

	fun get(node: Node): Data = get(node.name())

	fun set(name: String, value: Double) {
		pool[name] = Data(DataType.VALUE, value)
	}

	fun set(name: String, value: NodeGroup) {
		pool[name] = Data(DataType.POINTER, value)
	}

	fun set(name: String, value: String){
		pool[name] = Data(DataType.NAME, value)
	}

	fun set(name: String, value: LAPLArray){
		pool[name] = Data(DataType.ARRAY, value)
	}

	fun set(name: String, value: Data){
		pool[name] = value
	}

	fun set(name: String, value: Handler) {
		pool[name] = Data(DataType.HANDLER, value)
	}

	fun drop(name: String) = pool.remove(name)

	fun drop(node: Node) = drop(node.name())

	fun has(name: String): Boolean = pool.containsKey(name)

	fun has(node: Node): Boolean = has(node.name())

	private val pool: HashMap<String, Data> = HashMap()
}