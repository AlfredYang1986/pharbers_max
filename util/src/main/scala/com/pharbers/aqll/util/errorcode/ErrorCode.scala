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
        new ErrorNode("file not exist", -1, "文件不存在"),
        new ErrorNode("unknown error", -999, "未知错误")
  	)
  	
   	def errorToTrait(name: String)(implicit func : ErrorNode => ErrorTrait) : ErrorTrait = 
        func((xls.find(x => x.name == name)).map (x => x).getOrElse(xls.last))
}