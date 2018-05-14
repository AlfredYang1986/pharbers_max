package module.jobs.channel.callJobResponse

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by spark on 18-4-24.
  */
class msg_callJobResponse extends CommonMessage("callJobResponse", callJobResponseModule)

object callJobResponseMessage {
    case class msg_queryJobResponse(data : JsValue) extends msg_callJobResponse
    case class msg_changeJobStatus(jv: JsValue) extends msg_callJobResponse
}
