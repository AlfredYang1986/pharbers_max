package module.jobs.channel

import akka.actor.ActorSystem
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import java.util.concurrent.Executors
import com.pharbers.channel.chanelImpl._
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import module.jobs.channel.callJobResponse.callJobResponseMessage._
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

case class progressConsumer(as_inject: ActorSystem, dbt: dbInstanceManager) extends kafkaBasicConf with kafkaConsumer {

    override lazy val group_id: String = ObjectId.get().toString
    override lazy implicit val dispatch: ActorSystem = as_inject

    override lazy val endpoints: String = kafka_config_obj.endpoints
    override lazy val schemapath: String = kafka_config_obj.progressSP
    override lazy val topic: String = kafka_config_obj.progressTopic

    override val consumeHandler: JsValue => MessageRoutes = { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("call job response"))), jv)
                :: msg_queryJobResponse(jv)
                :: msg_changeJobStatus(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("as" -> as_inject, "db" -> dbt))))
    }

    Executors.newFixedThreadPool(1).submit(this)
}

