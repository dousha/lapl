package edu.guet.gnuforce

private val numComp = "0123456789.-"

fun isNumber(str: String): Boolean {
	var hasSign = false
	var hasDot = false
	var testStr = str
	if (testStr.startsWith("-")){
		if(testStr.length == 1) return false
		hasSign = true
		testStr.removePrefix("-")
	}
	testStr.forEach {
		if(!numComp.contains(it)) return false
		if(it == '.' && hasDot) return false
		else hasDot = true
	}
	return true
}