import org.scoverage.coveralls.Imports.CoverallsKeys._
val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "kniffel",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % "test",
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "5.0.1",
      "org.scala-lang.modules" %% "scala-xml" % "2.0.0",
      "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
      "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % "test"
    ),

    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
      lazy val osName = System.getProperty("os.name") match {
        case name if name.startsWith("Linux")   => "linux"
        case name if name.startsWith("Mac")     => "mac"
        case name if name.startsWith("Windows") => "win"
        case _ => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
    }
  )