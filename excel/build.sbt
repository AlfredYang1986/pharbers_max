lazy val commonSettings = Seq(
  organization := "com.pharbers.aqll",
  version := "1.0.1",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
	name := "pharber-excel",
	fork in run := true,
	javaOptions += "-Xmx4G"
  )
