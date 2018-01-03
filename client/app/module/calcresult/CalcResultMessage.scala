package module.calcresult

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class CalcResultMessage extends CommonMessage("calcresult", CalcResultModule)

object CalcResultMessage {
	case class MsgCalcResultHistorySumSales(data: JsValue) extends CalcResultMessage
	case class MsgCalcResultHistoryCurVsPreWithCity(data: JsValue) extends CalcResultMessage
	
	case class MsgCalcResultCondition(data: JsValue) extends CalcResultMessage
	
	case class MsgCalcResultSalesVsShare(data: JsValue) extends CalcResultMessage
	case class MsgCalcResultCurVsPreWithCity(data: JsValue) extends CalcResultMessage
}