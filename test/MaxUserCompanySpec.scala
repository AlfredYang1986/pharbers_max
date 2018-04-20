
import scala.concurrent.Await
import play.api.libs.json.JsValue
import play.api.test.WsTestClient
import scala.concurrent.duration._
import org.specs2.matcher.MatchResult
import play.api.libs.json.Json.toJson
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}
import scala.concurrent.ExecutionContext.Implicits.global

class MaxUserCompanySpec extends Specification with BeforeAll with AfterAll {
    var bind_id = ""
    var user_id : String = ""
    var company_id: String = ""
    val time_out: FiniteDuration = 2 second

    override def beforeAll(): Unit = pushUserAndCompnay
    override def afterAll(): Unit = popUserAndCompany

    override def is = s2"""
        This is a max to check the restful logic string

            The 'max' adding company functions should
                    query user with company info            $queryUserDetail
                    query company with user info            $queryCompanyDetail
                                                                              """

    lazy val company_push_map: Map[String, String] = Map(
        "company_name" -> "名字",
        "company_des" -> "备注"
    )

    lazy val user_push_map: Map[String, String] = Map(
        "screen_name" -> "显示名字",
        "screen_photo" -> "显示头像",
        "email" -> "testEmail@email.com",
        "phone" -> "13112341234"
    )

    lazy val company_info: JsValue = toJson(company_push_map)

    lazy val user_info: JsValue = toJson(user_push_map)

    def pushUserAndCompnay: MatchResult[Any] = {
        def pushCompanyTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(
                    new MaxRestfulClient(client, "http://127.0.0.1:9000").pushCompany(company_info), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                company_id = (result \ "company" \ "company_id").asOpt[String].get
                company_id.length must_!= 0
            }
        }
        def pushUserTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").pushUser(user_info), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                user_id = (result \ "user" \ "user_id").asOpt[String].get
                user_id.length must_!= 0
            }
        }
        def bindUserCompanyTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(
                    new MaxRestfulClient(client, "http://127.0.0.1:9000").bindUserCompany(user_id, company_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                (result \ "bind user with company").asOpt[String].get must_== "success"
                bind_id = (result \ "bind_id").asOpt[String].get
                bind_id.length must_!= 0
            }
        }

        pushCompanyTest
        pushUserTest
        bindUserCompanyTest
    }

    def popUserAndCompany: MatchResult[Any] = {
        def unbindUserCompanyTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(
                    new MaxRestfulClient(client, "http://127.0.0.1:9000").unbindUserCompany(bind_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                (result \ "unbind user with company").asOpt[String].get must_== "success"
            }
        }
        def popUserTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").popUser(user_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                (result \ "pop user").asOpt[String].get must_== "success"
            }
        }
        def popCompanyTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").popCompany(company_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                (result \ "pop company").asOpt[String].get must_== "success"
            }
        }

        unbindUserCompanyTest
        popUserTest
        popCompanyTest
    }

    def queryUserDetail: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").userDetail(user_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            val user = (result \ "user").asOpt[JsValue].get
            (user \ "screen_name").asOpt[String].get must_== user_push_map("screen_name")
            (user \ "screen_photo").asOpt[String].get must_== user_push_map("screen_photo")
            (user \ "email").asOpt[String].get must_== user_push_map("email")
            (user \ "phone").asOpt[String].get must_== user_push_map("phone")

            val company = (user \ "company").asOpt[JsValue].get
            (company \ "company_name").asOpt[String].get must_== company_push_map("company_name")
            (company \ "company_des").asOpt[String].get must_== company_push_map("company_des")
        }
    }

    def queryCompanyDetail: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").companyUsers(company_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            val company = (result \ "company").asOpt[JsValue].get
            (company \ "company_name").asOpt[String].get must_== company_push_map("company_name")
            (company \ "company_des").asOpt[String].get must_== company_push_map("company_des")

            val user = (company \ "users").asOpt[List[JsValue]].get.head
            (user \ "screen_name").asOpt[String].get must_== user_push_map("screen_name")
            (user \ "screen_photo").asOpt[String].get must_== user_push_map("screen_photo")
            (user \ "email").asOpt[String].get must_== user_push_map("email")
            (user \ "phone").asOpt[String].get must_== user_push_map("phone")
        }
    }

}
