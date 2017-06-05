package com.pharbers.aqll.alCalcMemory.aldata

import com.pharbers.aqll.alCalaHelp.alFileHandler.alFileHandlers
import com.pharbers.aqll.alCalcMemory.alexception.alException
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

/**
  * Created by Alfred on 09/03/2017.
  *　 Modify by clock on 05/06/2017.
  */
object alStorage extends alLoggerMsgTrait {
    def apply(path : String, rf : alFileHandlers) : alStorage = new alFileInitStorage(rf.prase(path))
    def apply(lst : List[Any]) : alStorage = new alMemoryInitStorage(lst)
    def apply(ps : List[alPortion]) : alPortionedStorage = {
        val tmp = new alPortionedStorage(Nil, x => x)
        tmp.portions = ps
        tmp
    }

    def groupBy(f : Any => Any)(s : alStorage) : Map[Any, alStorage] = {
        val ss = if (s.isPortions) union(s.upgrade)
                 else s
                 
        if (!ss.isCalc) 
            ss.doCalc
            
        ss.data.groupBy(f).map { x =>
            val s = alStorage(x._2)
            s.doCalc
            x._1 -> s
        }
    }
   
    def combine(lst : List[alStorage]) : alStorage = {
        val s = alStorage.union(lst)
        alStorage(alPortion.union(s.portions).data)
    }
    
    def union(lst : List[alStorage]) : alStorage = alStorage(lst map { x =>
            if (x.isInstanceOf[alPortionedStorage]){
                // TODO 为啥是抛出未知或异常才可以继续计算，不然在Group合并uuid的数据的时候有问题
                ???
            } else alPortion(x.data)
        })
}

abstract class alStorage(val parents : List[alStorage], val f : Any => Any) extends alLoggerMsgTrait {
    var data : List[Any] = Nil
    var portions : List[alPortion] = Nil
    var isCalc = false
    val isPortions = false

    def portion(ps : List[Any] => List[alPortion]) : alPortionedStorage = {
        if (!isCalc) {
            doCalc
            portion(ps)
        } else {
            val tmp = new alPortionedStorage(this :: Nil, f)
            tmp.portions = ps(data)
            tmp.isCalc = true
            tmp
        }
    }

    // Map 计算
    def map(f : Any => Any) : alStorage = {
        if (isPortions) new alPortionedStorage(this :: Nil, f)
        else new alNormalStorage(this :: Nil, f)
    }
    
    // 去重复
    def distinct : alStorage = {
        if (!isCalc) {
            doCalc
            distinct
        } else {
            if (isPortions) alStorage.union(this.upgrade).distinct
            else alStorage(this.data.distinct)
        }
    }

    // 过滤
    def filter(f : Any => Boolean) : alStorage = {
        if (!isCalc) {
            doCalc
            filter(f)
        } else {
            if (isPortions) alStorage.union(this.upgrade).filter(f)
            else alStorage(this.data.filter(f))
        }
    }

    // 计算
    def doCalc

    // 升级， Protion 升级成Storage
    def upgrade : List[alStorage] = this :: Nil

    // 属性计算
    def length : Int = data.length

    // 去掉所有的parent, 方便释放内存
//    def clean = parents = Nil
}

abstract class alInitStorage(fc : Any => Any) extends alStorage(Nil, fc) {
    override def doCalc
}

case class alFileInitStorage(fc : Any => Any) extends alInitStorage(fc) {
    override def doCalc {
        if (!isCalc) {
            data = f("0").asInstanceOf[alFileHandlers].data.toList
            isCalc = true
        }
    }
}

class alMemoryInitStorage(d : List[Any]) extends alInitStorage(null) {
    override def doCalc = {
        if (!isCalc) {
            data = d
            isCalc = true
        }
    }
}

class alPortionedStorage(p : List[alStorage], fc : Any => Any) extends alStorage(p, fc) {
    override val isPortions = true

    override def doCalc = {
        if (!isCalc) {
            p.foreach(_.doCalc)

            parents match {
                case Nil => portions = portions.map (iter => iter.map(f))
                case pt :: Nil => portions = pt.portions.map (iter => iter.map(f))
                case _ => alException(errorToJson("not implement"))
            }
            isCalc = true
        }
    }

    override def portion(ps : List[Any] => List[alPortion]) : alPortionedStorage = this

    override def upgrade : List[alStorage] = portions.map (x => alStorage(x.data))

    override def length : Int = portions.map(x => x.asInstanceOf[alPortion].length).sum
}

class alNormalStorage(p : List[alStorage], fc : Any => Any) extends alStorage(p, fc) {
    override def doCalc {
        if (!isCalc) {
            p.foreach(_.doCalc)

            parents match {
                case Nil => data = data.map(f)
                case pt :: Nil => {
                    data = pt.data.map(f)
                }
                case _ => alException(errorToJson("not implement"))
            }
            isCalc = true
        }
    }
}
