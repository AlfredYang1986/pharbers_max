package com.pharbers.aqll.alCalaHelp.alFinalDataProcess

import com.pharbers.baseModules.PharbersInjectModule

/**
  * Created by clock on 17-12-21.
  */
case class alBsonPath() extends PharbersInjectModule {
    override val id: String = "calc-path"
    override val configPath: String = "pharbers_config/calc_path.xml"
    override val md = "bson-path" :: "hosp" :: "field-names-hosp" :: "integrated" :: "field-names-integrated" :: Nil

    val bson_file_path = config.mc.find(p => p._1 == "bson-path").get._2.toString
}