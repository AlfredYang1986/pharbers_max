package module.jobs.channel

import com.pharbers.channel.chanelImpl._

trait callJobPusher extends kafkaBasicConf with kafkaPushRecord {

    override lazy val endpoints: String = kafka_config_obj.endpoints
    override lazy val schemapath: String = kafka_config_obj.callJobSP
    override lazy val topic: String = kafka_config_obj.callJobTopic

}

