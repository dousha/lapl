package edu.guet.gnuforce

import java.io.File

fun main(args: Array<String>){
	println("|> LAPL Interpreter")
	if(args.count() == 1) {
		val file = File(args[0])
		if(!(file.isFile && file.canRead())){
			error("Cannot open or read file ${args[0]}")
		}
		OperatorTypeCache.init()
		Parser().parse(file)
	} else {
		error("Usage: lapl <file>")
	}
}