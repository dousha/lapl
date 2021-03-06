package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException
import edu.guet.gnuforce.exceptions.ParameterMismatchException

open class NodeGroup(private val father: NodeGroup?){
	fun eval(param: HashMap<String, Data>?): Data {
		if(father == null){
			for(node in nodes){
				node.eval(null)
				++count
			}
			return NullData
		}
		when(nodes[0].type()) {
			NodeType.STRING_TOKEN -> {
				try {
					val type = operatorType(nodes[0].name())
					if (type.paramCount != -1 && length() - 1 != type.paramCount) {
						if (type == OperatorType.USER_DEFINED
								&& length() - 1 != UserDefinedOperatorTable.get(nodes[0].name()).paramCount())
							throw ParameterMismatchException()
					}
					val name = nodes[0].name()
					return when (type) {
						OperatorType.SIMPLE -> SimpleOperatorTable.get(name)()
						OperatorType.MONADIC -> MonadicOperatorTable.get(name)(nodes[1], param)
						OperatorType.DYADIC -> DyadicOperatorTable.get(name)(nodes[1], nodes[2], param)
						OperatorType.TRIADIC -> TriadicOperatorTable.get(name)(nodes[1], nodes[2], nodes[3], param)
						OperatorType.COMPLEX -> ComplexOperatorTable.get(name)(this, param)
						OperatorType.USER_DEFINED -> UserDefinedOperatorTable.get(name).eval(this, param)
					}
				} catch (ex: NameNotDefinedException) {
					return try {
						VariablePool.get(nodes[0].name())
					} catch (_: NameNotDefinedException) {
						val data = param?.get(nodes[0].name()) ?: throw NameNotDefinedException(ex.name)
						when (data.type()) {
							DataType.VALUE -> data
							DataType.POINTER -> data.pointer().eval(param)
							DataType.NAME -> {
								val name = data.name()
								try {
									val type = OperatorTypeCache.get(data.name())
									when (type) {
										OperatorType.SIMPLE -> SimpleOperatorTable.get(name)()
										OperatorType.MONADIC -> MonadicOperatorTable.get(name)(nodes[1], param)
										OperatorType.DYADIC -> DyadicOperatorTable.get(name)(nodes[1], nodes[2], param)
										OperatorType.TRIADIC -> TriadicOperatorTable.get(name)(nodes[1], nodes[2], nodes[3], param)
										OperatorType.COMPLEX -> ComplexOperatorTable.get(name)(this, param)
										OperatorType.USER_DEFINED -> UserDefinedOperatorTable.get(name).eval(this, param)
									}
								} catch (ex: NameNotDefinedException) {
									return param[ex.name] ?: VariablePool.get(ex.name)
								}
							}
							else -> throw NameNotDefinedException(ex.name)
						}
					}
				}
			}
			NodeType.NODE_GROUP_POINTER -> {
				return if(length() == 1)
					nodes[0].eval(param)
				else {
					for (node in nodes) {
						node.eval(param)
					}
					NullData
				}
			}
			NodeType.NUMBER_LITERAL -> return nodes[0].eval(param)
			else -> throw RuntimeException()
		}
	}

	fun length(): Int = nodes.size

	fun father(): NodeGroup? = father

	fun add(node: Node){
		nodes += node
	}

	fun nodes() = nodes

	private var nodes: Array<Node> = arrayOf()
	var count: Int = 1
}
