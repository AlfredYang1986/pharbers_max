package module.common

import com.pharbers.aqll.common.alDao.data_connection
import com.pharbers.aqll.common.alDate.scala.alDateOpt._
import play.api.libs.json.Json.toJson
/**
  * Created by liwei on 2017/1/4.
  */
object alPageDefaultData {
    /**
      * 获取页面下拉框默认数据
      * @author liwei
      * @param str      模块类型
      * @param basic    静态数据库
      * @param cores    核心数据库
      * @param flag     是否是市场数据
      * @return
      */
    def PageDefaultData(company: String,str: alModularEnum.Value,basic: data_connection,cores: data_connection,flag: Boolean = true): (List[String],List[Map[String,Any]]) = {
        try {
            val marketsOpt = PageDefaultMarkets(company,str,basic,cores)
            val markets = marketsOpt match {
                case None => Nil
                case _ => marketsOpt.get
            }
            flag match {
                case true => (markets,Nil)
                case false => {
                    val datesOpt = PageDefaultDates(company,str,basic,cores)
                    val dates = datesOpt match {
                        case None => Nil
                        case _ => datesOpt.get
                    }
                    (markets,dates)
                }
            }
        } catch {
            case ex: Exception => (Nil,Nil)
        }
    }

    /**
      * 获取页面市场下拉框默认数据
      * @author liwei
      * @param str      模块类型
      * @param basic    静态数据库
      * @param cores    核心数据库
      * @return
      */
    def PageDefaultMarkets(company: String,str: alModularEnum.Value,basic: data_connection,cores: data_connection): Option[List[String]] = {
        try {
            str match {
                case alModularEnum.FU => queryDefaultMarkets(basic)
                case alModularEnum.SC => Some(cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Market")).toList.map(y => y._1.asInstanceOf[String]))
                case alModularEnum.SR => queryDefaultMarkets(basic)
                case alModularEnum.RC => queryResultCheckMarkets(company,queryUUID(company),cores)
                case alModularEnum.RQ => queryDefaultMarkets(basic)
                case _ => None
            }
        } catch {
            case ex: Exception => None
        }
    }

    /**
      * 查询静态数据库市场数据
      * @author liwei
      * @param basic    静态数据库
      * @return
      */
    def queryDefaultMarkets(basic: data_connection): Option[List[String]] = {
        Some(basic.getCollection("Market").find().toList.map(x => x.get("Market_Name").asInstanceOf[String]))
    }

    /**
      * 查询结果检查表非重复市场数据
      * @author liwei
      * @param company  公司名
      * @param uuid     UUID
      * @param cores    核心数据库
      * @return
      */
    def queryResultCheckMarkets(company: String,uuid: Option[String],cores: data_connection): Option[List[String]] = uuid match {
        case None => None
        case Some(x) => {
            Some(List("INFMarket"))
            //Some(cores.getCollection(company+x).find().toList.groupBy(x => x.get("Market")).toList.map(y => y._1.asInstanceOf[String]))
        }
    }

    /**
      * 获取页面日期下拉框默认数据
      * @author liwei
      * @param str      模块类型
      * @param basic    静态数据库
      * @param cores    核心数据库
      * @return
      */
    def PageDefaultDates(company: String,str: alModularEnum.Value,basic: data_connection,cores: data_connection): Option[List[Map[String,Any]]] = {
        try {
            str match {
                case alModularEnum.FU => None
                case alModularEnum.SC => Some(cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Date")).map(y => Map("code" -> y._1.asInstanceOf[Number].longValue(),"name" -> Timestamp2yyyyMM(y._1.asInstanceOf[Number].longValue()))).toList)
                case alModularEnum.SR => None
                case alModularEnum.RC => queryResultCheckDates(company,queryUUID(company),cores)
                case alModularEnum.RQ => None
                case _ => None
            }
        } catch {
            case ex: Exception => None
        }
    }

    /**
      * 查询结果检查表非重复日期
      * @author liwei
      * @param company  公司名
      * @param uuid     UUID
      * @param cores    核心数据库
      * @return
      */
    def queryResultCheckDates(company: String,uuid: Option[String],cores: data_connection): Option[List[Map[String,Any]]] = uuid match {
        case None => None
        case Some(x) => {
            Some(List(Map("code" -> 1477929600000L,"name" -> "201611")))
            //Some(cores.getCollection(company+x).find().toList.groupBy(x => x.get("Date")).map(y => Map("code" -> y._1.asInstanceOf[Number].longValue(),"name" -> Timestamp2yyyyMM(y._1.asInstanceOf[Number].longValue()))).toList)
        }
    }

    /**
      * Query UUID
      * @author liwei
      * @param company  公司名
      * @return
      */
    def queryUUID(company: String): Option[String] = {
        val uuidjson = alCallHttp("/queryUUID",toJson(Map("company" -> toJson(company)))).call
        Some((uuidjson \ "result").asOpt[String].get)
    }
}
