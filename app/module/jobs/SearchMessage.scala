package module.jobs

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by spark on 18-4-19.
  */
abstract class msg_SearchCommand extends CommonMessage("search", SearchModule)

object SearchMessage {
    case class msg_searchAllMkt(data: JsValue) extends msg_SearchCommand
    case class msg_searchHistory(data: JsValue) extends msg_SearchCommand

    case class msg_searchSimpleCheckSelect(data: JsValue) extends msg_SearchCommand
    case class msg_searchSimpleCheck(data: JsValue) extends msg_SearchCommand

    case class msg_searchResultCheck(data: JsValue) extends msg_SearchCommand
}