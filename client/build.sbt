import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayScala

def common = Seq(
	scalaVersion := "2.11.8",
	version := "1.0",
	organization := "com.pharbers"
)

lazy val root = (project in file(".")).
	enablePlugins(PlayScala).
	settings(common: _*).
	settings(
		name := "pharber-client",
		fork in run := true,
		javaOptions += "-Xmx2G"
	)

routesGenerator := InjectedRoutesGenerator

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
	jdbc,
	cache,
	ws,
	"org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test",
	"commons-httpclient" % "commons-httpclient" % "3.1",
	"org.mongodb" % "casbah_2.11" % "3.1.1",
	"com.easemob" % "rest-java-sdk" % "1.0.1",
    	"com.pharbers" % "pharbers-modules" % "0.1",
	"com.pharbers" % "pharbers-message" % "0.1",
    	"com.pharbers" % "mongodb-connect" % "0.1",
	"com.pharbers" % "pharbers-max-util" % "0.1",
	"com.pharbers" % "pharbers-sercurity" % "0.1",
	"com.pharbers" % "mongodb-driver" % "0.1",
	"com.pharbers" % "mongodb-manager" % "0.1",
	"com.pharbers" % "pharbers-cli-traits" % "0.1",
	"com.pharbers" % "pharbers-pattern" % "0.1",
	"com.pharbers" % "auth-token" % "0.1",
	"com.pharbers" % "encrypt" % "0.1",
	"com.pharbers" % "http" % "0.1",
	"com.pharbers" % "errorcode" % "0.1",
	"org.apache.commons" % "commons-email" % "1.4"
)






