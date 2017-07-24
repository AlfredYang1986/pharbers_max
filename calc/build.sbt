lazy val commonSettings = Seq(
  organization := "com.pharbers",
  version := "1.0",
  scalaVersion := "2.11.8"
)

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-core" % "10.0.1",
    "com.typesafe.akka" %% "akka-http" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-xml" % "10.0.1",
	"com.google.code.gson" % "gson" % "2.2.4",
    "javax.mail" % "mail" % "1.4.7",
	"ch.qos.logback" % "logback-classic" % "1.1.3",
	"com.easemob" % "rest-java-sdk" % "1.0.1",
	"com.typesafe.play" % "play-json_2.11" % "2.5.6",
	"org.apache.commons" % "commons-email" % "1.4",
    "org.specs2" % "specs2_2.11" % "3.7" % "test"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
	name := "pharber-calc",
	fork in run := true,
	javaOptions += "-Xmx4G"
  )
