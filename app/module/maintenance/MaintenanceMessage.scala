package module.maintenance

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by jeorch on 18-6-7.
  */
abstract class msg_MaintenanceCommand extends CommonMessage("maintenance", MaintenanceModule)

object MaintenanceMessage {

    case class msg_getMaintenanceCenterAllCompanies(data: JsValue) extends msg_MaintenanceCommand
    case class msg_getSingleModuleAllFiles(data: JsValue) extends msg_MaintenanceCommand
    case class msg_replaceMatchFile(data: JsValue) extends msg_MaintenanceCommand

}
