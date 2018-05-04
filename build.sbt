import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayScala
import scala.language.experimental.macros

def common = Seq(
	scalaVersion := "2.11.8",
	version := "1.1",
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
	"commons-httpclient" % "commons-httpclient" % "3.1",
	"org.mongodb" % "casbah_2.11" % "3.1.1",
	"com.easemob" % "rest-java-sdk" % "1.0.1",

	"com.pharbers" % "pharbers-module" % "0.1",
	"com.pharbers" % "pharbers-mongodb" % "0.1",
	"com.pharbers" % "pharbers-errorcode" % "0.1",
	"com.pharbers" % "pharbers-mongodb" % "0.1",
	"com.pharbers" % "pharbers-third" % "0.1",
	"com.pharbers" % "pharbers-security" % "0.1",
	"com.pharbers" % "pharbers-redis" % "0.1",
	"com.pharbers" % "pharbers-pattern" % "0.1",
	"com.pharbers" % "pharbers-redis" % "0.1",
	"com.pharbers" % "pharbers-max" % "0.1",

	"org.apache.commons" % "commons-email" % "1.4",
	"net.debasishg" % "redisclient_2.11" % "3.4",
	"org.apache.kafka" % "kafka_2.11" % "1.1.0",
	"org.apache.kafka" % "kafka-streams" % "1.1.0",
	"org.apache.avro" % "avro" % "1.7.6",
	"log4j" % "log4j" % "1.2.17",
	"commons-io" % "commons-io" % "2.4",
	"org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "org.specs2" % "specs2_2.11" % "3.7" % "test"
)






