import Dependencies._

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

ThisBuild / scalaVersion := "2.12.8"

lazy val root = (project in file("."))
  .settings(
    name := "tictactoe",
    libraryDependencies ++= Seq(
      zio,
      zioCats,
      cats,
      catsEffect,
      scalaTest % Test,
      scalaCheck % Test
    ),
    scalacOptions ++= ScalacOptions.options
  )
