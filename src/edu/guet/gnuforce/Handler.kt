package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.FunctionNotSupportedException
import java.io.File
import java.io.IOException

abstract class Handler {
	abstract fun read(): Char
	abstract fun write(content: Data): Boolean
	abstract fun lock()
	abstract fun unlock()
	abstract fun good(): Double
	override abstract fun toString(): String
}

class StatusHandler : Handler() {
	// Mutex
	override fun read(): Char = if (lock) '1' else '0'

	override fun write(content: Data): Boolean = throw FunctionNotSupportedException()

	override fun lock() {
		while (lock); // waits for lock
		lock = true
	}

	override fun unlock() {
		lock = false
	}

	override fun good() = 1.0

	override fun toString(): String = "IsLocked: $lock"

	private var lock: Boolean = false
}

open class FileHandler(path: String) : Handler() {
	override fun read(): Char = file.reader().read().toChar()

	fun readAll() = file.readText()

	override fun write(content: Data) = write(content, null)

	fun write(content: Data, param: HashMap<String, Data>?): Boolean {
		if (!file.canWrite()) return false
		try {
			file.writer().write(content.toString(param))
		} catch (io: IOException) {
			return false
		}
		return true
	}

	override fun lock() {
		lock.lock()
	}

	override fun unlock() {
		lock.unlock()
	}

	override fun good() = if (file.canRead()) 1.0 else 0.0

	override fun toString(): String = file.readText()

	private val file = File(path)
	private val lock = StatusHandler()
}
