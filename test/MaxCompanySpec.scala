
import org.specs2.matcher.MatchResult

import scala.concurrent.Await
import play.api.libs.json.JsValue

import scala.concurrent.duration._
import play.api.test.WsTestClient
import play.api.libs.json.Json.toJson
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}

import scala.concurrent.ExecutionContext.Implicits.global

class MaxCompanySpec extends Specification with BeforeAll with AfterAll {
    var company_id : String = ""
    val time_out: FiniteDuration = 2 second

    override def beforeAll(): Unit = pushCompanyTest
    override def afterAll(): Unit = popCompanyTest

    override def is = s2"""
        This is a max to check the restful logic string

            The 'max' adding company functions should
                push repeat company             $pushRepeatCompanyTest
                query company with id           $queryCompanyTest
                query company multi             $queryCompanyMultiTest
                                                                              """

    lazy val company_push_map: Map[String, String] = Map(
        "company_name" -> "名字",
        "company_des" -> "备注"
    )

    lazy val company_push_info: JsValue = toJson(company_push_map)

    lazy val company_update_info: JsValue = toJson(
        Map(
            "company_name" -> "名字2",
            "company_des" -> "备注2"
        )
    )


    def pushCompanyTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").pushCompany(company_push_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            company_id = (result \ "company" \ "company_id").asOpt[String].get
            company_id.length must_!= 0
        }
    }

    def pushRepeatCompanyTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(new MaxRestfulClient(client, "http://127.0.0.1:9000").pushCompany(company_push_info), time_out)
            (reVal \ "status").asOpt[String].get must_== "error"

            val error_code = (reVal \ "error" \ "code").asOpt[Int].get
            error_code must_== -301
        }
    }

    def queryCompanyTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryCompany(company_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result").asOpt[JsValue].get
            (result \ "company" \ "company_name").asOpt[String].get must_== company_push_map("company_name")
            (result \ "company" \ "company_des").asOpt[String].get must_== company_push_map("company_des")
        }
    }

    def queryCompanyMultiTest: MatchResult[Any] = {
        WsTestClient.withClient { client =>
            val reVal = Await.result(
                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryCompanyMulti(company_id), time_out)
            (reVal \ "status").asOpt[String].get must_== "ok"

            val result = (reVal \ "result" \ "companies").asOpt[List[JsValue]].get
            result.length must_== 1

            val head = result.head
            (head \ "company_name").asOpt[String].get must_== company_push_map("company_name")
            (head \ "company_des").asOpt[String].get must_== company_push_map("company_des")
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
}
