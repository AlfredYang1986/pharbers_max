package com.pharbers.aqll.stub

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.aldata.alPortion
import com.pharbers.aqll.alcalc.alfilehandler.alexcel.alIntegrateddataparser
import com.pharbers.aqll.alcalc.alfilehandler.altext.{alTextParser, alTextSync}
import com.pharbers.aqll.alcalc.alcmd.pkgcmd.{pkgCmd, unPkgCmd}
import com.pharbers.aqll.alcalc.alcmd.scpcmd.{cpCmd, scpCmd}

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

    {
        // test case 5 : compress data ** gzip
        val p = new pkgCmd("""config/sync/po/0ae964e41b9bda2ff21045533bbcdda9""" :: Nil, """config/compress/test""")
        p.excute
    }

    {
        // test case 6 : restore from compress file ** gzip
        val p = new unPkgCmd("""config/compress/test""", """config/compress""")
        p.excute
    }

//    {
//        // test case 7 : scp ** gzip // 需要提前部署scp rsa秘钥
//        val p = new scpCmd("""config/compress/test.tar.gz""", """Desktop/test""", "10.6.20.138", "BM", "BM")
//        p.excute
//    }

    {
        // test case 8 : cp ** gzip
        val p = new cpCmd("""config/compress/test.tar.gz""", """~/Desktop/test""")
        p.excute
    }


    implicit def Int2Number(t : java.lang.Integer) : Int = t.intValue
}
