import Dependencies._

resolvers += Resolver.sonatypeRepo("releases")

ThisBuild / scalaVersion := "2.12.8"

lazy val root = (project in file("."))
  .settings(
    name := "tictactoe",
    libraryDependencies ++= Seq(
      zio,
      cats,
      catsEffect,
      scalaTest % Test,
      scalaCheck % Test
    ),
    scalacOptions ++= ScalacOptions.options
  )
