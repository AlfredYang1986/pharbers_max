package test

import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration._

import org.specs2.mutable.Specification

/**
  * Created by alfredyang on 11/07/2017.
  */
class alMaxMasterSpec extends Specfication {
    import scala.concurrent.ExecutionContext.Implicits.global

    override def is = s2"""
        This is a PIC specification to check the 'auth with password' string

            The 'PIC ' auth functions should
                login with user alfred and password 12345                     $e1
                                                                              """

    def e1 = {

    }
}
