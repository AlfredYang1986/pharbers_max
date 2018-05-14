package module.jobs

import play.api.libs.json.JsValue
import module.jobs.SearchMessage._
import module.common.MergeStepResult
import module.jobs.search.searchTrait
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object SearchModule extends ModuleTrait with searchTrait {

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_searchAllMkt(data) => searchAllMkt2(MergeStepResult(data, pr))
        case msg_searchHistory(data) => searchHistory(MergeStepResult(data, pr))

        case msg_searchSimpleCheckSelect(data) => searchSimpleCheckSelect(MergeStepResult(data, pr))
        case msg_searchSimpleCheck(data) => searchSimpleCheck(MergeStepResult(data, pr))

        case msg_searchResultCheck(data) => searchResultCheck(MergeStepResult(data, pr))

        case _ => ???
    }
}
