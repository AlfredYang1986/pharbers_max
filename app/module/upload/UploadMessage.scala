package module.upload

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request}

abstract class msgUploadCommand extends CommonMessage("user", UploadModule)
object UploadMessage {
	case class msgQueryWithUserCompanyForUpload(data: JsValue) extends msgUploadCommand
}
