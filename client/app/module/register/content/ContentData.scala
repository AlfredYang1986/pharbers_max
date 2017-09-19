package module.register.content

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by yym on 9/14/17.
  */
trait ContentData {
    //-------------jsValue => DBObject------------------------
    def pushEmail(data : JsValue) : DBObject = {
        val result = MongoDBList.newBuilder
        result += (data \ "reg_content" \ "email").asOpt[String].map (x => x).getOrElse(throw new Exception("User have no secret"))
        result.result
    }
    def pushSecret(data : JsValue) : DBObject = {
        val result = MongoDBList.newBuilder
        result += (data \ "reg_content" \ "secret").asOpt[String].map (x => x).getOrElse(throw new Exception("User have no secret"))
        result.result
    }
    def pushName(data : JsValue) : DBObject = {
        val result = MongoDBList.newBuilder
        result += (data \ "reg_content" \ "name").asOpt[String].map (x => x).getOrElse(throw new Exception("User have no name"))
        result.result
    }
    def pushPhone(data : JsValue) : DBObject = {
        val result = MongoDBList.newBuilder
        result += (data \ "reg_content" \ "phone").asOpt[String].map (x => x).getOrElse(throw new Exception("User have no secret"))
        result.result
    }
    def pushScope(data : JsValue) : DBObject = {
        val result = MongoDBList.newBuilder
        (data \ "reg_content" \ "scope").asOpt[List[String]].map (x => x).getOrElse(throw new Exception("User have no secret")).foreach(result += _)
        result.result
    }
    def pushOther(data : JsValue) : DBObject = {
        val result = MongoDBList.newBuilder
        result += (data \ "reg_content" \ "other").asOpt[String].map (x => x).getOrElse("")
        result.result
    }
    
    
    //---------------------------DBObject => jsValue-------------------------------------
    def queryEmailContent(obj : MongoDBObject) : JsValue = {
        val content = obj.getAs[MongoDBObject]("reg_content").map (x => x).getOrElse(throw new Exception(" db prase error"))
        val e = content.getAs[String]("email").map (x => x).getOrElse(throw new Exception(" db prase error"))
        toJson(e)
    }
    def querySecretContent(obj : MongoDBObject) : JsValue = {
        val content = obj.getAs[MongoDBObject]("reg_content").map (x => x).getOrElse(throw new Exception(" db prase error"))
        val e = content.getAs[String]("secret").map (x => x).getOrElse(throw new Exception(" db prase error"))
        toJson(e)
    }
    def queryNameContent(obj : MongoDBObject) : JsValue = {
        val content = obj.getAs[MongoDBObject]("reg_content").map (x => x).getOrElse(throw new Exception(" db prase error"))
        val e = content.getAs[String]("name").map (x => x).getOrElse(throw new Exception(" db prase error"))
        toJson(e)
    }
    def queryPhoneContent(obj : MongoDBObject) : JsValue = {
        val content = obj.getAs[MongoDBObject]("reg_content").map (x => x).getOrElse(throw new Exception(" db prase error"))
        val e = content.getAs[String]("phone").map (x => x).getOrElse(throw new Exception(" db prase error"))
        toJson(e)
    }
    def queryScopeContent(obj : MongoDBObject) : JsValue = {
        val content = obj.getAs[MongoDBObject]("reg_content").map (x => x).getOrElse(throw new Exception(" db prase error"))
        val e = content.getAs[List[String]]("scope").map (x => x).getOrElse(throw new Exception(" db prase error"))
        toJson(e)
    }
    def queryOtherContent(obj : MongoDBObject) : JsValue = {
        val content = obj.getAs[MongoDBObject]("reg_content").map (x => x).getOrElse(throw new Exception(" db prase error"))
        val e = content.getAs[String]("other").map (x => x).getOrElse("")
        toJson(e)
    }
    
}
