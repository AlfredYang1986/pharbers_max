package com.pharbers.aqll.util

import java.net.NetworkInterface

/**
  * Created by qianpeng on 2017/3/3.
  */
object GetLocalHostIP {
	def isWindowsOS: String = {
		""
	}

	def isLinuxOS(name: String): String = {
		try {
			var ip = ""
			val netInterfaces = NetworkInterface.getNetworkInterfaces()
			while (netInterfaces.hasMoreElements()) {
				val ni = netInterfaces.nextElement()
				if(ni.getDisplayName().equals(name)){
					println(s"ni.getDisplayName() = ${ni.getDisplayName()}")
					val ips = ni.getInetAddresses()
					while (ips.hasMoreElements()) {
						ip = ips.nextElement().getHostAddress()
//						if(ips.nextElement().getHostAddress().indexOf(":") > 0) {
//							println(ips.nextElement().getHostAddress())
//							ip = ips.nextElement().getHostAddress()
//						}
					}
				}
			}
			println(s"ip   ===  ${ip}")
			ip
		} catch {
			case ex: Exception => ???
		}
	}
}
