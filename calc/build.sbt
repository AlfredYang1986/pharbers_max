
lazy val commonSettings = Seq(
    organization := "com.pharbers",
    version := "1.0",
    scalaVersion := "2.11.8"
) 

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.easemob" % "rest-java-sdk" % "1.0.1",
    "com.pharbers" % "pharbers-modules" % "0.1",
    "com.pharbers" % "pharbers-data-parse" % "0.1",
    "com.pharbers" % "pharbers-bson" % "0.1",
    "com.pharbers" % "errorcode" % "0.1",
    "com.pharbers" % "pharbers-max-util" % "0.1",
    "com.pharbers" % "pharbers-memory" % "0.1",
    "com.pharbers" % "pharbers-page-memory" % "0.1",
    "com.pharbers" % "pharbers-message" % "0.1",
    "com.pharbers" % "http" % "0.1",
    "com.typesafe.akka" % "akka-actor_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-agent_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-camel_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-cluster_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-cluster-sharding_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-cluster-tools_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-contrib_2.11" % "2.4.16",
    "com.typesafe.akka" % "akka-distributed-data-experimental_2.11" % "2.4.16",
    "com.typesafe.akka" %% "akka-http-core" % "10.0.1",
    "com.typesafe.akka" %% "akka-http" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-xml" % "10.0.1",
    "com.typesafe.play" % "play-json_2.11" % "2.5.6",
    "org.agrona" % "Agrona" % "0.9.0",
    "org.apache.commons" % "commons-email" % "1.4",
    "org.apache.poi" % "poi-ooxml" % "3.8",
    "org.apache.poi" % "poi-ooxml-schemas" % "3.8",
    "org.mongodb" % "casbah_2.11" % "3.1.1",
    "org.specs2" % "specs2_2.11" % "3.7" % "test",
    "io.aeron" % "aeron-client" % "1.0.4",
    "io.aeron" % "aeron-driver" % "1.0.4",
    "javax.mail" % "mail" % "1.4.7",
    "xerces" % "xercesImpl" % "2.11.0"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
	name := "pharber-calc",
	fork in run := true,
	javaOptions += "-Xmx12G"
  )
//    .enablePlugins(JavaAppPackaging)
