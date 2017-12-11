package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.DividedByZeroException
import edu.guet.gnuforce.exceptions.NameNotDefinedException
import edu.guet.gnuforce.exceptions.ParameterMismatchException
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset
import kotlin.system.exitProcess

class Parser(private val silent: Boolean = false) {
	private fun scan(file: File): Boolean {
		var pCount = 0
		file.readLines(Charset.forName("UTF-8")).filter { !it.trim().startsWith('#') }.forEach {
			var i = 0
			val line = it.trim()
			while(i < line.length){
				if(line[i] == '#') break
				else if(line[i] == '(') ++pCount
				else if(line[i] == ')') --pCount
				++i
			}
		}
		return pCount == 0
	}

	fun parse(file: File) {
		if(!scan(file)) {
			println("!> ${file.name}: Syntax error: brackets mismatch!\n")
			return
		}
		if (!silent) println("|> Parsing...")
		file.readLines(Charset.forName("UTF-8")).filter { !it.trim().startsWith('#') }.forEach {
			var i = 0
			val line = it.trim()
			while(i < line.length){
				if (line[i] == '"') {
					// into the string literal mode
					val tail = line.lastIndexOf('"')
					val literal = line.substring(i, tail + 1)
					curGroup.add(Node(NodeType.NAME, stringEscape(literal)))
					i = tail + 1
					continue
				}
				if(line[i] == '#') break
				if(line[i] == '('){
					++depth
					++i
					val newGroup = NodeGroup(curGroup)
					curGroup.add(Node(NodeType.POINTER, newGroup))
					curGroup = newGroup
					continue
				}
				else if(line[i] == ')'){
					--depth
					++i
					curGroup = curGroup.father() ?: curGroup
					continue
				}
				else{
					val word = line.removeRange(0 until i).takeWhile { it != ' ' && it != ')' }
					if(word.isBlank() || word.isEmpty()){
						++i
					} else {
						if(isNumber(word)){
							curGroup.add(Node(NodeType.VALUE, word.toDouble()))
						} else {
							curGroup.add(Node(NodeType.NAME, word))
						}
						i += word.length
					}
				}
			}
		}
		if (!silent) println("|> Starting...")
		try {
			curGroup.eval(null)
		} catch (ex: NameNotDefinedException){
			println("!> Undefined name: `${ex.name}'@${file.name}#${curGroup.count}")
			ex.printStackTrace()
			exitProcess(-1)
		} catch (overflow: StackOverflowError){
			println("!> Stack overflow when evaluating ${file.name}#${curGroup.count}")
			exitProcess(-1)
		} catch (mismatch: ParameterMismatchException) {
			println("!> Parameter count mismatch when evaluating ${file.name}#${curGroup.count}")
			exitProcess(-1)
		} catch (zero: DividedByZeroException) {
			println("!> Divided by zero when evaluating ${file.name}#${curGroup.count}")
			exitProcess(1)
		} catch (file404: FileNotFoundException) {
			println("!> Cannot found external file when evaluating ${file.name}#${curGroup.count}")
			exitProcess(2)
		}
		if (!silent) println("-> Done.")
	}

	private var depth = 0
	private var curGroup: NodeGroup = NodeGroup(null)
}
