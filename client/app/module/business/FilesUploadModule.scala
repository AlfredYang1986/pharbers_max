package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_basic
import com.pharbers.aqll.util.{GetProperties, MD5}
import java.text.SimpleDateFormat
import java.util.Date
import java.io._
import play.api.libs.Files
import com.pharbers.aqll.util.CopyFileUtil._
/**
  * Created by Wli on 2017/2/20.
  */
object FilesUploadModuleMessage {
      sealed class msg_filesuploadBase extends CommonMessage
	  case class msg_filesupload(data : JsValue) extends msg_filesuploadBase
    case class msg_filesexists(data : JsValue) extends msg_filesuploadBase
    case class msg_classifyfiles(data : JsValue) extends msg_filesuploadBase
}

object FilesUploadModule extends ModuleTrait {
    import FilesUploadModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_filesupload(data) => msg_filesupload_func(data)
    case msg_filesexists(data) => msg_filesexists_func(data)
    case msg_classifyfiles(data) => msg_classifyfiles_func(data)
		case _ => ???
	}
    
    def msg_filesupload_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
          val filename = (data \ "uuid").asOpt[String].get
          val company = (data \ "company").asOpt[String].get
          val Datasource_Type = (data \ "Datasource_Type").asOpt[String].get
          val findResult = _data_connection_basic.getCollection("DataSources").distinct("File_Path").find(x => x.equals(filename))
          findResult match {
            case None => {
              val bulk = _data_connection_basic.getCollection("DataSources").initializeUnorderedBulkOperation
              val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
              val longvalue = dateFormat.parse(dateFormat.format(new Date())).getTime
              bulk.insert(Map("Datasource_Id" -> MD5.md5(filename),
                "Datasource_Type" -> Datasource_Type,
                "File_Path" -> filename,
                "Creation_Date" -> longvalue,
                "Company" -> company))
              bulk.execute()
            }
            case _ => println("The file already exists, written to fail.")
          }
          (Some(Map("uploadfiles" -> toJson("ok"))), None)
        } catch {
          case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }


    def msg_filesexists_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
      try {
        val filename = (data \ "filename").asOpt[String].get
        val file : File = new File(GetProperties.Client_Download_FilePath)
        println(filename)
        var flag = false
        file.listFiles().foreach{ x=>
          if(x.getName.equals(filename))
            flag = true
        }
        (Some(Map("result" -> toJson(flag))), None)
      } catch {
        case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
      }
    }

    def msg_classifyfiles_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
      try {
        var filename = (data \ "filename").asOpt[String].map (x => x).getOrElse("")
        var company = (data \ "company").asOpt[String].map (x => x).getOrElse("")
        var year = (data \ "year").asOpt[String].map (x => x).getOrElse("")
        var filetype = (data \ "filetype").asOpt[String].map (x => x).getOrElse("")

        println(s"filename=${filename} company=${company} year=${year} datatype=${filetype}")

        var newfilepath = ""
        filetype match {
          case "CPAP" => {
            newfilepath = GetProperties.Upload_CPA_Product_FilePath
          }
          case "CPAM" => {
            newfilepath = GetProperties.Upload_CPA_Market_FilePath
          }
          case "PTP" => {
            newfilepath = GetProperties.Upload_PT_Product_FilePath
          }
          case "PTM" => {
            newfilepath = GetProperties.Upload_PT_Market_FilePath
          }
        }
        copyFile(GetProperties.Client_Upload_FilePath + filename,newfilepath + year+ "/" +company+ "/" + filename,true)
        (Some(Map("result" -> toJson("ok"))), None)
      } catch {
        case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
      }
    }
}