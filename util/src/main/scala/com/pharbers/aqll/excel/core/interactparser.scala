package com.pharbers.aqll.excel.core

import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.xml.sax.Attributes

trait interactparser extends excelparser with exceltitleparser with exceltargetcreator {

	var nextIsString = false
	var lastContents = ""
	var closeV = false
	
	var rowlist : List[String] = Nil
	var resultlist: List[target_type] = Nil

	def richText2String = {
		if (nextIsString) {
			try {
				val idx = Integer.parseInt(lastContents)
				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString
			} catch {
				case ex : Exception => Unit
			}
		}
	}
	
	override def endElement(uri : String, localName : String, name : String) = {
		richText2String

		name match {
			case "v" => {
				val value = if (lastContents.trim.equals("")) " "
							else lastContents.trim
				rowlist = rowlist :+ value
				closeV = true
			}
			case "c" => {
				if(!closeV){
					rowlist = rowlist :+ ""
				}
			}
			case "row" => {		// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
				if (switchbtn == false && hasTop == false) {
					if(rowlist.length != title.length
						&& ((rowlist zip title).map ( x => if (x._1 == x._2) 1
											   		else 0)).sum != title.length) {
					
						System.err.println("该标题有误或顺序错乱请重新检查、下载模板！")
					}else{
					    rowlist = Nil
					    switchbtn = true
					}
				} else {
					try {
						val target = targetInstance
						(rowlist zip fields).foreach ( x => ReflectUtil.invokeSetter(target, x._2, x._1))
						resultlist = resultlist :+ target
						handleOneTarget(target)
						rowlist = Nil
						switchbtn = true
					} catch {
						case e : Exception => e.printStackTrace()
					}
				}
			}
			case _ => Unit
		}
	}
	
	override def startElement(uri : String, localName : String, name : String, attributes : Attributes) = {
		// c => 单元格
		if (name.equals("c")) {
			// 如果下一个元素是 SST 的索引，则将nextIsString标记为true
			val cellType = attributes.getValue("t");
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

	override def characters(ch : Array[Char], start : Int, length : Int) = {
		lastContents += new String(ch, start, length)
	}
	
	def handleOneTarget(target : target_type)
}