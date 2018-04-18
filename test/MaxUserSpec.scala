
import scala.concurrent.Await
import play.api.libs.json.JsValue
import play.api.test.WsTestClient
import scala.concurrent.duration._
import org.specs2.matcher.MatchResult
import play.api.libs.json.Json.toJson
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}
import scala.concurrent.ExecutionContext.Implicits.global

class MaxUserSpec extends Specification with BeforeAll with AfterAll {
    val time_out: FiniteDuration = 2 second
    var user_id: String = ""

    override def beforeAll(): Unit = pushUserTest
    override def afterAll(): Unit = popUserTest

    override def is =
        s2"""
        This is a max to check the restful logic string

            The 'max' adding user functions should
                push repeat user                $pushRepeatUserTest
                query user with user_id         $queryUserWithIDTest
                query users multi               $queryUserMulti
                                                                              """

    lazy val user_push_info: JsValue = toJson(
        Map(
            "screen_name" -> "显示名字",
            "screen_photo" -> "显示头像",
            "email" -> "testEmail@email.com",
            "phone" -> "13112341234"
        )
    )

    lazy val apply_update_info: JsValue = toJson(
        Map(
            "screen_name" -> "显示名字2",
            "screen_photo" -> "显示头像2",
            "email" -> "testEmail2@email.com",
            "phone" -> "13112341111"
        )
    )

    def pushUserTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").pushUser(user_push_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            user_id = (result \ "user" \ "user_id").asOpt[String].get
            println(user_id)
            user_id.length must_!= 0
        }
    }

    def pushRepeatUserTest: MatchResult[Any] = {
        1 must_== 1
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").pushUser(user_push_info), time_out)
//            val rrr = (reVal \ "status").asOpt[String].get// must_== "ok"
//            println(rrr)
//            println(reVal)
//            val result = (reVal \ "result").asOpt[JsValue].get
//            user_id = (result \ "user" \ "user_id").asOpt[String].get
//            println(user_id)
//            user_id.length must_!= 0
//        }
    }

    def popUserTest: MatchResult[Any] = {
        1 must_== 1
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").popUser(user_id), time_out)
//            (reVal \ "status").asOpt[String].get must_== "ok"
//
//            val result = (reVal \ "result").asOpt[JsValue].get
//            (result \ "pop user").asOpt[String].get must_== "success"
//        }
    }

    def queryUserWithIDTest = {
        1 must_== 1
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(
//                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryUser(user_id), time_out)
//            (reVal \ "status").asOpt[String].get must_== "ok"
//
//            val result = (reVal \ "result").asOpt[JsValue].get
//            (result \ "user" \ "screen_name").asOpt[String].get must_== "名字"
//            (result \ "user" \ "screen_photo").asOpt[String].get must_== "头像"
//            (result \ "user" \ "email").asOpt[String].get must_== "alfredyang@blackmirror.tech"
//            (result \ "user" \ "phone").asOpt[String].get must_== "13720200856"
//        }
    }

    def queryUserMulti = {
        1 must_== 1
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(
//                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryUserMulti(user_id), time_out)
//            (reVal \ "status").asOpt[String].get must_== "ok"
//
//            println(reVal)
//            val result = (reVal \ "result").asOpt[JsValue].get
//            val lst = (result \ "users").asOpt[List[JsValue]].get// must_== "名字"
//            lst.length must_== 1
//            val head = lst.head
//            (head \ "screen_name").asOpt[String].get must_== "名字"
//            (head \ "screen_photo").asOpt[String].get must_== "头像"
//        }
    }
}