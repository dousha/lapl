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

fun stringEscape(str: String): String {
	var i = 0
	val strBuilder = StringBuilder()
	while (i < str.length) {
		if (str[i] == '\\') {
			if (str[i + 1] != '\u0000') {
				val escapeIndicator = str[i + 1]
				strBuilder.append(
						when (escapeIndicator) {
							'n' -> '\n'
							'r' -> '\r'
							'\\' -> '\\'
							'_' -> System.lineSeparator()
							'u' -> {
								val num = Integer.valueOf(str.substring(i + 1, i + 5), 16)
								i += 6
								num.toChar()
							}
							'x' -> {
								val num = Integer.valueOf(str.substring(i + 1, i + 3), 16)
								num.toChar()
								i += 4
							}
							't' -> '\t'
							else -> str.substring(i, i + 2)
						}
				)
				i += 2
				continue
			}
		} else {
			strBuilder.append(str[i])
		}
		++i
	}
	return strBuilder.toString()
}