package com.pharbers.aqll.calc.excel.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ReadExcel2007<T> extends DefaultHandler{
	private ReadExcel2007 hander;
	private OPCPackage pkg;
	private int sheetIndex = -1;
	private List<String> rowlist = new ArrayList<String>();
	private int curRow = 0;
	private int curCol = 0;
	private String fileName;
	
	private SharedStringsTable sst;
	private String lastContents;
	private boolean nextIsString;
	private boolean closeV=false;
	
	private boolean switchbtn = false;//判断读取标题的开关 为false的时候=打开状态 true的时候=关闭,客户要求的对比title是否有错误
	private List list = new ArrayList<>();//存数返回来的list
	private Class clazz = null;//用于类映射
	private String[] fieldNames = null;
	private String[] title = null;
	private boolean hasTitle;
	private boolean hasTop;
	
	public void setSst(SharedStringsTable sst) {
		this.sst = sst;
	}
	
	public ReadExcel2007(String fileName){
		this.fileName = fileName;
	}

	/***
	 * 读取2007以上的Excel方法
	 * @param hander
	 * @param clazz
	 * @param sheetNo
	 * @param hasTitle
	 * @param hasTop
	 * @param fieldNames
	 * @param title
	 * @return
	 */
	public <T> List<T> readExcel(ReadExcel2007 hander, Class<T> clazz, int sheetNo, boolean hasTitle, boolean hasTop, String[] fieldNames, String[] title){
		this.hander = hander;
		this.clazz = clazz;
		this.fieldNames = fieldNames;
		this.title = title;
		this.hasTitle = hasTitle;
		this.hasTop = hasTop;
		try {
			processByRow(sheetNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// 得到单元格内容的值
		lastContents += new String(ch, start, length);
	}
	
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		// 根据SST的索引值的到单元格的真正要存储的字符串
		// 这时characters()方法可能会被调用多次
		if (nextIsString) {
			try {
				int idx = Integer.parseInt(lastContents);
				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
			} catch (Exception e) {
			}
		}
		if (name.equals("v")) {
			String value = lastContents.trim();
			value = value.equals("") ? " " : value;
			rowlist.add(curCol, value);
			curCol++;
			closeV=true;
		} else {
			if(name.equals("c")){
				if(!closeV){
					rowlist.add(curCol, "");
					curCol++;
				}
			}
			// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
			if (name.equals("row")) {
				// 生成实例并通过反射调用setter方法
				T target = null;
				XRow row=new XRow();
				for(int i=0;i<rowlist.size();i++){
					//2016-1-25    在这里进行判断和插入对象
					if(switchbtn ==false && hasTop==false){
						if(!rowlist.get(i).toString().trim().equals(title[i]) && rowlist.get(i).toString().indexOf(title[i]) == -1){
							// 生成实例并通过反射调用setter方法
							try {
								target = (T) clazz.newInstance();
								System.err.println(rowlist.get(i).toString()+":该标题有误或顺序错乱请重新检查、下载模板！");
								//ReflectUtil.invokeSuperSetter(target, "exceptionString", rowlist.get(i).toString()+":该标题有误或顺序错乱请重新检查、下载模板");
								//stop();
							} catch (Exception e) {
								System.err.println(rowlist.get(i).toString()+":"+e.getMessage());
								try {
									//ReflectUtil.invokeSuperSetter(target, "exceptionString", rowlist.get(i).toString()+":"+e.getMessage());
									//stop();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								e.printStackTrace();
								
							}
						}
					}else{
						try {
							if(target == null){
								target = (T) clazz.newInstance();
							}
							ReflectUtil.invokeSetter(target, fieldNames[i], rowlist.get(i).toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if(target != null){
					list.add(target);
				}
				rowlist.clear();
				curRow++;
				curCol = 0;
				switchbtn = true;
			}
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		// c => 单元格
		if (name.equals("c")) {
			// 如果下一个元素是 SST 的索引，则将nextIsString标记为true
			String cellType = attributes.getValue("t");
			if (cellType != null && cellType.equals("s")) {
				nextIsString = true;
			} else {
				nextIsString = false;
			}
			closeV=false;
		}
		// 置空
		lastContents = "";
	}
	
	private boolean isBlankRow(XRow row){
		boolean b=true;
		for(int i=0;i<row.getCellsSize();i++){
			XCell cell=row.getCell(i);
			if(StringUtil.hasValue(cell.getValue())){
				b=false;
			}
		}
		return b;
	}
	
	/**
	 * 辅助实现方法，xml解析
	 * @param sst
	 * @return
	 * @throws SAXException
	 */
	private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		hander.setSst(sst);
		parser.setContentHandler(hander);
		return parser;
	}
	
	/**
	 * 处理所有sheet
	 */
	public void processByRow() throws Exception {
		curRow = 0;
		pkg = OPCPackage.open(new FileInputStream(fileName));
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		Iterator<InputStream> sheets = r.getSheetsData();
		while (sheets.hasNext()) {
			curRow = 0;
			sheetIndex++;
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
		
	}

	/**
	 * 处理指定索引的sheet
	 */
	public void processByRow(int optSheetIndex) throws Exception {
		curRow = 0;
		pkg = OPCPackage.open(new FileInputStream(fileName));
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();

		XMLReader parser = fetchSheetParser(sst);

		// rId2 found by processing the Workbook
		// 根据 rId# 或 rSheet# 查找sheet
		InputStream sheet = r.getSheet("rId" + optSheetIndex);
		sheetIndex++;
		parser.parse(new InputSource(sheet));
		sheet.close();
		System.gc();
		switchbtn = false;//指定的Sheet里的内容全部处理完毕
	}
	
	public void stop() throws IOException{
		if(pkg != null){
			pkg .close();
		}
	}
}
