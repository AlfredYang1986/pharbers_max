package com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala

import java.io.FileInputStream
import java.lang.reflect.Field
import java.util.Date

import akka.actor.ActorRef
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.java.{Excel, ReflectUtil}
import com.pharbers.aqll.old.calc.util.export.ExcelDataFormatter
import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.apache.poi.xssf.model.SharedStringsTable
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.xml.sax.helpers.{DefaultHandler, XMLReaderFactory}
import org.xml.sax.{Attributes, InputSource, XMLReader}

trait excelparser extends DefaultHandler {
	val a: ActorRef

	var sst: SharedStringsTable = null
	var hasTop: Boolean = false
	var switchbtn: Boolean = false

	def startParse(filename: String, optSheetIndex: Int = 1, ht: Boolean = false, sb: Boolean = false) = {

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

trait excelwriteparser[T] {

	var columnIndex = 0

	var rowIndex = 1

	var excel: Excel = null

	val wb = createWb

	var row: Row = null

	var cell: Cell = null

	val cs: CellStyle = wb.createCellStyle

	val createHelper: CreationHelper = wb.getCreationHelper

	def getWorkBook(lst: List[T]): Workbook

	def createWb: Workbook = new SXSSFWorkbook(1000)

	var sheet: Option[Sheet] = None

	var sheetNum = 0

	def createSheet = {
		sheet = Some(wb.createSheet)
		row = sheet.get.createRow(0)
		wb.setSheetName(0, s"Sheet${sheetNum + 1}")
	}

	def setTitle(title: Array[Field]) = {
		columnIndex = 0
		title foreach { x =>
			x.setAccessible(true)
			excel = x.getAnnotation(classOf[Excel])
			if(excel != null || excel.skip() != true) {
				// 列宽注意乘256
				sheet.get.setColumnWidth(columnIndex, excel.width() * 256)
				cell = row.createCell(columnIndex)
				cell.setCellType(Cell.CELL_TYPE_STRING)
				cell.setCellValue(excel.name)
				columnIndex += 1
			}
		}
	}

	def setCell(fields: Array[Field], lst: List[T]) = {
		var num = 0
		val list = lst.iterator
		while (list.hasNext){
			if(num >= 1000000) {
				sheetNum += 1
				createSheet
				setTitle(fields)
				num = 0
				rowIndex = 1
			}
			val t: T = list.next()
			row = sheet.get.createRow(rowIndex)
			columnIndex = 0
			fields foreach { x =>
				x.setAccessible(true)
				excel = x.getAnnotation(classOf[Excel])
				if(excel != null || excel.skip() != true) {
					cell = row.createCell(columnIndex)
					val o = x.get(t)
					if(o != null) {
						format(o)
					}
				}
				columnIndex += 1
			}
			rowIndex += 1
			num += 1
		}
	}


	def format(attr: Any) = {
		attr match {
			case x: Date =>
				cs.setDataFormat(createHelper.createDataFormat.getFormat(excel.dateFormat))
				cell.setCellStyle(cs)
				cell.setCellValue(x)
			case x if(x.isInstanceOf[Double] || x.isInstanceOf[Float]) =>
				cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"))
				cell.setCellStyle(cs)
				cell.setCellValue(x.asInstanceOf[Double])
			case x: Int =>
				cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"))
				cell.setCellStyle(cs)
				cell.setCellValue(x)
			case x: String =>
				cell.setCellValue(x)
			case _ => ???
		}
	}
}