lazy val commonSettings = Seq(
  organization := "com.pharbers",
  version := "1.0",
  scalaVersion := "2.11.8"
)

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-core" % "10.0.1",
    "com.typesafe.akka" %% "akka-http" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-jackson" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-xml" % "10.0.1",
	"com.google.code.gson" % "gson" % "2.2.4",
    "javax.mail" % "mail" % "1.4.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
	name := "pharber-calc",
	fork in run := true,
	javaOptions += "-Xmx4G"
  )
