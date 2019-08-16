name := "Damysos"

version := "2"

scalaVersion := "2.13.0"

// SBT scoverage settings
coverageMinimum := 95
coverageFailOnMinimum := true

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0-M4",
  "org.scalatest" %% "scalatest" % "3.0.8",
)

scalacOptions ++= Seq(
  "-deprecation", // Warn about deprecated features
  "-encoding", "UTF-8", // Specify character encoding used by source files
  "-feature", // Emit warning and location for usages of features that should be imported explicitly
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds", // Allow higher-kinded types
  "-unchecked", // Enable additional warnings where generated code depends on assumptions
  // "-Xfatal-warnings", // Fail on warnings
  "-Xlint:_", // Enable all available style warnings
  "-Ywarn-macros:after", // Only inspect expanded trees when generating unused symbol warnings
  "-Ywarn-unused:_", // Enables all unused warnings
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  // "-opt:l:inline",
  // "-opt-inline-from:**",
  // "-opt-warnings",
)

javaOptions ++= Seq(
  "-XX:+CMSClassUnloadingEnabled", // Enable class unloading under the CMS GC
  "-Xms10g",
  "-Xmx14g",
  // "-XX:+UseParNewGC",
)

// Test suite settings
fork in Test := true
