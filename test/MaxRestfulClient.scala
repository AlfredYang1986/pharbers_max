import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import scala.concurrent.{ExecutionContext, Future}

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
                response.json
            }
    }

    def companyUsers(company_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/company/users")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "company_id" -> toJson(company_id)
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }


    def pushJob(user_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/job/push")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "user_id" -> toJson(user_id)
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

    def popJob(job_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/job/pop")
                .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
                .post(
                    toJson(Map(
                        "condition" -> toJson(Map(
                            "job_id" -> job_id
                        ))
                    ))
                )
                .map { response =>
                    response.json
                }
    }

    def bindJobUser(job_id: String, user_id: String): Future[JsValue] = {
        ws.url(baseUrl + "/api/job/user/bind")
                .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
                .post(
                    toJson(Map(
                        "job" -> toJson(Map(
                            "job_id" -> toJson(job_id)
                        )),
                        "user" -> toJson(Map(
                            "user_id" -> toJson(user_id)
                        ))
                    ))
                )
                .map { response =>
                    response.json
                }
    }

    def unbindJobUser(bind_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/job/user/unbind")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "bind_id" -> toJson(bind_id)
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

    def jobDetail(job_id: String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/job/detail")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "job_id" -> toJson(job_id)
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

    def userJobs(user_id : String) : Future[JsValue] = {
        ws.url(baseUrl + "/api/user/jobs")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(
                toJson(Map(
                    "condition" -> toJson(Map(
                        "user_id" -> toJson(user_id)
                    ))
                ))
            )
            .map { response =>
                response.json
            }
    }

}
