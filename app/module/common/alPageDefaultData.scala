package module.common

import com.pharbers.aqll.common.alModularEnum
import com.pharbers.mongodbConnect.connection_instance
import com.pharbers.common.datatype.date.PhDateOpt.Timestamp2yyyyMM

/**
  * Created by liwei on 2017/1/4.
  */
object alPageDefaultData {
    /**
      * 获取页面下拉框默认数据
      *
      * @author liwei
      * @param str   模块类型
      * @param basic 静态数据库
      * @param cores 核心数据库
      * @param flag  是否是市场数据
      * @return
      */
    def PageDefaultData(str: alModularEnum.Value, basic: connection_instance, cores: connection_instance, flag: Boolean = true): (List[String], List[Map[String, Any]]) = {
        try {
            val marketsOpt = PageDefaultMarkets(str, basic, cores)
            val markets = marketsOpt match {
                case None => Nil
                case Some(x) => x
            }
            flag match {
                case true => (markets, Nil)
                case false => {
                    PageDefaultDates(str, basic, cores) match {
                        case None => (markets, Nil)
                        case Some(x) => (markets, x)
                    }
                }
            }
        } catch {
            case ex: Exception => (Nil, Nil)
        }
    }

    /**
      * 获取页面市场下拉框默认数据
      *
      * @author liwei
      * @param str   模块类型
      * @param basic 静态数据库
      * @param cores 核心数据库
      * @return
      */
    def PageDefaultMarkets(str: alModularEnum.Value, basic: connection_instance, cores: connection_instance): Option[List[String]] = {
        try {
            str match {
                case alModularEnum.FU => queryDefaultMarkets(basic)
                case alModularEnum.SC => queryOtherMarkets(cores)
                case alModularEnum.SR => queryDefaultMarkets(basic)
                case alModularEnum.RC => queryOtherMarkets(cores)
                case alModularEnum.RQ => queryDefaultMarkets(basic)
                case _ => None
            }
        } catch {
            case ex: Exception => None
        }
    }

    /**
      * 查询静态数据库市场数据
      *
      * @author liwei
      * @param basic 静态数据库
      * @return
      */
    def queryDefaultMarkets(basic: connection_instance): Option[List[String]] = {
        Some(basic.getCollection("Market").find().toList.map(x => x.get("Market_Name").asInstanceOf[String]))
    }

    def queryOtherMarkets(cores: connection_instance): Option[List[String]] = {
        Some(cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Market")).toList.map(y => y._1.asInstanceOf[String]))
    }

    /**
      * 获取页面日期下拉框默认数据
      *
      * @author liwei
      * @param str   模块类型
      * @param basic 静态数据库
      * @param cores 核心数据库
      * @return
      */
    def PageDefaultDates(str: alModularEnum.Value, basic: connection_instance, cores: connection_instance): Option[List[Map[String, Any]]] = {
        try {
            str match {
                case alModularEnum.FU => None
                case alModularEnum.SC => queryOtherDates(cores)
                case alModularEnum.SR => None
                case alModularEnum.RC => queryOtherDates(cores)
                case alModularEnum.RQ => None
                case _ => None
            }
        } catch {
            case ex: Exception => None
        }
    }

    /**
      * 查询结果检查表非重复日期
      *
      * @author liwei
      * @param cores 核心数据库
      * @return
      */
    def queryOtherDates(cores: connection_instance): Option[List[Map[String, AnyVal]]] = {
        Some(cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Date")).map(y => Map("code" -> y._1.asInstanceOf[Number].longValue(), "name" -> Timestamp2yyyyMM(y._1.asInstanceOf[Number].longValue()).toLong)).toList.sortBy(_.head._2))
    }
}
