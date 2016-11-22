/**
 * ordering and comparing
 * Created By Alfred Yang
 */

package com.pharbers.aqll.util.dao

class QueryOrdering[T] extends Ordering[T] {
	def compare(x: T, y: T): Int = {
		x match {
		  case x: Int => 
		    {
		      Ordering[Int].compare(x, y.asInstanceOf[Int])
		    }
		  case x: String => 
		    {
		      Ordering[String].compare(x, y.asInstanceOf[String])
		    }
		  case x: Long => 
		    {
		      Ordering[Long].compare(x, y.asInstanceOf[Long])
		    }
		  case x: Double => 
		    {
		      Ordering[Double].compare(x, y.asInstanceOf[Double])
		    }
		  case _ => throw new Exception
		}
	}
}