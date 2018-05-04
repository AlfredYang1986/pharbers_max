package module.jobs

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by spark on 18-4-19.
  */
abstract class msg_JobCommand extends CommonMessage("jobs", JobModule)

object JobMessage {
    case class msg_pushJob(data: JsValue) extends msg_JobCommand
    case class msg_popJob(data : JsValue) extends msg_JobCommand
    case class msg_queryJob(data : JsValue) extends msg_JobCommand
    case class msg_queryJobMulti(data : JsValue) extends msg_JobCommand

    case class msg_verifyUserBind(data: JsValue) extends msg_JobCommand
    case class msg_bindJobUser(data : JsValue) extends msg_JobCommand
    case class msg_unbindJobUser(data : JsValue) extends msg_JobCommand
    case class msg_expendUserInfo(data : JsValue) extends msg_JobCommand

    case class msg_ymCalcJob(data : JsValue) extends msg_JobCommand
    case class msg_ymCalcingJob(data : JsValue) extends msg_JobCommand
    case class msg_panelJob(data : JsValue) extends msg_JobCommand
    case class msg_paneledJob(data : JsValue) extends msg_JobCommand
    case class msg_calcJob(data : JsValue) extends msg_JobCommand
    case class msg_calcingJob(data : JsValue) extends msg_JobCommand
    case class msg_doneJob(data : JsValue) extends msg_JobCommand
    case class msg_killJob(data : JsValue) extends msg_JobCommand
}