package com.pharbers.aqll.stub

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alprecess.alPrecess
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.read_excel_split
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by BM on 10/03/2017.
  */
object stub_test_2 extends App {
    // test cases for stages
    {
        // test cases 1 : load data for stage
//        val s = alStage("""config/new_test/2016-01.xlsx""")
//        val p = read_excel()
//        val s1 = p.precess(s).head
//
//        println(s1.getClass)
//        println(s1.storages.head)
//
//        val ss = s1.storages.head.asInstanceOf[alStorage]
//        println(ss.isCalc)
//        ss.doCalc
//        println(ss.data)
    }

    {
        // test case 2 : calc precess
//        val s = alStage("""config/new_test/2016-01.xlsx""")
//        val p = read_excel()
//        val s1 = p.precess(s).head
//
//        println(s1.getClass)
//        println(s1.storages.head)
//
//        {
//            val ss = s1.storages.head.asInstanceOf[alStorage]
//            println(ss.isCalc)
//        }
//
//        {
//            val p = do_calc()
//            val s2 = p.precess(s1).head
//            val ss = s2.storages.head.asInstanceOf[alStorage]
//            println(ss.isCalc)
//        }
    }

    {
        // test case 3 : presist data
//        val s = alStage("""config/new_test/2016-01.xlsx""")
//        val p = read_excel()
//        val s1 = p.precess(s).head
//
//        println(s1.length)
//
//        val p_do = do_calc()
//        val s2 = p_do.precess(s1).head
//        val ss = s2.storages.head.asInstanceOf[alStorage]
//        println(ss.isCalc)
//        println(s2.getClass)
//        println(s2.isCalc)
//        println(s2.length)
//
//        val p_presist = presist_data()
//        val s3 = p_presist.precess(s2)
//        println(s3)
//        println(p_presist.result)
    }

    {
        // test case 4 : restore data
//        val s = alStage("""config/new_test/2016-01.xlsx""")
//        val p = read_excel()
//        val s1 = p.precess(s).head
//
//        val p_do = do_calc()
//        val s2 = p_do.precess(s1).head
//        val ss = s2.storages.head.asInstanceOf[alStorage]
//
//        val p_presist = presist_data()
//        val s3 = p_presist.precess(s2)
//
//        val p_restoe = restore_data()
//        val s4 = alStage(p_presist.result.get.asInstanceOf[(String, List[String])]._2.head)
//        val s5 = p_restoe.precess(s4).head
//        val s6 = p_do.precess(s5).head
//        println(s6.length)
    }

    {
        // test case 5 : Split and presist and restore
        val s = alStage("""config/new_test/2016-01.xlsx""")
        val p = read_excel()
        val s1 = p.precess(s).head

        val p_do = do_calc()
        val s2 = p_do.precess(s1).head
        println(s2.length)

        val p_split = split_data(read_excel_split(Map(read_excel_split.section_number -> 4)))
        val s3 = p_split.precess(s2).head

        val s4 = p_do.precess(s3).head
        println(s4.length)

        val p_presist = presist_data()
        val s5 = p_presist.precess(s4)

        println(p_presist.result.get)

        {
            val p_restoe = restore_data()
            val s4 = alStage(p_presist.result.get.asInstanceOf[(String, List[String])]._2.head)
            val s5 = p_restoe.precess(s4).head
            val s6 = p_do.precess(s5).head
            println(s6.length)
        }
    }

}
