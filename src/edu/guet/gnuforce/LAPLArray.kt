package edu.guet.gnuforce

import java.util.*

data class LAPLArray(var shape: Pair<Int, Int>, val content: Array<Data>) {
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
}