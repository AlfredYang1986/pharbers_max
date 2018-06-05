package controllers

import play.api.mvc._

class IndexController extends Controller {
	def index(path: String) = Action {
		Ok(views.html.index())
	}
}
