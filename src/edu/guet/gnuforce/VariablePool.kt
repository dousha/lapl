package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException

object VariablePool {

	fun get(name: String): Data {
		return if (pool.containsKey(name))
			pool[name]!!
		else throw NameNotDefinedException(name)
	}

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

	fun drop(name: String){
		pool.remove(name)
	}

	fun has(name: String): Boolean = pool.containsKey(name)

	private val pool: HashMap<String, Data> = HashMap()
}