package edu.guet.gnuforce

import edu.guet.gnuforce.exceptions.NameNotDefinedException
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

class Parser {
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
			println("!> Syntax error: brackets mismatch!\n")
			return
		}
		println("|> Parsing...")
		file.readLines(Charset.forName("UTF-8")).filter { !it.trim().startsWith('#') }.forEach {
			var i = 0
			val line = it.trim()
			while(i < line.length){
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
					val word = line.removeRange(0 until i).takeWhile { it != ' ' && it != ')'}
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
		println("|> Starting...")
		try {
			curGroup.eval(null)
		} catch (ex: NameNotDefinedException){
			println("!> Undefined name: `${ex.name}'@blk#${curGroup.count}")
			exitProcess(-1)
		}
		println("-> Done.")
	}

	private var depth = 0
	private var curGroup: NodeGroup = NodeGroup(null)
}