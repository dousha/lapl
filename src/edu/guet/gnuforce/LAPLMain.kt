package edu.guet.gnuforce

import java.io.File

fun main(args: Array<String>){
	println("|> LAPL Interpreter")
	if (args.count() >= 1) {
		val file = File(args[0])
		if(!(file.isFile && file.canRead())){
			error("Cannot open or read file ${args[0]}")
		}
		OperatorTypeCache.init()
		val paramArr = LAPLArray(Pair(1, args.count() - 1), arrayOf())
		for (arg in args.sliceArray(1 until args.size)) {
			paramArr.content += Data(DataType.CHAR, arg)
		}
		Parser().parse(file)
	} else {
		error("Usage: lapl <file>")
	}
}