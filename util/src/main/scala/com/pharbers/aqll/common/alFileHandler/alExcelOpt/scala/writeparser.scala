package com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.java.ReflectUtil
import com.pharbers.aqll.old.calc.util.export.ExcelDataFormatter
import org.apache.poi.ss.usermodel.Workbook

/**
  * Created by qianpeng on 2017/5/15.
  */
trait writeparser[T] extends excelwriteparser[T] {
	override def getWorkBook(lst: List[T]): Workbook = {
		if(!lst.isEmpty) {
			createSheet
			val fields = ReflectUtil.getClassFieldsAndSuperClassFields(lst.head.getClass)
			setTitle(fields)
			setCell(fields, lst)
		}
		wb
	}
}