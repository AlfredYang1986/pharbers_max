package com.pharbers.aqll.util

/**
  * Created by Faiz on 2017/1/17.
  */
object GetProperties {
    import java.util.Properties
    import java.io.FileInputStream

    def loadProperties(filename: String) : Properties = {
        val properties = new Properties()
        properties.load(new FileInputStream(Thread.currentThread().getContextClassLoader.getResource(filename).getPath))
        properties
    }
}
