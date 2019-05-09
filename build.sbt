name := "Proximus"

version := "1"

scalaVersion := "2.12.8"

// SBT scoverage settings
coverageMinimum := 80
coverageFailOnMinimum := true

resolvers ++= Seq(
  "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5"
)

scalacOptions ++= Seq("-deprecation", "-feature")

javaOptions ++= Seq(
  // The permanent space is where the classes, methods, internalized strings,
  // and similar objects used by the VM are stored and never deallocated
  "-XX:MaxPermSize=2048M",
  "-XX:+CMSClassUnloadingEnabled" // Enable class unloading under the CMS GC
)

// Test suite settings
fork in Test := true
// Show runtime of tests
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
