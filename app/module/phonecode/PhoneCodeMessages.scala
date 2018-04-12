package module.phonecode

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class msg_PhoneCodeCommand extends CommonMessage("phoneCode", PhoneCodeModule)

object PhoneCodeMessages {
	case class msg_send_sms_code(data : JsValue) extends msg_PhoneCodeCommand
	case class msg_check_sms_code(data : JsValue) extends msg_PhoneCodeCommand
	case class msg_check_send_time(data: JsValue) extends msg_PhoneCodeCommand
}