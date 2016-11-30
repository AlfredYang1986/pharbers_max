package com.pharbers.aqll.util.errorcode

trait ErrorTrait {
    def getErrMessage : String
}

case class CommonError(val data : (Int, String)) extends ErrorTrait {
    type data_type = (Int, String)
    def getErrMessage = data._2
} 

object ErrorCode {
        
    implicit val defined2ConcretError : ErrorNode => ErrorTrait = node => CommonError((node.code, node.message)) 
    
    case class ErrorNode(name : String, code : Int, message : String)
    
    private def xls : List[ErrorNode] = List(
        new ErrorNode("is null", -1, "数据NUll"),
        new ErrorNode("timeout", -250, "TimeOut"),
        new ErrorNode("unknow error", -9999, "未知错误")
  	)
  	
  	def getErrorCodeByName(name : String) : Int = (xls.find(x => x.name == name)) match {
		case Some(y) => y.code
		case None => -9999
	}
  	
   	def getErrorMessageByName(name : String) : String = (xls.find(x => x.name == name)) match {
		case Some(y) => y.message
		case None => "unknow error"
	}
   	
   	def errorToTrait(name: String)(implicit func : ErrorNode => ErrorTrait) : ErrorTrait = 
        func((xls.find(x => x.name == name)).map (x => x).getOrElse(xls.last))
}