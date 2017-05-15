package com.pharbers.aqll.common

/**
  * Created by qianpeng on 2017/2/18.
  */
import java.io._
import java.net._

import play.api.libs.json.JsValue
import play.api.libs.json.Json.{parse, toJson}

import scala.collection.JavaConversions._
import scala.io.Source.fromInputStream

object HTTP {
	def apply(url : String) : httpOpt = new httpOpt(url)
}

case class httpOpt(val url : String) {

	private var connection = (new URL(url)).openConnection()

	/**
	  * cookies
	  */
	var cookies : Map[String, String] = Map.empty

	def storeCookies : httpOpt = {
		connection.getHeaderFields.lift("Set-Cookie") match {
			case Some(cookieList) => cookieList foreach { c =>
				val (name,value) = c span { _ != '='}
				cookies += name -> (value drop 1)
			}
			case None =>
		}
		this
	}

	def loadCookies : httpOpt = {
		for ((name, value) <- cookies) connection.setRequestProperty("Cookie", name + "=" + value)
		this
	}

	/**
	  * set request headers
	  */
	def header(parameters : (String, String)*) : httpOpt = {
		for ((name, value) <- parameters) connection.setRequestProperty(name, value)
		this
	}

	private def encodePostParameters(data: Map[String, String]) =
		for ((name,value) <- data)
			yield URLEncoder.encode(name) + "=" + URLEncoder.encode(value)

	/**
	  * last call
	  */
	def get(parameters : Map[String, String]) = {
		loadCookies
		connection.connect
		storeCookies
		fromInputStream(connection.getInputStream)
	}

	def post(data : String) : String = {
		connection.setDoOutput(true)
		connection.connect

		val postStream = new OutputStreamWriter(connection.getOutputStream())
		postStream.write(data)
		postStream.flush
		postStream.close

		val in = new BufferedReader(new InputStreamReader(connection.getInputStream))
		val buffer = new StringBuffer
		var line = ""
		do {
			buffer.append(line)
			line = in.readLine
		} while (line != null)

		buffer.toString
	}

	def post(parameters : JsValue) : JsValue = {
		connection.setRequestProperty("Content-Type", "application/json")
		connection.setRequestProperty("connection", "keep-alive")
		connection.setDoOutput(true)
		connection.setDoInput(true)
		connection.connect

		val postStream = new OutputStreamWriter(connection.getOutputStream())
		postStream.write(parameters.toString)
		postStream.flush
		postStream.close

		val in = new BufferedReader(new InputStreamReader(connection.getInputStream))
		val buffer = new StringBuffer
		var line = ""
		do {
			buffer.append(line)
			line = in.readLine
		} while (line != null)

		parse(buffer.toString)
	}

	def post(parameters : (String, JsValue)*) : JsValue = {
		var para : Map[String, JsValue] = Map.empty
		for ((name, value) <- parameters) para += name -> value

		post(toJson(para))
	}

	def delete : JsValue = {
		val httpURLConnection = connection.asInstanceOf[HttpURLConnection]
		httpURLConnection.setRequestMethod("DELETE")
		httpURLConnection.connect

		val in = new BufferedReader(new InputStreamReader(connection.getInputStream))
		val buffer = new StringBuffer
		var line = ""
		do {
			buffer.append(line)
			line = in.readLine
		} while (line != null)

		parse(buffer.toString)
	}
}
