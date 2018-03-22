
lazy val commonSettings = Seq(
    organization := "com.pharbers",
    version := "1.1",
    scalaVersion := "2.11.8"
) 

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.specs2" % "specs2_2.11" % "3.7" % "test",
    "com.easemob" % "rest-java-sdk" % "1.0.1",

    "com.pharbers" % "pharbers-all-libs" % "1.0",

    "com.pharbers" % "pharbers-module" % "0.1",
    "com.pharbers" % "pharbers-errorcode" % "0.1",
    "com.pharbers" % "pharbers-mongodb" % "0.1",
    "com.pharbers" % "pharbers-third" % "0.1",
    "com.pharbers" % "pharbers-spark" % "0.1",
    "com.pharbers" % "pharbers-memory" % "0.1",
    "com.pharbers" % "pharbers-security" % "0.1",
    "com.pharbers" % "pharbers-message" % "0.1",
    "com.pharbers" % "pharbers-redis" % "0.1",
    "com.pharbers" % "pharbers-max" % "0.1",
    "com.pharbers" % "pharbers-pattern" % "0.1",

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

    "org.apache.spark" % "spark-sql_2.11" % "2.0.0" exclude("org.slf4j","slf4j-log4j12"),
    "org.mongodb.spark" % "mongo-spark-connector_2.11" % "2.0.0",
    "org.apache.hadoop" % "hadoop-client" % "2.2.0" exclude("org.slf4j","slf4j-log4j12"),
    "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7",

    "org.agrona" % "Agrona" % "0.9.0",
    "org.apache.commons" % "commons-email" % "1.4",
    "org.apache.poi" % "poi-ooxml" % "3.8",
    "org.apache.poi" % "poi-ooxml-schemas" % "3.8",
    "org.mongodb" % "casbah_2.11" % "3.1.1",
    "io.aeron" % "aeron-client" % "1.0.4",
    "javax.mail" % "mail" % "1.4.7",
    "net.debasishg" % "redisclient_2.11" % "3.4",
    "xerces" % "xercesImpl" % "2.11.0"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
	name := "pharber-calc",
	fork in run := true,
	javaOptions += "-Xmx8G"
  ).enablePlugins(JavaAppPackaging)
