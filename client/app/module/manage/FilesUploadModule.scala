package module.manage

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import com.pharbers.aqll.excel.dispose.WriteInData

object ManageFilesUploadModuleMessage {
    sealed class msg_managefilesuploadBase extends CommonMessage
	  case class msg_managefilesupload(data : JsValue) extends msg_managefilesuploadBase
}

object ManageFilesUploadModule extends ModuleTrait {
    import ManageFilesUploadModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_managefilesupload(data) => msg_managefilesupload_func(data)
		case _ => ???
	}
    
  def msg_managefilesupload_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
      println("Write Data Start.")
      val uuid = (data \ "uuid").asOpt[String].get
      val company = (data \ "company").asOpt[String].get
      val filetype = (data \ "Datasource_Type").asOpt[String].get
      try {
        filetype match {
          case "医院数据" => {
            WriteInData.insertHospitalCoresInfo("D:\\SourceData\\Manage\\"+filetype+"\\"+uuid)
          }
          case "产品匹配" => {
            WriteInData.insertProductsCoresInfo("D:\\SourceData\\Manage\\"+filetype+"\\"+uuid)
          }
          case _ => println("upload file succeed.")
        }
        println("Write Data Start.")
        (Some(Map("result" -> toJson("入库成功"))), None)
      } catch {
        case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
      }
  }
}