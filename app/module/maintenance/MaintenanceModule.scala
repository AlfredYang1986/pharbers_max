package module.maintenance

import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.builder.MaintenanceCenter
import module.common.MergeStepResult
import module.maintenance.MaintenanceMessage._
import play.api.libs.json.JsValue

/**
  * Created by jeorch on 18-6-7.
  */
object MaintenanceModule extends ModuleTrait {

    val maintenance = new MaintenanceCenter

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        //TODO:Use role to verify.
        case msg_getMaintenanceCenterAllCompanies(data) => maintenance.getAllCompaniesLst
        case msg_getSingleModuleAllFiles(data) => maintenance.getSingleModuleArgs(MergeStepResult(data, pr))
        case msg_replaceMatchFile(data) => maintenance.replaceMatchFile(MergeStepResult(data, pr))
    }

}
