package com.pharbers.aqll.excel.core

import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.xml.sax.Attributes

import akka.actor.actorRef2Scala

trait interactparser extends excelparser with exceltitleparser with exceltargetcreator {

	var nextIsString = false
	var lastContents = ""
	var closeV = false

	var rowlist: List[String] = Nil
	var resultlist: List[target_type] = Nil

	var preRef: String = null
	var ref: String = null
	var maxRef: String = null

	def richText2String = {
		if (nextIsString) {
			try {
				val idx = Integer.parseInt(lastContents)
				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString
			} catch {
				case ex: Exception => Unit
			}
		}
	}

	override def endElement(uri: String, localName: String, name: String) = {
		richText2String

		name match {
			case "v" => {
				if (switchbtn == true && hasTop == false) {
					if (!ref.equals(preRef)) {
						val len: Int = countNullCell(ref, preRef)
						for (i <- 1 to len) {
							rowlist = rowlist :+ ""
						}
					}
				}
				val value = if (lastContents.trim.equals("")) " "
				else lastContents.trim
				rowlist = rowlist :+ value
				closeV = true
			}
			case "c" => {
				if (!closeV) {
					rowlist = rowlist :+ ""
				}
			}
			case "row" => {
				// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
				if (maxRef != null) {
					val len: Int = countNullCell(maxRef, ref);
					for (i <- 1 to len) {
						rowlist = rowlist :+ ""
					}
				}
				if (switchbtn == false && hasTop == false) {
					maxRef = ref
					if (rowlist.length != title.length
						&& ((rowlist zip title).map(x => if (x._1 == x._2) 1
					else 0)).sum != title.length) {
						throw new Exception("该标题有误或顺序错乱请重新检查、下载模板！")
						System.err.println("该标题有误或顺序错乱请重新检查、下载模板！")
					} else {
						rowlist = Nil
						switchbtn = true
					}
				} else {
					try {
						val target = targetInstance
						(rowlist zip fields).foreach(x => ReflectUtil.invokeSetter(target, x._2, x._1))
						resultlist = resultlist :+ target
						handleOneTarget(target)
						rowlist = Nil
						switchbtn = true
					} catch {
						case e: Exception => e.printStackTrace()
					}
				}
				preRef = null
				ref = null
			}
			case _ => Unit
		}
	}

	override def startElement(uri: String, localName: String, name: String, attributes: Attributes) = {
		// c => 单元格
		if (name.equals("c")) {
			// 如果下一个元素是 SST 的索引，则将nextIsString标记为true

			// 前一个单元格的位置
			if (preRef == null) {
				preRef = attributes.getValue("r")
			} else {
				preRef = ref
			}

			// 当前单元格的位置
			ref = attributes.getValue("r")

			val cellType = attributes.getValue("t")
			if (cellType != null && cellType.equals("s")) {
				nextIsString = true;
			} else {
				nextIsString = false;
			}
			closeV = false;
		}
		// 置空
		lastContents = "";
	}

	override def characters(ch: Array[Char], start: Int, length: Int) = {
		lastContents += new String(ch, start, length)
	}

	def countNullCell(ref: String, preRef: String): Int = {
		val xfd: String = ref.replaceAll("\\d+", "")
		val xfd_1: String = preRef.replaceAll("\\d+", "")
		val letter: Array[Char] = fillChar(xfd, 3, '@', true).toCharArray()
		val letter_1: Array[Char] = fillChar(xfd_1, 3, '@', true).toCharArray()
		((letter(0) - letter_1(0)) * 26 * 26 + (letter(1) - letter_1(1)) * 26 + (letter(2) - letter_1(2))) - 1
	}

	def fillChar(str: String, len: Int, let: Char, isPre: Boolean): String = {
		val len_1: Int = str.length()
		var str_2 = str
		if (len_1 < len) {
			if (isPre) {
				for (i <- 1 to (len - len_1)) {
					str_2 = let + str_2
				}
			} else {
				for (i <- 1 to (len - len_1)) {
					str_2 = str_2 + let
				}
			}
		}
		str_2
	}

	def handleOneTarget(target: target_type)
}