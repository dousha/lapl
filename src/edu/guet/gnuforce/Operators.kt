/**
 * Sooner or later you will have to refactor this shit.
 * esp. when you want to do more
 */

package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException
import edu.guet.gnuforce.exceptions.ParameterMismatchException
import java.io.File
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

object SimpleOperatorTable: OperatorTable<() -> Data>() {
	override var table: Array<Pair<String, () -> Data>> = arrayOf(
			Pair("nop", fun() = NullData),
			Pair("halt", fun(): Data {
				exitProcess(0)
			}),
			Pair("newline", fun(): Data {
				println()
				return NullData
			})
	)
}

object MonadicOperatorTable: OperatorTable<(element: Node, param: HashMap<String, Data>?) -> Data>() {
	override var table: Array<Pair<String, (element: Node, param: HashMap<String, Data>?) -> Data>> = arrayOf(
			Pair("+1", fun(element, param) = Data(DataType.VALUE, element.eval(param).number() + 1)),
			Pair("-1", fun(element, param) = Data(DataType.VALUE, element.eval(param).number() - 1)),
			Pair("ln", fun(element, param) = Data(DataType.VALUE, Math.log(element.eval(param).number()))),
			Pair("ret", fun(element, param) = element.eval(param)),
			Pair("drop!", fun(element, _): Data {
				VariablePool.drop(element.name())
				return NullData
			}),
			Pair("len", fun(name, _) = Data(DataType.VALUE, VariablePool.get(name).array().content.size.toDouble())),
			Pair("type", fun(name, _) = Data(DataType.TYPE, VariablePool.get(name).type())),
			Pair("input!", fun(name, _): Data {
				val input = readLine() ?: return NullData
				VariablePool.set(name.name(), string(input))
				return Data(DataType.VALUE, input.length.toDouble())
			}),
			Pair("eval", fun(path, _): Data {
				val parser = Parser(true)
				parser.parse(File(path.name()))
				return NullData
			}),
			Pair("file-lines", fun(file, _) = Data(DataType.VALUE, (VariablePool.get(file).handler() as FileHandler).countLines().toDouble())),
			Pair("file-lock", fun(file, _): Data {
				VariablePool.get(file).handler().lock()
				return NullData
			}),
			Pair("file-unlock", fun(file, _): Data {
				VariablePool.get(file).handler().unlock()
				return NullData
			}),
			Pair("file-delete", fun(path, _) = Data(DataType.BOOL, File(path.name()).deleteRecursively())),
			Pair("file-eof", fun(file, _) = Data(DataType.VALUE, (VariablePool.get(file).handler() as FileHandler).eof().toDouble())),
			Pair("set?", fun(name, _) = Data(DataType.BOOL, VariablePool.has(name))),
			Pair("file-open", fun(path, param) = Data(DataType.HANDLER, FileHandler(path.toString(param)))),
			Pair("file-new", fun(path, param): Data {
				val fs = File(path.toString(param))
				return if (fs.createNewFile())
					Data(DataType.HANDLER, FileHandler(path.name()))
				else
					FalseData
			}),
			Pair("env-var", fun(name, param): Data {
				val handler = EnvironmentVariableHandler()
				handler.write(name.eval(param), param)
				return handler.read()
			})
	)
}


object DyadicOperatorTable: OperatorTable<(left: Node, right: Node, param: HashMap<String, Data>?) -> Data>() {
	override var table: Array<Pair<String, (left: Node, right: Node, param: HashMap<String, Data>?) -> Data>> = arrayOf(
			Pair("+", fun(left, right, param) = Data(DataType.VALUE, left.eval(param).number() + right.eval(param).number())),
			Pair("-", fun(left, right, param) = Data(DataType.VALUE, left.eval(param).number() - right.eval(param).number())),
			Pair("*", fun(left, right, param) = Data(DataType.VALUE, left.eval(param).number() * right.eval(param).number())),
			Pair("/", fun(left, right, param) = Data(DataType.VALUE, left.eval(param).number() / right.eval(param).number())),
			Pair("^", fun(left, right, param) = Data(DataType.VALUE, Math.pow(left.eval(param).number(), right.eval(param).number()))),
			Pair("set!", fun(name, value, param): Data {
				VariablePool.set(name.name(), value.eval(param))
				return NullData
			}),
			Pair("<", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() < right.eval(param).number())),
			Pair(">", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() > right.eval(param).number())),
			Pair("<=", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() <= right.eval(param).number())),
			Pair(">=", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() >= right.eval(param).number())),
			Pair("!=", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() - right.eval(param).number() > 1e-10)),
			Pair("!==", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() != right.eval(param).number())),
			Pair("=", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() - right.eval(param).number() < 1e-10)),
			Pair("==", fun(left, right, param) = Data(DataType.BOOL, left.eval(param).number() == right.eval(param).number())),
			Pair("def", fun(signature, body, _): Data {
				UserDefinedOperatorTable.add(signature.pointer().nodes()[0].name(), Procedure(signature.pointer(), body.pointer()))
				OperatorTypeCache.update(signature.pointer().nodes()[0].name())
				return NullData
			}),
			Pair("while", fun(condition, body, param): Data {
				while(condition.eval(param).bool()){
					body.eval(param)
				}
				return NullData
			}),
			Pair("read-at", fun(name, index, param)
					= VariablePool.get(name).array().content[index.eval(param).number().toInt()]),
			Pair("erase-at", fun(name, index, param): Data {
				VariablePool.get(name).array().content.drop(index.eval(param).number().toInt())
				return NullData
			}),
			Pair("append", fun(name, value, param): Data {
				VariablePool.get(name).array().content += value.eval(param)
				return NullData // XXX: Maybe returning the length is better?
			}),
			Pair("join", fun(dest, src, _): Data {
				VariablePool.get(dest).array().join(VariablePool.get(src).array())
				return NullData // XXX: Maybe new length?
			}),
			Pair("file-read-all!", fun(name, file, _): Data {
				val str = (VariablePool.get(file).handler() as FileHandler).readAll()
				VariablePool.set(name.name(), string(str))
				return Data(DataType.VALUE, str.length.toDouble())
			}),
			Pair("file-read-line", fun(file, offset, param): Data {
				val str = (VariablePool.get(file).handler() as FileHandler).readLine(offset.eval(param).number().toInt())
				return Data(DataType.ARRAY, string(str))
			}),
			Pair("file-seek", fun(name, pos, param): Data {
				val offset = pos.eval(param).number().toLong()
				(VariablePool.get(name).handler() as FileHandler).seek(offset)
				return Data(DataType.VALUE, offset.toDouble())
			}),
			Pair("file-write", fun(name, arr, param) =
					Data(DataType.BOOL, (VariablePool.get(name).handler() as FileHandler).write(VariablePool.get(arr), param)))
	)
}

object TriadicOperatorTable: OperatorTable<(left: Node, middle: Node, right: Node, param: HashMap<String, Data>?) -> Data>() {
	override var table: Array<Pair<String, (left: Node, middle: Node, right: Node, param: HashMap<String, Data>?) -> Data>> = arrayOf(
			Pair("if", fun(condition, consequence, alternative, param): Data {
				return if (condition.eval(param).bool())
					consequence.eval(param)
				else
					alternative.eval(param)
			}),
			Pair("let", fun(name, value, body, param): Data {
				val pFlag = VariablePool.has(name)
				return if(pFlag) {
					val protect = VariablePool.get(name)
					VariablePool.set(name.name(), value.eval(param))
					val result = body.eval(param)
					VariablePool.set(name.name(), protect)
					result
				} else {
					VariablePool.set(name.name(), value.eval(param))
					val result = body.eval(param)
					VariablePool.drop(name)
					result
				}
			}),
			Pair("write-at", fun(name, index, value, param): Data {
				VariablePool.get(name).array().content[index.eval(param).number().toInt()] = value.eval(param)
				return NullData
			}),
			Pair("range", fun(start, to, step, param): Data {
				return Data(DataType.ARRAY, range(start.eval(param).number().toInt(),
						to.eval(param).number().toInt(),
						step.eval(param).number().toInt()))
			})
	)
}

object ComplexOperatorTable: OperatorTable<(group: NodeGroup, param: HashMap<String, Data>?) -> Data>() {
	override var table: Array<Pair<String, (group: NodeGroup, param: HashMap<String, Data>?) -> Data>> = arrayOf(
			Pair("arr", fun(group, param): Data {
				if (group.length() < 2) throw ParameterMismatchException()
				val arr = LAPLArray(Pair(1, group.length() - 1), arrayOf())
				if (group.nodes()[1].type() == NodeType.STRING_TOKEN && group.nodes()[1].name().startsWith('"')) {
					val builder = StringBuilder()
					for (node in group.nodes().sliceArray(1 until group.length())) {
						builder.append(node.name())
						builder.append(' ')
					}
					return Data(DataType.ARRAY, string(builder.toString().substring(1, builder.length - 2)))
				} else {
					for (node in group.nodes().sliceArray(1 until group.length())) {
						arr.content += node.eval(param)
					}
					return Data(DataType.ARRAY, arr)
				}
			}),
			Pair("print", fun(group, param): Data {
				var start = false
				for(node: Node in group.nodes()){
					if(start) {
						if (node.type() == NodeType.STRING_TOKEN) {
							if (node.name().startsWith('$')) {
								try {
									val data = VariablePool.get(node.name().substring(1))
									print("${data.toString(param)} ")
								} catch (name: NameNotDefinedException) {
									print("${node.name()} ")
								}
							} else {
								print("${node.name()} ")
							}
						} else {
							val result = node.eval(param)
							if(result.type() != DataType.NULL)
								print("${result.toString(param)} ")
						}
					} else start = true // skip the first word
				}
				return NullData
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
