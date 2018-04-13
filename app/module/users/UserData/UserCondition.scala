package module.users.UserData

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue

trait UserCondition {
    implicit val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "user_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    implicit val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "users").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }
}
