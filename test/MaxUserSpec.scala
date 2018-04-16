import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.duration._

class MaxUserSpec extends Specification with BeforeAll with AfterAll {

    import scala.concurrent.ExecutionContext.Implicits.global

    val time_out = 2 second
    var user_id: String = ""

    lazy val user_push_info = toJson(
        Map(
            "screen_name" -> "名字",
            "screen_photo" -> "头像",
            "email" -> "alfredyang@blackmirror.tech",
            "phone" -> "13720200856"
        )
    )

    lazy val apply_update_info = toJson(
        Map(
            "screen_name" -> "我是二个品牌",
            "screen_photo" -> "我是二个服务提供商",
            "email" -> "yangyuanpig@163.com",
            "phone" -> "17611245119"
        )
    )

    override def beforeAll(): Unit = pushUserTest
    override def afterAll(): Unit = popUserTest

    override def is =
        s2"""
        This is a max to check the restful logic string

            The 'max' adding user functions should
                query user with user_id         $queryUserWithIDTest
                query users multi               $queryUserMulti
                                                                              """

    def pushUserTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").pushUser(user_push_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            user_id = (result \ "user" \ "user_id").asOpt[String].get
            println(user_id)
            user_id.length must_!= 0
        }
    }

    def popUserTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").popUser(user_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "pop user").asOpt[String].get must_== "success"
        }
    }

    def queryUserWithIDTest = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryUser(user_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "user" \ "screen_name").asOpt[String].get must_== "名字"
            (result \ "user" \ "screen_photo").asOpt[String].get must_== "头像"
            (result \ "user" \ "email").asOpt[String].get must_== "alfredyang@blackmirror.tech"
            (result \ "user" \ "phone").asOpt[String].get must_== "13720200856"
        }
    }

    def queryUserMulti = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryUserMulti(user_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            val lst = (result \ "users").asOpt[List[JsValue]].get// must_== "名字"
            lst.length must_== 1
            val head = lst.head
            (head \ "screen_name").asOpt[String].get must_== "名字"
            (head \ "screen_photo").asOpt[String].get must_== "头像"
        }
    }
}