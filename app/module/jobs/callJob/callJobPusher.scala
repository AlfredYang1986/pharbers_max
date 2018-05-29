package module.jobs.callJob

import com.pharbers.common.xmpp.kafka.{kafkaBasicConf, kafkaPushRecord, kafka_config_obj}

trait callJobPusher extends kafkaBasicConf with kafkaPushRecord {

    override lazy val endpoints: String = kafka_config_obj.endpoints
    override lazy val schemapath: String = kafka_config_obj.requestSP
    override lazy val topic: String = kafka_config_obj.callJobTopic

}

