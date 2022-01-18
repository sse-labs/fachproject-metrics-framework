name := "opal-metrics-framework"

version := "0.1"

scalaVersion := "2.12.14"

idePackagePrefix := Some("org.tud.sse.metrics")

libraryDependencies += "com.opencsv" % "opencsv" % "5.5"

val opalVersion = "4.0.0"
libraryDependencies ++= Seq(
  "de.opal-project" % "common_2.12" % opalVersion,
  "de.opal-project" % "framework_2.12" % opalVersion,
  "de.opal-project" % "hermes_2.12" % opalVersion
)

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"


