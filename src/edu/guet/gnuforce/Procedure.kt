package edu.guet.gnuforce

class Procedure(private val signature: NodeGroup, private val body: NodeGroup) {
	fun eval(call: NodeGroup, param: HashMap<String, Data>?): Data {
		var i = 1
		return if(param == null) {
			val sub: HashMap<String, Data> = HashMap()
			while (i < signature.length()) {
				val callerNode = call.nodes()[i]
				sub.put(signature.nodes()[i].name(),
						when(callerNode.type()){
							NodeType.NUMBER_LITERAL -> Data(DataType.VALUE, callerNode.eval(null).number())
							NodeType.NODE_GROUP_POINTER -> {
								if (signature.nodes()[i].name().startsWith('@'))
									Data(DataType.POINTER, callerNode)
								else
									Data(DataType.VALUE, callerNode.eval(null).name())
							}
							NodeType.STRING_TOKEN -> Data(DataType.NAME, callerNode.name())
						})
				++i
			}
			body.eval(sub)
		} else {
			while (i < signature.length()) {
				val callerNode = call.nodes()[i]
				param.put(signature.nodes()[i].name(), when(callerNode.type()){
					NodeType.NUMBER_LITERAL -> callerNode.eval(param)
					NodeType.NODE_GROUP_POINTER -> {
						if(signature.nodes()[i].name().startsWith("@"))
							Data(DataType.POINTER, callerNode.pointer())
						else
							Data(DataType.VALUE, callerNode.eval(param).number())
					}
					NodeType.STRING_TOKEN -> Data(DataType.NAME, callerNode.name())
				})
				++i
			}
			body.eval(param)
		}
	}

	fun paramCount() = paramCount

	private val paramCount = signature.length() - 1
}