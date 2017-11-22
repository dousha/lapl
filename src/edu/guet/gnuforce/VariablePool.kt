package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException

object VariablePool {
	fun getGlobal(name: String): Double {
		if (_pool.containsKey(name))
			return _pool[name]!!
		else throw NameNotDefinedException(name)
	}

	fun setGlobal(name: String, value: Double) {
		_pool[name] = value
	}

	private val _pool: HashMap<String, Double> = HashMap<String, Double>()
}