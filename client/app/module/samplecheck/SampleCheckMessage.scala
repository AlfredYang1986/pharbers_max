package module.samplecheck

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class MsgSampleCheckCommand extends CommonMessage("samplecheck", SampleCheckModule)

object SampleCheckMessage {
	
	case class MsgQuerySelectBoxValue(data: JsValue) extends MsgSampleCheckCommand
	
	case class MsgQueryHospitalNumber(data: JsValue) extends MsgSampleCheckCommand
	
	case class MsgQueryProductNumber(data: JsValue) extends MsgSampleCheckCommand
	
	case class MsgQuerySampleProductNumber(data: JsValue) extends MsgSampleCheckCommand
	
	case class MsgQueryNotSampleHospital(data: JsValue) extends MsgSampleCheckCommand
}
