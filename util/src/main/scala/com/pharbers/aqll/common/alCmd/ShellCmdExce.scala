package com.pharbers.aqll.common.alCmd

import com.pharbers.aqll.common.alCmd.almodel.alResultDefines
/**
  * Created by liwei on 2017/5/16.
  */
trait ShellCmdExce {

  def process : Process = null

  val cmd : String = ""

  def excute : List[alResultDefines]

  def resultDefines(c: Int, n: String, m: String) : List[alResultDefines] = {
    val result : alResultDefines = new alResultDefines()
    result.setCode(c)
    result.setName(n)
    result.setMessage(m)
    result :: Nil
  }
}
