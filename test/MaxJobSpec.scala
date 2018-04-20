
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class MaxJobSpec extends Specification with BeforeAll with AfterAll {
    val time_out: FiniteDuration = 2 second
    var user_id: String = ""
    var job_id: String = ""
    var bind_id: String = ""

    override def beforeAll(): Unit = bindJobUserTest
    override def afterAll(): Unit = unbindJobUserTest

    override def is = s2"""
        This is a max to check the restful logic string

            The 'max' adding user functions should
                query job with user info    $queryJobDetail
                query user with job info    $queryUserJobs
                                                                              """

    lazy val user_push_map: Map[String, String] = Map(
        "screen_name" -> "显示名字",
        "screen_photo" -> "显示头像",
        "email" -> "testEmail@email.com",
        "phone" -> "13112341234"
    )

    lazy val user_push_info: JsValue = toJson(user_push_map)

    def bindJobUserTest: MatchResult[Any] = {
        def pushUserTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").pushUser(user_push_info), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                user_id = (result \ "user" \ "user_id").asOpt[String].get
                user_id.length must_!= 0
            }
        }
        def pushJobTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").pushJob(user_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                job_id = (reVal \ "result" \ "job" \ "job_id").asOpt[String].get
                job_id.length must_!= 0
            }
        }
        def bindJobUser: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").bindJobUser(job_id, user_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                (result \ "bind job with user").asOpt[String].get must_== "success"
                bind_id = (result \ "bind_id").asOpt[String].get
                bind_id.length must_!= 0
            }
        }

        pushUserTest
        pushJobTest
        bindJobUser
    }

    def unbindJobUserTest: MatchResult[Any] = {
        def unbindJobUser: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").unbindJobUser(bind_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                (result \ "unbind job with user").asOpt[String].get must_== "success"
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
        def popJobTest: MatchResult[Any] = {
            WsTestClient.withClient { client =>
                val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").popJob(job_id), time_out)
                (reVal \ "status").asOpt[String].get must_== "ok"

                val result = (reVal \ "result").asOpt[JsValue].get
                (result \ "pop job").asOpt[String].get must_== "success"
            }
        }

        unbindJobUser
        popJobTest
        popUserTest
    }

    def queryJobDetail: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").jobDetail(job_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            val job = (result \ "job").asOpt[JsValue].get
            (job \ "job_id").asOpt[String].get.length must_!= 0
            (job \ "start_time").asOpt[Long].get must_!= 0
            (job \ "status").asOpt[String].get.length must_!= 0

            val user = (job \ "user").asOpt[JsValue].get
            (user \ "screen_name").asOpt[String].get must_== user_push_map("screen_name")
            (user \ "screen_photo").asOpt[String].get must_== user_push_map("screen_photo")
            (user \ "email").asOpt[String].get must_== user_push_map("email")
            (user \ "phone").asOpt[String].get must_== user_push_map("phone")
        }
    }

    def queryUserJobs: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").userJobs(user_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            val user = (result \ "user").asOpt[JsValue].get
            (user \ "screen_name").asOpt[String].get must_== user_push_map("screen_name")
            (user \ "screen_photo").asOpt[String].get must_== user_push_map("screen_photo")
            (user \ "email").asOpt[String].get must_== user_push_map("email")
            (user \ "phone").asOpt[String].get must_== user_push_map("phone")

            val job = (user \ "jobs").asOpt[List[JsValue]].get.head
            (job \ "job_id").asOpt[String].get.length must_!= 0
            (job \ "start_time").asOpt[Long].get must_!= 0
            (job \ "status").asOpt[String].get.length must_!= 0
        }
    }

}