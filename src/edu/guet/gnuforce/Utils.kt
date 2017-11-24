package edu.guet.gnuforce

private val numComp = "0123456789"

fun isNumber(str: String): Boolean {
	val hasSign = str[0] == '-'
	if(hasSign && str.length == 1) return false
	var hasDot = false
	var i = if (hasSign) 1 else 0
	while(i < str.length){
		if(!numComp.contains(str[i])){
			if (!hasDot && str[i] == '.') hasDot = true
			else return false
		}
		++i
	}
	return true
}