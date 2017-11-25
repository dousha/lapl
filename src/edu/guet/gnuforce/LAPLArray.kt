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

	fun dropAt(index: Int) {
		content.drop(index)
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