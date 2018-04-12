package controllers

import java.io.{File, FileWriter, PrintWriter}
import javax.inject._

import play.api._
import play.api.libs.json.JsValue
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() extends Controller {
	
	/**
	  * Create an Action to render an HTML page with a welcome message.
	  * The configuration in the `routes` file means that this method
	  * will be called when the application receives a `GET` request with
	  * a path of `/`.
	  */
	def index = Action {
		Ok(views.html.index("Your new application is ready."))
	}
	
	
	
	
	
	def writeFile(jv: JsValue): Unit = {
		try {
			val out = new FileWriter(new File("logs/contactus.txt"),true)
			out.write(jv.toString + "\n")
			out.close()
		} catch {
			case e: Exception => ???
		}
	}
	
	def contactUs = Action { request =>
		request.body.asJson.map { jv =>
			writeFile(jv)
			Redirect("/")
		}.getOrElse(Redirect("/"))
	}
	
	
}
