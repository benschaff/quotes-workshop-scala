import play.{PlayImport, PlayScala}

name := "quotes-workshop-scala"

version := "1.0.0"

libraryDependencies ++= Seq(
  PlayImport.ws,
  PlayImport.cache,
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "requirejs" % "2.1.14-3",
  "org.webjars" % "jquery" % "1.11.1",
  "org.webjars" % "angularjs" % "1.2.25",
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.webjars" % "angular-loading-bar" % "0.5.1",
  "org.webjars" % "highcharts" % "4.0.3-1"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)