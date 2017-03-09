package com.pharbers.aqll.stub

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.aldata.alPortion
import com.pharbers.aqll.alcalc.alFileHandler.alexcel.alIntegrateddataparser
import com.pharbers.aqll.alcalc.alFileHandler.altext.{alTextParser, alTextSync}

/**
  * Created by Alfred on 09/03/2017.
  */
object stub_test_1 extends App {

    {
        // test case 1 : reading excel file and storage and portion
        val s = alStorage("""config/new_test/2016-01.xlsx""", new alIntegrateddataparser)
        val ps = s.portion { lst =>
            lst.grouped(10).map(iter => alPortion(iter)).toList
        }
        println(ps.isCalc)
        println(ps.portions.head)
    }

    {
        // test case 2 : reading memory and map func
        val s = alStorage(List(1, 2, 3, 4, 5, 6, 7, 8, 9))
        val s1 = s.map { x =>
            x.asInstanceOf[Int] + 1
        }

        val ps = s1.portion { lst =>
            lst.grouped(2).map(iter => alPortion(iter)).toList
        }
        println(ps.portions.head)
    }

//    {
//        // test case 3 : persistence test
//        val s = alStorage("""config/new_test/2016-01.xlsx""", new alIntegrateddataparser)
//        val ps = s.portion { lst =>
//            lst.grouped(10).map(iter => alPortion(iter)).toList
//        }
//        alTextSync("""config/sync""", s)
//        alTextSync("""config/sync/po""", ps)
//        println(ps.portions.head)
//    }

    {
        // test case 4 : restore data form text
        val s = alStorage("""config/sync/po/0ae964e41b9bda2ff21045533bbcdda9""", new alTextParser)
        println(s.isCalc)
        s.doCalc
        println(s.data)
    }

    implicit def Int2Number(t : java.lang.Integer) : Int = t.intValue
}
