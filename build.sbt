val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "kniffel",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",
    coverageExcludedPackages := "<empty>;*aview*;*util*"
  )