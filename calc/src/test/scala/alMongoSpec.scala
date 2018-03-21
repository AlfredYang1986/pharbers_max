//import com.mongodb.casbah.Imports.DBObject
//import org.specs2.mutable.Specification
//import org.specs2.specification.AfterAll
//import com.pharbers.aqll.alCalcHelp.dbAdmin.dba
//
//class alMongoSpec extends Specification with AfterAll{
//    override def is = s2"""
//        This is a Max Master Specification to check the 'Max calc' process
//            The 'Max master' structure should
//
//            The 'Max master' functions should
//                calc data                                               $e1
//        """
//
//    def e1 = {
//        dba.command(
//            DBObject(
//                "shardcollection" -> "testdb.city2",
//                "key" -> DBObject(
//                    "_id" -> 1
//                )
//            )
//        )
//        0 must_== 0
//    }
//
//    override def afterAll(): Unit = {
//
//    }
//}
