package module.immessage

import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.message.im.EmChatMsg
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson

import module.immessage.ImRoomMessage._

object ImRoomModule extends ModuleTrait {
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgCreateImRooms(data) => createRooms(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def createRooms(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "condition" \ "company").asOpt[String].getOrElse("")
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse("")
			creatreEmRooms(company, uid)
			(Some(Map("condition" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def creatreEmRooms(company: String, uid: String): String = {
		val roomName = s"${company}_${uid}"
		val roomId = (Json.parse(EmChatMsg().getAllRooms) \ "data").as[List[String Map JsValue]].find(x => x("name").as[String] == roomName) match {
			case None => {
				val reVal = (Json.parse(EmChatMsg()
					.setRoomName(roomName)
					.setRoomDescription(roomName)
					.setRoomOnwer("project")
					.setRoomMaxUsers(200)
					.createChatRoom) \ "data").as[String Map JsValue]
				reVal("id").as[String]
			}
			case Some(x) => x("id").as[String]
		}
		
		(Json.parse(EmChatMsg().getUsersBatch()) \ "entities").as[List[String Map JsValue]].filter(x =>
			x("username").as[String].indexOf(s"${company}_") != -1 && x("username").as[String].indexOf(s"_${uid}") != -1
		).map(x => x("username").as[String]) match {
			case Nil => ""
			case lst => EmChatMsg().setRoomMembers(roomId, lst)
		}
	}
}
