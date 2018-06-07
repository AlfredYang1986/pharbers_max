package module.maintenance

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by jeorch on 18-6-7.
  */
abstract class msg_MaintenanceCommand extends CommonMessage("maintenance", MaintenanceModule)

object MaintenanceMessage {

    case class msg_getMaintenanceCenterAllCompanies(data: JsValue) extends msg_MaintenanceCommand
    case class msg_getDataCleanModuleAllFiles(data: JsValue) extends msg_MaintenanceCommand
    case class msg_getSimpleModuleAllFiles(data: JsValue) extends msg_MaintenanceCommand
    case class msg_getMaxModuleAllFiles(data: JsValue) extends msg_MaintenanceCommand
    case class msg_getDeliveryModuleAllFiles(data: JsValue) extends msg_MaintenanceCommand
    case class msg_replaceDataCleanModuleFile(data: JsValue) extends msg_MaintenanceCommand
    case class msg_replaceSimpleModuleFile(data: JsValue) extends msg_MaintenanceCommand
    case class msg_replaceMaxModuleFile(data: JsValue) extends msg_MaintenanceCommand
    case class msg_replaceDeliveryModuleFile(data: JsValue) extends msg_MaintenanceCommand

}
