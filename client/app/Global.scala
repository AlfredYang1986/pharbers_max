import play.api.GlobalSettings

object Global extends GlobalSettings {
	
	override def onStart(application: play.api.Application)  = {
		import scala.concurrent.duration._
		import play.api.Play.current
		println("application started")
	}
	
	override def onStop(application: play.api.Application) = {
		println("application stoped")
	}
}