package module.jobs

import java.util.Date

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.datamodel.basemodel
import com.mongodb.casbah.Imports.DBObject
import com.pharbers.module.MAXCallJobPusher
import com.pharbers.bmmessages.CommonModules
import module.jobs.jobStatus._
import play.api.libs.json.Json.toJson


/**
  * Created by spark on 18-4-25.
  */
trait jobStatusChangeTrait { this : basemodel =>

    val changeStatus : (jobStatusDefine, DBObject, JsValue) => DBObject = { (jsd, obj, _) =>
        obj += "status" -> jsd.des
        obj += "change_time" -> new Date().getTime.toString

        obj
    }

    val up2ym : (DBObject, JsValue) => DBObject = changeStatus(jobYmCalc(), _, _)
    val up2ymed : (DBObject, JsValue) => DBObject = changeStatus(jobYmCalced(), _, _)
    val up2panel : (DBObject, JsValue) => DBObject = changeStatus(jobPanel(), _, _)
    val up2paneled : (DBObject, JsValue) => DBObject = changeStatus(jobPaneled(), _, _)
    val up2calc : (DBObject, JsValue) => DBObject = changeStatus(jobCalc(), _, _)
    val up2calced : (DBObject, JsValue) => DBObject = changeStatus(jobCalced(), _, _)
    val up2kill : (DBObject, JsValue) => DBObject = changeStatus(jobKill(), _, _)
}

/**
  * Created by spark on 18-4-25.
  */
trait callJob {

    val yf: JsValue => java.util.HashMap[String, Object] = { jv =>
        val map = new java.util.HashMap[String, Object]()
        map.put("cpa", (jv \ "condition" \ "args" \ "cpa").asOpt[String].getOrElse(""))
        map.put("gycx", (jv \ "condition" \ "args" \ "gycx").asOpt[String].getOrElse(""))
        map
    }

    val pf: JsValue => java.util.HashMap[String, Object] = { jv =>
        val map = new java.util.HashMap[String, Object]()
        map.put("cpa", (jv \ "condition" \ "args" \ "cpa").asOpt[String].getOrElse(""))
        map.put("gycx", (jv \ "condition" \ "args" \ "gycx").asOpt[String].getOrElse(""))
        map.put("ym", (jv \ "condition" \ "args" \ "ym").asOpt[String].getOrElse(""))
        map
    }

    val nullFun: JsValue => java.util.HashMap[String, Object] = { _ => new java.util.HashMap[String, Object]() }

    def callFunc(callName: String, func: JsValue => java.util.HashMap[String, Object])
                (jv: JsValue): Map[String, AnyRef] = {
        Map(
            "job_id" -> (jv \ "condition" \ "job_id").asOpt[String].get,
            "user_id" -> (jv \ "user" \ "user_id").asOpt[String].get,
            "company_id" -> (jv \ "user" \ "company" \ "company_id").asOpt[String].get,
            "date" -> new Date().getTime.toString,
            "call" -> callName,
            "args" -> func(jv)
        )
    }

    def callJob(func: (JsValue, String) => Map[String, AnyRef])
               (call: String)
               (jv: JsValue)
               (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val channel = cm.modules.get.get("cp").map(x => x.asInstanceOf[MAXCallJobPusher]).get
        channel.pushRecord(func(jv, call))(channel.precord)
        (Some(Map(call -> toJson("call success"))), None)
    }

}