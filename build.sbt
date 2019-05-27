import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"

lazy val root = (project in file("."))
  .settings(
    name := "tictactoe",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalaCheck % Test
    ),
    scalacOptions ++= ScalacOptions.options
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
