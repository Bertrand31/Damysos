name := "Damysos"

version := "1"

scalaVersion := "2.12.8"

// SBT scoverage settings
coverageMinimum := 95
coverageFailOnMinimum := true

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0-M1",
  "org.scalatest" %% "scalatest" % "3.0.7",
)

scalacOptions ++= Seq(
  "-deprecation", // Warn about deprecated features
  "-encoding", "UTF-8", // Specify character encoding used by source files
  "-feature", // Emit warning and location for usages of features that should be imported explicitly
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds", // Allow higher-kinded types
  "-unchecked", // Enable additional warnings where generated code depends on assumptions
  "-Xfatal-warnings", // Fail on warnings
  "-Xfuture", // Turn on future language features
  "-Xlint:_", // Enable all available style warnings
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-macros:after", // Only inspect expanded trees when generating unused symbol warnings
  "-Ywarn-unused-import",
  "-Ywarn-unused:_", // Enables all unused warnings
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  // "-opt:l:inline",
  // "-opt-inline-from:**",
  // "-opt-warnings",
)

javaOptions ++= Seq(
  "-XX:+CMSClassUnloadingEnabled", // Enable class unloading under the CMS GC
  // "-Xms2g",
  // "-Xmx5g",
  // "-XX:+UseParNewGC",
)

// Test suite settings
fork in Test := true
