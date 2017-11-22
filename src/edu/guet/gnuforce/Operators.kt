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
		table += Pair<String, T>(name, procedure)
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

object MonadicOperatorTable: OperatorTable<(element: Node, param: HashMap<String, Double>?) -> Double>() {
	override var table: Array<Pair<String, (element: Node, param: HashMap<String, Double>?) -> Double>> = arrayOf(
			Pair("+1", fun(element, param) = element.eval(param) + 1),
			Pair("-1", fun(element, param) = element.eval(param) - 1),
			Pair("ret", fun(element, param) = element.eval(param)),
			Pair("display", fun(element, param): Double {
				println(element.eval(param))
				return Double.NaN
			})
	)
}


object DyadicOperatorTable: OperatorTable<(left: Node, right: Node, param: HashMap<String, Double>?) -> Double>() {
	override var table: Array<Pair<String, (left: Node, right: Node, param: HashMap<String, Double>?) -> Double>> = arrayOf(
			Pair("+", fun(left, right, param) = left.eval(param) + right.eval(param)),
			Pair("-", fun(left, right, param) = left.eval(param) - right.eval(param)),
			Pair("*", fun(left, right, param) = left.eval(param) * right.eval(param)),
			Pair("/", fun(left, right, param) = left.eval(param) / right.eval(param)),
			Pair("set!", fun(name, value, param): Double {
				VariablePool.setGlobal(name.name(), value.eval(param))
				return Double.NaN
			}),
			Pair("<", fun(left, right, param) = if (left.eval(param) < right.eval(param)) 1.0 else 0.0),
			Pair(">", fun(left, right, param) = if (left.eval(param) > right.eval(param)) 1.0 else 0.0),
			Pair("<=", fun(left, right, param) = if (left.eval(param) <= right.eval(param)) 1.0 else 0.0),
			Pair(">=", fun(left, right, param) = if (left.eval(param) >= right.eval(param)) 1.0 else 0.0),
			Pair("!=", fun(left, right, param) = if (left.eval(param) != right.eval(param)) 1.0 else 0.0),
			Pair("==", fun(left, right, param) = if (left.eval(param) == right.eval(param)) 1.0 else 0.0),
			Pair("def", fun(signature, body, _): Double {
				UserDefinedOperatorTable.add(signature.pointer().nodes()[0].name(), Procedure(signature.pointer(), body.pointer()))
				return Double.NaN
			}),
			Pair("while", fun(condition, body, param): Double {
				while(condition.eval(param) > 0.0){
					body.eval(param)
				}
				return Double.NaN
			})
	)
}

object TriadicOperatorTable: OperatorTable<(left: Node, middle: Node, right: Node, param: HashMap<String, Double>?) -> Double>() {
	override var table: Array<Pair<String, (left: Node, middle: Node, right: Node, param: HashMap<String, Double>?) -> Double>> = arrayOf(
			Pair("if", fun(condition, consequence, alternative, param): Double {
				if (condition.eval(param) > 0.0)
					return consequence.eval(param)
				else
					return alternative.eval(param)
			})
	)
}

object ComplexOperatorTable: OperatorTable<(group: NodeGroup, param: HashMap<String, Double>?) -> Double>() {
	override var table: Array<Pair<String, (group: NodeGroup, param: HashMap<String, Double>?) -> Double>> = arrayOf(
			Pair("len", fun(group, _) = group.length().toDouble()),
			Pair("arr", fun(group, param) = group.length().toDouble()),
			Pair("print", fun(group, _): Double {
				var start = false
				for(node: Node in group.nodes()){
					if(start)
						print("${node.name()} ")
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

fun operatorType(name: String): OperatorType {
	return when {
		SimpleOperatorTable.has(name) -> OperatorType.SIMPLE
		MonadicOperatorTable.has(name) -> OperatorType.MONADIC
		DyadicOperatorTable.has(name) -> OperatorType.DYADIC
		TriadicOperatorTable.has(name) -> OperatorType.TRIADIC
		ComplexOperatorTable.has(name) -> OperatorType.COMPLEX
		UserDefinedOperatorTable.has(name) -> OperatorType.USER_DEFINED
		else -> throw NameNotDefinedException(name)
	}
}