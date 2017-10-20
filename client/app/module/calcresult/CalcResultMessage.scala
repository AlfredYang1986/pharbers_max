package module.calcresult

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class CalcResultMessage extends CommonMessage("calcresult", CalcResultModule)

object CalcResultMessage {
	case class MsgCalcResultCondition(data: JsValue) extends CalcResultMessage
	case class MsgCalcResult(data: JsValue) extends CalcResultMessage
}