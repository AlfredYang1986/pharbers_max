package module.company

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by spark on 18-4-19.
  */
abstract class msg_CompanyCommand extends CommonMessage("companies", CompanyModule)

object CompanyMessage {
    case class msg_verifyCompanyRegister(data: JsValue) extends msg_CompanyCommand
    case class msg_pushCompany(data: JsValue) extends msg_CompanyCommand
    case class msg_popCompany(data : JsValue) extends msg_CompanyCommand
    case class msg_queryCompany(data : JsValue) extends msg_CompanyCommand
    case class msg_queryCompanyMulti(data : JsValue) extends msg_CompanyCommand

    case class msg_expendUsersInfo(data : JsValue) extends msg_CompanyCommand
}