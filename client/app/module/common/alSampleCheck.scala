package module.common

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by liwei on 2017/4/25.
  */
object alSampleCheck {

  /**
    * @author liwei
    * @param arr
    * @return
    */
  def richDateArr(arr: Array[String]): List[Map[String,Any]] = {
    arr.map(x => Map("Date" -> x,"HospNum" -> 0,"ProductNum" -> 0,"MarketNum" -> 0,"Sales" -> 0.0,"Units" -> 0.0)).toList
  }

  /**
    * @author liwei
    * @param arr
    * @param lst
    * @return
    */
  def matchThisYearData(arr: Array[String],lst: List[List[Map[String,AnyRef]]]): List[Map[String,Any]] = {
    val date_lst = richDateArr(arr)

    val temp_head_lst = date_lst map { x =>
      val obj = lst.head.find(y => y.get("Date").get.equals(x.get("Date").get))
      obj match {
        case None => x
        case _ => obj.get
      }
    }

    val temp_tail_lst = date_lst map { x =>
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

  /**
    * @author liwei
    * @param arr
    * @param lst
    * @return
    */
  def matchLastYearData(arr: Array[String],lst: List[Map[String,AnyRef]]): List[Map[String,Any]] = {
    val date_lst = richDateArr(arr)
    lst.size match {
      case 0 => date_lst
      case _ => {
        date_lst map{ x =>
          val obj = lst.find(y => y.get("Date").get.equals(x.get("Date").get))
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
  def lst2Json(lst: List[Map[String,Any]]): JsValue ={
    toJson(lst.map{ x => toJson(
      Map(
        "Date" -> toJson(x.get("Date").get.asInstanceOf[String]),
        "HospNum" -> toJson(x.get("HospNum").get.asInstanceOf[Number].intValue()),
        "ProductNum" -> toJson(x.get("ProductNum").get.asInstanceOf[Number].intValue()),
        "MarketNum" -> toJson(x.get("MarketNum").get.asInstanceOf[Number].intValue()),
        "Sales" -> toJson(x.get("Sales").get.asInstanceOf[Number].doubleValue()),
        "Units" -> toJson(x.get("Units").get.asInstanceOf[Number].doubleValue())
      )
    )})
  }
}
