package module.common

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import scala.collection.mutable.ListBuffer
/**
  * Created by liwei on 2017/4/27.
  */
object alOperation {
  /**
    * @author liwei
    * @param arr
    * @return
    */
  def richDateArr(arr: Array[String]): List[Map[String,Any]] = {
    arr.map(x => Map("Date" -> x,"f_sales" -> 0.0)).toList
  }

  /**
    * @author liwei
    * @param arr
    * @param lst
    * @return
    */
  def matchDateData(arr: Array[String],lst: List[List[Map[String,Any]]]): List[Map[String,Any]] = {
    val date_lst = richDateArr(arr)

    val temp_head_lst = date_lst map { x =>
      val obj = lst.head.find(y => y.get("Date").get.equals(x.get("Date").get))
      obj match {
        case None => x
        case _ => obj.get
      }
    }
    val temp_tail_lst: List[Map[String,Any]] = Nil
    temp_tail_lst match {
      case Nil => temp_head_lst
      case _ => {
        date_lst map { x =>
          val obj = lst.tail.head.find(y => y.get("Date").get.equals(x.get("Date").get))
          obj match {
            case None => x
            case _ => obj.get
          }
        }

        temp_tail_lst map { x =>
          val obj = temp_head_lst.find(y => y.get("Date").get.equals(x.get("Date").get))
          obj match {
            case None => x
            case _ => obj.get
          }
        }
      }
    }
  }

  /**
    * @author liwei
    * @param lst
    * @return
    */
  def salesSumByDate(lst: List[Map[String,Any]]): List[Map[String,Any]] = {
    var date = ""
    var sales_sum = 0.0
    val list = new ListBuffer[Map[String,Any]]()
    lst foreach {x =>
      val obj_date = x.get("Date").get.asInstanceOf[String]
      val obj_sales = x.get("f_sales").get.asInstanceOf[Number].doubleValue()
      date match {
        case i if i.equals("") => date = obj_date
        case i if i.equals(obj_date) => sales_sum = sales_sum + obj_sales
        case i if !i.equals(obj_date) => {
          list.append(Map("Date" -> date,"f_sales" -> sales_sum))
          date = obj_date
          sales_sum = 0.0
        }
      }
    }
    list.toList
  }


  /**
    * @author liwei
    * @param m_data
    * @param r_data
    * @return
    */
  def matchCityData(m_data: List[Map[String,Any]])(r_data: List[Map[String,Any]]): List[Map[String,Any]] = r_data map(x => findSomeDataByCity(m_data.find(y => x.get("City").get.equals(y.get("City").get)))(x))

  /**
    * @author liwei
    * @param omap
    * @param map
    * @return
    */
  def findSomeDataByCity(omap: Option[Map[String,Any]])(map: Map[String,Any]) : Map[String,Any] = omap match {
    case None => map
    case _ => omap.get
  }

  /**
    * @author liwei
    * @param lst
    * @param s
    * @return
    */
  def lst2Json(lst: List[Map[String,Any]],s : Int): JsValue = s match {
      case 1 => toJson(lst.map(x => toJson(Map("Date" -> toJson(x.get("Date").get.asInstanceOf[String]),"f_sales" -> toJson(x.get("f_sales").get.asInstanceOf[Number].doubleValue())))))
      case 2 => toJson(lst map(x => toJson(Map("City" -> toJson(x.get("City").get.asInstanceOf[String]),"f_sales" -> toJson(x.get("f_sales").get.asInstanceOf[Number].doubleValue())))))
  }
}