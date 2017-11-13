lazy val commonSettings = Seq(
    organization := "com.pharbers",
    version := "1.0",
    scalaVersion := "2.11.8"
)

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
    "com.pharbers" % "pharbers-modules" % "0.1",
    "com.pharbers" % "pharbers-data-parse" % "0.1",
    "com.pharbers" % "pharbers-bson" % "0.1",
    "com.pharbers" % "pharbers-memory" % "0.1",
    "com.pharbers" % "pharbers-page-memory" % "0.1",
    "com.pharbers" % "pharbers-message" % "0.1",
    "com.typesafe.akka" %% "akka-http-core" % "10.0.1",
    "com.typesafe.akka" %% "akka-http" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-xml" % "10.0.1",
    "javax.mail" % "mail" % "1.4.7",
    "com.pharbers" % "http" % "0.1",
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
	javaOptions += "-Xmx2G"
  )
