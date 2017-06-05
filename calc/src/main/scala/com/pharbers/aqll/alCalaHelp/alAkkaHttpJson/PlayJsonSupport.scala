package com.pharbers.aqll.alCalaHelp.alAkkaHttpJson

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.ContentTypeRange
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.server.{ RejectionError, ValidationRejection }
import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, Unmarshaller }
import akka.util.ByteString
import play.api.libs.json.{ JsError, JsValue, Json, Reads, Writes }
import scala.collection.immutable.Seq

/**
  * Created by qianpeng on 2017/6/4.
  */

trait PlayJsonSupport {
	def unmarshallerContentTypes: Seq[ContentTypeRange] = Seq(`application/json`)
	
	case class PlayJsonError(error: JsError) extends RuntimeException {
		override def getMessage: String =
			JsError.toJson(error).toString()
	}
	
	private val jsonStringUnmarshaller =
		Unmarshaller.byteStringUnmarshaller
			.forContentTypes(unmarshallerContentTypes: _*)
			.mapWithCharset {
				case (ByteString.empty, _) => throw Unmarshaller.NoContentException
				case (data, charset)       => data.decodeString(charset.nioCharset.name)
			}
	
	private val jsonStringMarshaller = Marshaller.stringMarshaller(`application/json`)
	
	/**
	  * HTTP entity => `A`
	  *
	  * @tparam A type to decode
	  * @return unmarshaller for `A`
	  */
	implicit def unmarshaller[A: Reads]: FromEntityUnmarshaller[A] = {
		def read(json: JsValue) =
			implicitly[Reads[A]]
				.reads(json)
				.recoverTotal { e =>
					throw RejectionError(
						ValidationRejection(JsError.toJson(e).toString, Some(PlayJsonError(e)))
					)
				}
		jsonStringUnmarshaller.map(data => read(Json.parse(data)))
	}
	
	/**
	  * `A` => HTTP entity
	  *
	  * @tparam A type to encode
	  * @return marshaller for any `A` value
	  */
	implicit def marshaller[A](implicit writes: Writes[A],
	                           printer: JsValue => String = Json.prettyPrint): ToEntityMarshaller[A] =
		jsonStringMarshaller.compose(printer).compose(writes.writes)
}
