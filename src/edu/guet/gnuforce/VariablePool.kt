package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException

object VariablePool {
	fun getGlobal(name: String): Double {
		if (pool.containsKey(name))
			return pool[name]!!
		else throw NameNotDefinedException(name)
	}

	fun setGlobal(name: String, value: Double) {
		pool[name] = value
	}

	fun dropGlobal(name: String){
		pool.remove(name)
	}

	private val pool: HashMap<String, Double> = HashMap()
}