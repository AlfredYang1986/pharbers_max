lazy val commonSettings = Seq(
	organization := "com.pharbers",
	version := "1.0",
	scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
	settings(commonSettings: _*).
	settings(
		name := "DBPreformanceProtocol",
		fork in run := true,
		javaOptions += "-Xmx5G"
	)
    