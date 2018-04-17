import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient

import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by alfredyang on 07/07/2017.
  * 每一个max restful post 接口都需要写单元测试
  */
class MaxRestfulClient(ws: WSClient, baseUrl: String)(implicit ec: ExecutionContext) {
    @Inject def this(ws: WSClient, ec: ExecutionContext) = this(ws, "http://127.0.0.1:9000")(ec)

    def pushUser(user : JsValue) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/push")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "user" -> user
                ))
            )
            .map { response =>
//                println(response.json)
                response.json
            }
    }

    def popUser(user_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/pop")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "user_id" -> toJson(user_id)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def queryUser(user_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/query")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "user_id" -> toJson(user_id)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def queryUserMulti(user_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/query/multi")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "users" -> toJson(user_id :: Nil)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def pushCompany(company : JsValue) : Future[JsValue] = {
        ws.url(baseUrl + "/api/company/push")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "company" -> company
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def popCompany(company_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/company/pop")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "company_id" -> company_id
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def queryCompany(company_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/company/query")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "company_id" -> company_id
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def queryCompanyMulti(company_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/company/query/multi")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "companies" -> toJson(company_id :: Nil)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def bindUserCompany(user_id : String, company_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/company/bind")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "user" -> toJson(Map(
                        "user_id" -> toJson(user_id)
                    )),
                    "company" -> toJson(Map(
                        "company_id" -> toJson(company_id)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def unbindUserCompany(bind_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/company/unbind")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "bind_id" -> toJson(bind_id)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def userDetail(user_id: String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/detail")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "user_id" -> toJson(user_id)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }

    def companyDetail(company_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/company/detail")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "company_id" -> toJson(company_id)
                    ))
                ))
            )
            .map { response =>
                //                println(response.json)
                response.json
            }
    }
}
