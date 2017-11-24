package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException
import kotlin.system.exitProcess

enum class OperatorType(val paramCount: Int) {
	SIMPLE(0), MONADIC(1), DYADIC(2), TRIADIC(3), COMPLEX(-1), USER_DEFINED(-2)
}

abstract class OperatorTable<T> {
	fun has(name: String): Boolean {
		table.forEach {
			if (name == it.first) return true
		}
		return false
	}

	fun add(name: String, procedure: T) {
		table += Pair(name, procedure)
	}

	fun get(name: String): T{
		table.forEach { if (it.first == name) return it.second }
		throw RuntimeException()
	}

	abstract var table: Array<Pair<String, T>>
}

object SimpleOperatorTable: OperatorTable<() -> Double>() {
	override var table: Array<Pair<String, () -> Double>> = arrayOf(
			Pair("nop", fun() = Double.NaN),
			Pair("halt", fun(): Double {
				exitProcess(0)
			}),
			Pair("newline", fun(): Double {
				println()
				return Double.NaN
			})
	)
}

object MonadicOperatorTable: OperatorTable<(element: Node, param: HashMap<String, Data>?) -> Double>() {
	override var table: Array<Pair<String, (element: Node, param: HashMap<String, Data>?) -> Double>> = arrayOf(
			Pair("+1", fun(element, param) = element.eval(param).number() + 1),
			Pair("-1", fun(element, param) = element.eval(param).number() - 1),
			Pair("ln", fun(element, param) = Math.log(element.eval(param).number())),
			Pair("ret", fun(element, param) = element.eval(param).number()),
			Pair("display", fun(element, param): Double {
				println(element.eval(param).number())
				return Double.NaN
			}),
			Pair("drop!", fun(element, _): Double {
				VariablePool.drop(element.name())
				return Double.NaN
			})
	)
}


object DyadicOperatorTable: OperatorTable<(left: Node, right: Node, param: HashMap<String, Data>?) -> Double>() {
	override var table: Array<Pair<String, (left: Node, right: Node, param: HashMap<String, Data>?) -> Double>> = arrayOf(
			Pair("+", fun(left, right, param) = left.eval(param).number() + right.eval(param).number()),
			Pair("-", fun(left, right, param) = left.eval(param).number() - right.eval(param).number()),
			Pair("*", fun(left, right, param) = left.eval(param).number() * right.eval(param).number()),
			Pair("/", fun(left, right, param) = left.eval(param).number() / right.eval(param).number()),
			Pair("^", fun(left, right, param) = Math.pow(left.eval(param).number(), right.eval(param).number())),
			Pair("set!", fun(name, value, param): Double {
				VariablePool.set(name.name(), value.eval(param).number())
				return Double.NaN
			}),
			Pair("<", fun(left, right, param) = if (left.eval(param).number() < right.eval(param).number()) 1.0 else 0.0),
			Pair(">", fun(left, right, param) = if (left.eval(param).number() > right.eval(param).number()) 1.0 else 0.0),
			Pair("<=", fun(left, right, param) = if (left.eval(param).number() <= right.eval(param).number()) 1.0 else 0.0),
			Pair(">=", fun(left, right, param) = if (left.eval(param).number() >= right.eval(param).number()) 1.0 else 0.0),
			Pair("!=", fun(left, right, param) = if (left.eval(param).number() - right.eval(param).number() > 1e-10) 1.0 else 0.0),
			Pair("!==", fun(left, right, param) = if (left.eval(param).number() != right.eval(param).number()) 1.0 else 0.0),
			Pair("=", fun(left, right, param) = if (left.eval(param).number() - right.eval(param).number() < 1e-10) 1.0 else 0.0),
			Pair("==", fun(left, right, param) = if (left.eval(param).number() == right.eval(param).number()) 1.0 else 0.0),
			Pair("def", fun(signature, body, _): Double {
				UserDefinedOperatorTable.add(signature.pointer().nodes()[0].name(), Procedure(signature.pointer(), body.pointer()))
				OperatorTypeCache.update(signature.pointer().nodes()[0].name())
				return Double.NaN
			}),
			Pair("while", fun(condition, body, param): Double {
				while(condition.eval(param).number() > 0.0){
					body.eval(param).number()
				}
				return Double.NaN
			})
	)
}

object TriadicOperatorTable: OperatorTable<(left: Node, middle: Node, right: Node, param: HashMap<String, Data>?) -> Double>() {
	override var table: Array<Pair<String, (left: Node, middle: Node, right: Node, param: HashMap<String, Data>?) -> Double>> = arrayOf(
			Pair("if", fun(condition, consequence, alternative, param): Double {
				return if (condition.eval(param).number() > 0.0)
					consequence.eval(param).number()
				else
					alternative.eval(param).number()
			}),
			Pair("let", fun(name, value, body, param): Double {
				val pFlag = VariablePool.has(name.name())
				return if(pFlag) {
					val protect = VariablePool.get(name.name())
					VariablePool.set(name.name(), value.eval(param).number())
					val result = body.eval(param).number()
					VariablePool.set(name.name(), protect)
					result
				} else {
					VariablePool.set(name.name(), value.eval(param).number())
					val result = body.eval(param).number()
					VariablePool.drop(name.name())
					result
				}
			})
	)
}

object ComplexOperatorTable: OperatorTable<(group: NodeGroup, param: HashMap<String, Data>?) -> Double>() {
	override var table: Array<Pair<String, (group: NodeGroup, param: HashMap<String, Data>?) -> Double>> = arrayOf(
			Pair("len", fun(group, _) = group.length().toDouble()),
			//Pair("arr", fun(group, param) = group.length().toDouble()),
			Pair("print", fun(group, param): Double {
				var start = false
				for(node: Node in group.nodes()){
					if(start) {
						if (node.type() == NodeType.NAME) {
							print("${node.name()} ")
						}
						else {
							val result = node.eval(param).number()
							if(!result.isNaN())
								print("$result ")
						}
					}
					else
						start = true // skip the first word
				}
				return Double.NaN
			})
	)
}

object UserDefinedOperatorTable: OperatorTable<Procedure>(){
	override var table: Array<Pair<String, Procedure>> = arrayOf()
}

object OperatorTypeCache {
	fun init(){
		SimpleOperatorTable.table.forEach {
			table.put(it.first, OperatorType.SIMPLE)
		}
		MonadicOperatorTable.table.forEach {
			table.put(it.first, OperatorType.MONADIC)
		}
		DyadicOperatorTable.table.forEach {
			table.put(it.first, OperatorType.DYADIC)
		}
		TriadicOperatorTable.table.forEach {
			table.put(it.first, OperatorType.TRIADIC)
		}
		ComplexOperatorTable.table.forEach {
			table.put(it.first, OperatorType.COMPLEX)
		}
	}

	fun has(name: String): Boolean = table.containsKey(name)

	fun get(name: String): OperatorType = table[name]!!

	fun update(name: String) = table.put(name, OperatorType.USER_DEFINED)

	private var table: HashMap<String, OperatorType> = hashMapOf()
}

fun operatorType(name: String): OperatorType = when {
	OperatorTypeCache.has(name) -> OperatorTypeCache.get(name)
	UserDefinedOperatorTable.has(name) -> OperatorType.USER_DEFINED
	else -> throw NameNotDefinedException(name)
}
