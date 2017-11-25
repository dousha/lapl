package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.FunctionNotSupportedException

abstract class Handler {
	abstract fun read(): Char
	abstract fun write(): Boolean
	abstract fun lock()
	abstract fun unlock()
}

class StatusHandler : Handler() { // Mutex
	override fun read(): Char = if (lock) '1' else '0'

	override fun write(): Boolean = throw FunctionNotSupportedException()

	override fun lock() {
		while (lock);
		lock = true
	}

	override fun unlock() {
		lock = false
	}

	private var lock: Boolean = false
}

open class FileHandler(path: String) : Handler() {
	override fun read(): Char {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun write(): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun lock() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun unlock() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

}
