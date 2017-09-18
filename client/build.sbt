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

libraryDependencies ++= Seq(
	jdbc,
	cache,
	ws,
	"org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"
)






