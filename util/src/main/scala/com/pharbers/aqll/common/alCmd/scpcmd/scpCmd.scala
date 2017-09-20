package com.pharbers.aqll.common.alCmd.scpcmd

import com.pharbers.aqll.common.alCmd.alShellOtherCmdExce

/**
  * Created by Alfred on 10/03/2017.
  */

/**
  * 本地传输远程
  * @param file 本地目标文件
  * @param des_path 远程目标地址目录
  * @param host 远程地址
  * @param user_name 用户名称
  */
case class scpCmd(file : String, des_path : String, host : String, user_name : String) extends alShellOtherCmdExce {
    override def cmd = s"scp ${file} ${user_name}@${host}:~/${des_path}"
}

/**
  * 远程传输本地
  * @param file 远程目标文件
  * @param targt_path 本地地址目录
  * @param host 远程地址
  * @param user_name 用户名称
  */
case class scpRemotCmd(file: String, targt_path: String, host: String, user_name: String) extends alShellOtherCmdExce {
    override def cmd = s"scp ${user_name}@${host}:~/${file} $targt_path"
}
