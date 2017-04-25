package module.common

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import scala.collection.mutable.ListBuffer

/**
  * Created by liwei on 2017/4/25.
  */
object TwoTectonic {

  /**
    * @author liwei
    * @param arr
    * @return
    */
  def rich(arr: Array[String]): List[Map[String,Any]] = {
    arr.map{ x =>
      Map("Date" -> x,"HospNum" -> 0,"ProductNum" -> 0,"MarketNum" -> 0,"Sales" -> 0.0,"Units" -> 0.0)
    } toList
  }

  /**
    * @author liwei
    * @param arr
    * @param lst
    * @return
    */
  def rich_curr12(arr: Array[String],lst: List[List[Map[String,AnyRef]]]): List[Map[String,Any]] = {
    val head_lst = lst.head
    val tail_lst = lst.tail.head
    val date_lst = rich(arr)

    val temp_head_lst = new ListBuffer[Map[String,Any]]()
    val temp_tail_lst = new ListBuffer[Map[String,Any]]()

    date_lst foreach { x =>
      val obj = head_lst.find(y => y.get("Date").get.equals(x.get("Date").get))
      obj match {
        case None => temp_head_lst.append(x)
        case _ => temp_head_lst.append(obj.get)
      }
    }

    date_lst foreach { x =>
      val obj = tail_lst.find(y => y.get("Date").get.equals(x.get("Date").get))
      obj match {
        case None => temp_tail_lst.append(x)
        case _ => temp_tail_lst.append(obj.get)
      }
    }

    val temp_fina_lst = new ListBuffer[Map[String,Any]]()
    temp_tail_lst foreach { x =>
      val obj = temp_head_lst.find(y => y.get("Date").get.equals(x.get("Date").get))
      obj match {
        case None => temp_fina_lst.append(x)
        case _ => temp_fina_lst.append(obj.get)
      }
    }
    temp_fina_lst.toList
  }

  /**
    * @author liwei
    * @param arr
    * @param lst
    * @return
    */
  def rich_last12(arr: Array[String],lst: List[Map[String,AnyRef]]): List[Map[String,Any]] = {
    val temp_fina_lst = new ListBuffer[Map[String,Any]]()
    val date_lst = rich(arr)
    lst.size match {
      case 0 => {
        date_lst.foreach( x => temp_fina_lst.append(x))
      }
      case _ => {
        date_lst.foreach{ x =>
          val obj = lst.find(y => y.get("Date").get.equals(x.get("Date").get))
          obj match {
            case None => temp_fina_lst.append(x)
            case _ => temp_fina_lst.append(obj.get)
          }
        }
      }
    }
    temp_fina_lst.toList
  }

  /**
    * @author liwei
    * @param lst
    * @return
    */
  def lst2json(lst: List[Map[String,Any]]): JsValue ={
    val json = lst.map{ x => toJson(Map(
        "Date" -> toJson(x.get("Date").get.asInstanceOf[String]),
        "HospNum" -> toJson(x.get("HospNum").get.asInstanceOf[Number].intValue()),
        "ProductNum" -> toJson(x.get("ProductNum").get.asInstanceOf[Number].intValue()),
        "MarketNum" -> toJson(x.get("MarketNum").get.asInstanceOf[Number].intValue()),
        "Sales" -> toJson(x.get("Sales").get.asInstanceOf[Number].doubleValue()),
        "Units" -> toJson(x.get("Units").get.asInstanceOf[Number].doubleValue())
      ))
    }
    toJson(json)
  }
}
