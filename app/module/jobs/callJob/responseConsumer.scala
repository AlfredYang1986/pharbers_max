package module.jobs.callJob

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.common.xmpp.kafka.{kafkaBasicConf, kafkaConsumer, kafka_config_obj}
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.jobs.callJob.callJobResponse.callJobResponseMessage._
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

case class responseConsumer(as_inject: ActorSystem, dbt: dbInstanceManager) extends kafkaBasicConf with kafkaConsumer {

    override lazy val group_id: String = ObjectId.get().toString
    override lazy implicit val dispatch: ActorSystem = as_inject

    override lazy val endpoints: String = kafka_config_obj.endpoints
    override lazy val schemapath: String = kafka_config_obj.responseSP
    override lazy val topic: String = kafka_config_obj.callJobResponseTopic

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

