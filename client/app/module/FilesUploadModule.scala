package module

import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.common.fopConfig._
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.common.alFileHandler.alFilesOpt._
/**
  * Created by Wli on 2017/2/20.
  */
object FilesUploadModuleMessage {
      sealed class msg_filesuploadBase extends CommonMessage
	  case class msg_scpfile(data : JsValue) extends msg_filesuploadBase
    case class msg_removefiles(data : JsValue) extends msg_filesuploadBase
}

object FilesUploadModule extends ModuleTrait {
    import FilesUploadModuleMessage._
    import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_scpfile(data) => msg_scpfile_func(data)
    case msg_removefiles(data) => msg_removefiles_func(data)
		case _ => ???
	}

  def msg_scpfile_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
    try {
      val company = (data \ "company").asOpt[String].get
      val scp_filename = (data \ "filename").asOpt[String].get
      val scp_filepath = s"$fileBase$company$hospitalData"
      //println(company+"-----"+scp_filename)
      scpCmd(s"$scp_filepath$scp_filename",s"$scp_filepath","aliyun106", "root").excute
      scpCmd(s"$scp_filepath$scp_filename",s"$scp_filepath", "aliyun50", "root").excute
      (Some(Map("result" -> toJson("OK"))), None)
    } catch {
      case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
    }
  }

  def msg_removefiles_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
    try {
      val company = (data \ "company").asOpt[String].get
      val cpa_filepath = s"$fileBase$company$client_cpa_file"
      val gycx_filepath = s"$fileBase$company$client_gycx_file"
      //println(s"$cpa_filepath \n $gycx_filepath")
      alFileOpt.apply(cpa_filepath).removeCurFiles
      alFileOpt.apply(gycx_filepath).removeCurFiles
      (Some(Map("result" -> toJson("OK"))), None)
    } catch {
      case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
    }
  }
}