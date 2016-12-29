package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import java.util.Date
import java.util.Calendar
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import play.api.libs.json.JsObject

import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_basic
import com.pharbers.aqll.util.MD5
import java.text.SimpleDateFormat
import java.util.Date

object FilesUploadModuleMessage {
      sealed class msg_filesuploadBase extends CommonMessage
	  case class msg_filesupload(data : JsValue) extends msg_filesuploadBase
}

object FilesUploadModule extends ModuleTrait {
    import FilesUploadModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_filesupload(data) => msg_filesupload_func(data)
		case _ => ???
	}
    
    def msg_filesupload_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        val uuid = (data \ "uuid").asOpt[String].get
        val filetype = (data \ "Datasource_Type").asOpt[String].get
        val bulk = _data_connection_basic.getCollection("DataSources").initializeUnorderedBulkOperation
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")  
        val longvalue = dateFormat.parse(dateFormat.format(new Date())).getTime
        bulk.insert(Map("Datasource_Id" -> MD5.md5(uuid), "Datasource_Type" -> filetype, "File_Path" -> uuid, "Creation_Date" -> longvalue))
        bulk.execute()
        (Some(Map("uploadfiles" -> toJson("ok"))), None)
    }
}