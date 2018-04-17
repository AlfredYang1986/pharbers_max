import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.duration._

class MaxUserCompanySpec extends Specification with BeforeAll with AfterAll {

    import scala.concurrent.ExecutionContext.Implicits.global

    val time_out = 2 second
    var company_id: String = ""
    var user_id : String = ""

    var bind_id = ""

    lazy val company_info = toJson(
        Map(
            "company_name" -> "公司",
            "company_des" -> "工商税务"
        )
    )

    lazy val user_info = toJson(
        Map(
            "screen_name" -> "名字",
            "screen_photo" -> "头像",
            "email" -> "alfredyang@blackmirror.tech",
            "phone" -> "13720200856"
        )
    )

    override def beforeAll(): Unit = pushUserAndCompnay
    override def afterAll(): Unit = popUserAndCompany

    override def is =
        s2"""
        This is a max to check the restful logic string

            The 'max' adding company functions should
                    query user with company info            $queryUserDetail
                    query company with user info            $queryCompanyDetail
                                                                              """
//    bind user and company               $bindUserCompanyTest

    def pushUserAndCompnay = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").pushCompany(company_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            company_id = (result \ "company" \ "company_id").asOpt[String].get
            println(s"company id is $company_id")
            company_id.length must_!= 0
        }

        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").pushUser(user_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            user_id = (result \ "user" \ "user_id").asOpt[String].get
            println(s"user id is $user_id")
            user_id.length must_!= 0
        }

        bindUserCompanyTest
    }

    def popUserAndCompany = {
        unbindUserCompanyTest

        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").popCompany(company_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "pop company").asOpt[String].get must_== "success"
        }

        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").popUser(user_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "pop user").asOpt[String].get must_== "success"
        }
    }

    def bindUserCompanyTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").bindUserCompany(user_id, company_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            println(reVal)
            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "bind user with company").asOpt[String].get must_== "success"
            bind_id = (result \ "bind_id").asOpt[String].get
            println(s"bind id is $bind_id")
            bind_id.length must_!= 0
        }
    }

    def unbindUserCompanyTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").unbindUserCompany(bind_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            println(reVal)
            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "unbind user with company").asOpt[String].get must_== "success"
        }
    }

    def queryUserDetail = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").userDetail(user_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            println(reVal)
            val result = (reVal \ "result").asOpt[JsValue].get
            val user = (result \ "user").asOpt[JsValue].get
            (user \ "screen_name").asOpt[String].get must_== "名字"
            (user \ "screen_photo").asOpt[String].get must_== "头像"
            (user \ "email").asOpt[String].get must_== "alfredyang@blackmirror.tech"
            (user \ "phone").asOpt[String].get must_== "13720200856"

            val company = (user \ "company").asOpt[JsValue].get
            (company \ "company_name").asOpt[String].get must_== "公司"
            (company \ "company_des").asOpt[String].get must_== "工商税务"
        }
    }

    def queryCompanyDetail = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").companyDetail(company_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            println(reVal)
            val result = (reVal \ "result").asOpt[JsValue].get
            val company = (result \ "company").asOpt[JsValue].get
            (company \ "company_name").asOpt[String].get must_== "公司"
            (company \ "company_des").asOpt[String].get must_== "工商税务"

            val user = (company \ "users").asOpt[List[JsValue]].get.head
            (user \ "screen_name").asOpt[String].get must_== "名字"
            (user \ "screen_photo").asOpt[String].get must_== "头像"
            (user \ "email").asOpt[String].get must_== "alfredyang@blackmirror.tech"
            (user \ "phone").asOpt[String].get must_== "13720200856"
        }
    }
}
