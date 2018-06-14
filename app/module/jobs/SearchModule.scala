package module.jobs

import play.api.libs.json.JsValue
import module.jobs.SearchMessage._
import module.common.MergeStepResult
import com.pharbers.builder.SearchFacade
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object SearchModule extends ModuleTrait {
    val search = new SearchFacade

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_searchAllMkt(data) => search.searchAllMkt(MergeStepResult(data, pr))
        case msg_searchHistory(data) => search.searchHistory(MergeStepResult(data, pr))
        case msg_getExportType(data) => search.getCurrentCompanyExportType(MergeStepResult(data, pr))
        case msg_searchExportData(data) => search.exportData(MergeStepResult(data, pr))
        case msg_searchExportDelivery(data) => search.exportDelivery(MergeStepResult(data, pr))

        case msg_searchSimpleCheckSelect(data) => search.searchSimpleCheckSelect(MergeStepResult(data, pr))
        case msg_searchSimpleCheck(data) => search.searchSimpleCheck(MergeStepResult(data, pr))

        case msg_searchResultCheck(data) => search.searchResultCheck(MergeStepResult(data, pr))

        case _ => ???
    }
}
