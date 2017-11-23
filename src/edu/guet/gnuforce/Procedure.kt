package edu.guet.gnuforce

class Procedure(private val signature: NodeGroup, private val body: NodeGroup) {
	fun eval(call: NodeGroup, param: HashMap<String, Double>?): Double {
		var i = 1
		return if(param == null) {
			val sub: HashMap<String, Double> = HashMap()
			while (i < signature.length()) {
				sub.put(signature.nodes()[i].name(), call.nodes()[i].eval(null))
				++i
			}
			body.eval(sub)
		} else {
			while (i < signature.length()) {
				param.put(signature.nodes()[i].name(), call.nodes()[i].eval(param))
				++i
			}
			body.eval(param)
		}
	}

	fun paramCount() = paramCount

	private val paramCount = signature.length() - 1
}