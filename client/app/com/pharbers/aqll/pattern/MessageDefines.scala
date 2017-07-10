package com.pharbers.aqll.pattern

import com.pharbers.aqll.dbmodule.MongoDBModule
import play.api.libs.json.JsValue

trait MessageDefines
abstract class CommonMessage extends MessageDefines

//object JobCategories {
//    object cpaMarketJob extends JobDefines(0, "CpaMarket")
//    object cpaProductJob extends JobDefines(1, "CpaProduct")
//    object phaMarketJob extends JobDefines(2, "PhaMarket")
//    object phaProductJob extends JobDefines(3, "PhaProduct")
//}
//
//sealed case class JobDefines(t : Int, des : String)

//class ParallelMessage extends MessageDefines {
//	type data_type = List[MessageDefines]
//}

//case class excelJobStart(filename : String, cat : JobDefines, company: String, n: Int)

case class excute(msr : MessageRoutes)
case class result(rst : JsValue)

case class error(err : JsValue)
case class timeout()

case class MessageRoutes(lst : List[MessageDefines], rst : Option[Map[String, JsValue]])(implicit val db: MongoDBModule)