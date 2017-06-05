package module

import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd
import play.api.libs.json.JsValue
import com.pharbers.aqll.common.alFileHandler.alFilesOpt._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.dbmodule.MongoDBModule
/**
  * Created by Wli on 2017/2/20.
  */
object FilesUploadModuleMessage {
    sealed class msg_filesuploadBase extends CommonMessage
	  case class msg_scpCopyFiles(data : JsValue) extends msg_filesuploadBase
    case class msg_removeFiles(data : JsValue) extends msg_filesuploadBase
}

object FilesUploadModule extends ModuleTrait {
    import FilesUploadModuleMessage._
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit db: MongoDBModule) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_scpCopyFiles(data) => scpCopyFiles_func(data)
    case msg_removeFiles(data) => removeFiles_func(data)
		case _ => ???
	}

  /**
    * @author liwei
    * @param data
    * @return
    */
  def scpCopyFiles_func(data : JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
    try {
      val company = (data \ "company").asOpt[String].getOrElse(throw new Exception("error input"))
      val scp_filename = (data \ "filename").asOpt[String].getOrElse(throw new Exception("error input"))

      val scp_filepath = s"$root$program$fileBase$company$hospitalData"

      val scp106result = scpCmd(s"$scp_filepath$scp_filename",s"$scp_filepath","aliyun106", "root").excute
      (scp106result \ "status").get.asOpt[String].get match {
        case "success" => (successToJson(), None)
        case "error" => throw new Exception("warn aliyun106 scp copy file failed")
      }

      val scp50result = scpCmd(s"$scp_filepath$scp_filename",s"$scp_filepath", "aliyun50", "root").excute
      (scp50result \ "status").get.asOpt[String].get match {
        case "success" => (successToJson(), None)
        case "error" => throw new Exception("warn aliyun50 scp copy file failed")
      }
    } catch {
      case ex : Exception => (None, Some(errorToJson(ex.getMessage())))
    }
  }

  /**
    * @author liwei
    * @param data
    * @return
    */
  def removeFiles_func(data : JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
    try {
      val company = (data \ "company").asOpt[String].getOrElse(throw new Exception("error input"))

      alFileOpt.apply(s"$root$program$fileBase$company$client_cpa_file").removeCurFiles match {
        case true => (successToJson(), None)
        case false => throw new Exception("warn cpa file delete failed")
      }
      alFileOpt.apply(s"$root$program$fileBase$company$client_gycx_file").removeCurFiles match {
        case true =>  (successToJson(), None)
        case false => throw new Exception("warn gycx file delete failed")
      }
    } catch {
      case ex : Exception => (None, Some(errorToJson(ex.getMessage())))
    }
  }
}