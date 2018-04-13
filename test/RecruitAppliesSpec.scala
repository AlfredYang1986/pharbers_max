import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.duration._

class RecruitAppliesSpec extends Specification with BeforeAll with AfterAll {

    import scala.concurrent.ExecutionContext.Implicits.global

    val time_out = 2 second
    var token: String = ""
    var user_id: String = ""

    var user_opent_id: String = ""

    lazy val apply_info = toJson(
        Map(
            "brand_name" -> "我是一个品牌",
            "name" -> "我是一个服务提供商"
        )
    )

    lazy val apply_update_info = toJson(
        Map(
            "brand_name" -> "我是二个品牌",
            "name" -> "我是二个服务提供商"
        )
    )

    var apply_id: String = ""

    override def beforeAll(): Unit = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").authLoginWithPhone("13720200856"), time_out)

            val result = (reVal \ "result").asOpt[JsValue].get
            token = (result \ "auth_token").asOpt[String].get
            user_id = (result \ "user" \ "user_id").asOpt[String].get

            pushApplyTest
        }
    }

    override def afterAll(): Unit = popApplyTest

    override def is =
        s2"""
        This is a dongda to check the profile logic string

            The 'dongda' adding recruit functions should
                query apply test                $queryApplyTest
                update apply test               $updateApplyTest
                                                                              """
//    pushApplyTest               $pushApplyTest

    def pushApplyTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").pushRecruitApply(token, user_id, apply_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            apply_id = (result \ "apply_id").asOpt[String].get
            println(apply_id)
            apply_id.length must_!= 0
        }
    }

    def popApplyTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").popRecruitApply(token, apply_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "pop apply").asOpt[String].get must_== "success"
        }
    }

    def queryApplyTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryRecruitApply(token, apply_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            val ap = (result \ "apply").asOpt[JsValue].get

            (ap \ "brand_name").asOpt[String].get must_== "我是一个品牌"
            (ap \ "name").asOpt[String].get must_== "我是一个服务提供商"
        }
    }

    def updateApplyTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").updateRecruitApply(token, apply_id, apply_update_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            val ap = (result \ "apply").asOpt[JsValue].get

            (ap \ "brand_name").asOpt[String].get must_== "我是二个品牌"
            (ap \ "name").asOpt[String].get must_== "我是二个服务提供商"
        }
    }
}