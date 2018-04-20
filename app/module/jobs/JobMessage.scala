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
}