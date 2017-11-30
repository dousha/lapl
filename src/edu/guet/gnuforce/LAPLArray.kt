package edu.guet.gnuforce

import java.util.*

data class LAPLArray(var shape: Pair<Int, Int>, var content: Array<Data>) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as LAPLArray

		if (shape != other.shape) return false
		if (!Arrays.equals(content, other.content)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = shape.hashCode()
		result = 31 * result + Arrays.hashCode(content)
		return result
	}

	fun append(data: Data) {
		content += data
	}

	fun toString(param: HashMap<String, Data>?): String {
		val sb = StringBuilder()
		if (content[0].type() == DataType.CHAR) {
			for (element in content) {
				sb.append(element.char())
			}
		} else {
			for (element in content) {
				sb.append(element.toString(param))
				sb.append(' ')
			}
		}
		return sb.toString()
	}
}

fun range(from: Int, to: Int): LAPLArray {
	val array = LAPLArray(Pair(1, to - from), arrayOf())
	var i = from
	while (i <= to) {
		array.append(Data(DataType.VALUE, i))
		++i
	}
	return array
}

fun string(str: String): LAPLArray {
	val array = LAPLArray(Pair(1, str.length), arrayOf())
	for (char in str.toCharArray()) {
		array.content += Data(DataType.CHAR, char)
	}
	return array
}