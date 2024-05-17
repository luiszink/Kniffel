import org.scoverage.coveralls.Imports.CoverallsKeys._
val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "kniffel",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",
val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "kniffel",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",
    // Hier werden bestimmte Pakete und Dateien von der Testabdeckung ausgeschlossen
    scoverage.ScoverageKeys.coverageExcludedPackages := Seq("aview", "util"),
    scoverage.ScoverageKeys.coverageExcludedFiles := Seq("Kniffel.scala")
  )

    )
