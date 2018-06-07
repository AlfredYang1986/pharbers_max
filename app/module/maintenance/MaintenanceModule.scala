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
        case msg_getMaintenanceCenterAllCompanies(data) => maintenance.getAllCompanies
        case msg_getDataCleanModuleAllFiles(data) => maintenance.getDataCleanModuleArgs(MergeStepResult(data, pr))
        case msg_getSimpleModuleAllFiles(data) => maintenance.getSimpleModuleArgs(MergeStepResult(data, pr))
        case msg_getMaxModuleAllFiles(data) => maintenance.getMaxModuleArgs(MergeStepResult(data, pr))
        case msg_getDeliveryModuleAllFiles(data) => maintenance.getDeliveryModuleArgs(MergeStepResult(data, pr))
        case msg_replaceDataCleanModuleFile(data) => maintenance.replaceMatchFile(MergeStepResult(data, pr))
        case msg_replaceSimpleModuleFile(data) => maintenance.replaceMatchFile(MergeStepResult(data, pr))
        case msg_replaceMaxModuleFile(data) => maintenance.replaceMatchFile(MergeStepResult(data, pr))
        case msg_replaceDeliveryModuleFile(data) => maintenance.replaceMatchFile(MergeStepResult(data, pr))
    }

}
