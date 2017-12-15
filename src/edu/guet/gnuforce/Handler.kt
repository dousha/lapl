package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.FunctionNotSupportedException
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

abstract class Handler {
	abstract fun read(): Data
	abstract fun write(content: Data, param: HashMap<String, Data>?): Boolean
	abstract fun lock()
	abstract fun unlock()
	abstract fun good(): Data
	override abstract fun toString(): String
}

class StatusHandler : Handler() {
	// Mutex
	override fun read(): Data = Data(DataType.BOOL, lock)

	override fun write(content: Data, param: HashMap<String, Data>?): Boolean = throw FunctionNotSupportedException()

	override fun lock() {
		while (lock); // waits for lock
		lock = true
	}

	override fun unlock() {
		lock = false
	}

	override fun good() = TrueData

	override fun toString(): String = "IsLocked: $lock"

	private var lock: Boolean = false
}

open class FileHandler(path: String) : Handler() {
	override fun read(): Data = Data(DataType.CHAR, file.reader().read().toChar())

	fun readAll() = file.readText()

	fun readLine(offset: Int) = fileContents[offset]

	fun countLines() = fileContents.size

	override fun write(content: Data, param: HashMap<String, Data>?): Boolean {
		if (!file.canWrite()) return false
		try {
			raf.write(content.toString(param).toByteArray())
			fileContents = file.readLines() // refresh file contents
		} catch (io: IOException) {
			return false
		}
		return true
	}

	fun seek(offset: Long) {
		raf.seek(offset)
	}

	fun eof() = raf.length()

	override fun lock() {
		lock.lock()
	}

	override fun unlock() {
		lock.unlock()
	}

	override fun good() = Data(DataType.BOOL, file.canRead())

	override fun toString(): String = file.readText()

	protected fun finalize() {
		raf.close()
	}

	private val file = File(path)
	private val raf = if (file.canRead() && file.canWrite()) RandomAccessFile(file, "rw") else {
		println("!> Opened a read-only file!")
		RandomAccessFile(file, "r")
	}
	private var fileContents = file.readLines()
	private val lock = StatusHandler()
}

class EnvironmentVariableHandler : Handler() {
	override fun read() = Data(DataType.ARRAY, string(envVarValue))

	override fun write(content: Data, param: HashMap<String, Data>?): Boolean {
		envVarValue = System.getenv(content.toString(param))
		return envVarValue.isNotBlank() && envVarValue.isNotEmpty()
	}

	override fun lock() = throw FunctionNotSupportedException()

	override fun unlock() = throw FunctionNotSupportedException()

	override fun good() = TrueData

	override fun toString() = "SysHandler@${this.hashCode()}"

	private lateinit var envVarValue: String
}
