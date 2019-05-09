name := "Proximus"

version := "1"

scalaVersion := "2.12.8"

// SBT scoverage settings
coverageMinimum := 90
coverageFailOnMinimum := true

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5"
)

scalacOptions ++= Seq("-deprecation", "-feature")

javaOptions ++= Seq(
  "-XX:+CMSClassUnloadingEnabled" // Enable class unloading under the CMS GC
)

// Test suite settings
fork in Test := true
