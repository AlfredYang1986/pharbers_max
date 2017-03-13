package com.pharbers.aqll.calc.excel.core

import java.io.FileInputStream

import akka.actor.ActorRef
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.apache.poi.xssf.model.SharedStringsTable
import org.xml.sax.{Attributes, InputSource, XMLReader}
import org.xml.sax.helpers.{DefaultHandler, XMLReaderFactory}

trait excelparser extends DefaultHandler {
	val a: ActorRef

	var sst: SharedStringsTable = null
	var hasTop: Boolean = false
	var switchbtn: Boolean = false

	def startParse(filename: String, optSheetIndex: Int, ht: Boolean = false, sb: Boolean = false) = {

		hasTop = ht
		switchbtn = sb

		val pkg = OPCPackage.open(new FileInputStream(filename))
		val r = new XSSFReader(pkg)
		sst = r.getSharedStringsTable()
		val parser = fetchSheetParser

		val sheet = r.getSheet("rId" + optSheetIndex)
		parser.parse(new InputSource(sheet))
		sheet.close()
		System.gc()
	}

	def fetchSheetParser: XMLReader = {
		val parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser")
		parser.setContentHandler(this)
		parser
	}

	override def endElement(uri: String, localName: String, name: String)

	override def startElement(uri: String, localName: String, name: String, attr: Attributes)

	override def characters(ch: Array[Char], start: Int, length: Int)
}

trait exceltitleparser {
	val xml_file_name: String
	val xml_file_name_ch: String

	def f(fn: String): List[String] = ((xml.XML.loadFile(fn) \ "title").map(x => x.text)).toList

	lazy val fields = f(xml_file_name)
	lazy val title = f(xml_file_name_ch)
}

trait exceltargetcreator {
	type target_type

	def targetInstance: target_type
}