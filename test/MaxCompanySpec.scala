//import org.specs2.mutable.Specification
//import org.specs2.specification.{AfterAll, BeforeAll}
//import play.api.libs.json.JsValue
//import play.api.libs.json.Json.toJson
//import play.api.test.WsTestClient
//
//import scala.concurrent.Await
//import scala.concurrent.duration._
//
//class MaxCompanySpec extends Specification with BeforeAll with AfterAll {
//
//    import scala.concurrent.ExecutionContext.Implicits.global
//
//    val time_out = 2 second
//    var company_id : String = ""
//
//    lazy val company_push_info = toJson(
//        Map(
//            "company_name" -> "名字",
//            "company_des" -> "头像"
//        )
//    )
//
//    lazy val company_update_info = toJson(
//        Map(
//            "company_name" -> "我是二个工商",
//            "company_des" -> "我是二个公司"
//        )
//    )
//
//    override def beforeAll(): Unit = pushCompanyTest
//    override def afterAll(): Unit = popCompanyTest
//
//    override def is =
//        s2"""
//        This is a max to check the restful logic string
//
//            The 'max' adding company functions should
//                query company with id           $queryCompanyTest
//                query company multi             $queryCompanyMultiTest
//                                                                              """
//
//    def pushCompanyTest = {
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(
//                new MaxRestfulClient(client, "http://127.0.0.1:9000").pushCompany(company_push_info), time_out)
//            (reVal \ "status").asOpt[String].get must_== "ok"
//
//            val result = (reVal \ "result").asOpt[JsValue].get
//            company_id = (result \ "company" \ "company_id").asOpt[String].get
//            println(company_id)
//            company_id.length must_!= 0
//        }
//    }
//
//    def popCompanyTest = {
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(
//                new MaxRestfulClient(client, "http://127.0.0.1:9000").popCompany(company_id), time_out)
//            (reVal \ "status").asOpt[String].get must_== "ok"
//
//            val result = (reVal \ "result").asOpt[JsValue].get
//            (result \ "pop company").asOpt[String].get must_== "success"
//        }
//    }
//
//    def queryCompanyTest = {
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(
//                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryCompany(company_id), time_out)
//            (reVal \ "status").asOpt[String].get must_== "ok"
//
//            val result = (reVal \ "result").asOpt[JsValue].get
//            (result \ "company" \ "company_name").asOpt[String].get must_== "名字"
//            (result \ "company" \ "company_des").asOpt[String].get must_== "头像"
//        }
//    }
//
//    def queryCompanyMultiTest = {
//        WsTestClient.withClient { client =>
//            val reVal = Await.result(
//                new MaxRestfulClient(client, "http://127.0.0.1:9000").queryCompanyMulti(company_id), time_out)
//            (reVal \ "status").asOpt[String].get must_== "ok"
//
//            val result = (reVal \ "result" \ "companies").asOpt[List[JsValue]].get.head
//            (result \ "company_name").asOpt[String].get must_== "名字"
//            (result \ "company_des").asOpt[String].get must_== "头像"
//        }
//    }
//}
