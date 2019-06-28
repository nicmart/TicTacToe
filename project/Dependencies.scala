import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0"
  lazy val zio = "org.scalaz" %% "scalaz-zio" % "1.0-RC5"
  lazy val zioCats = "org.scalaz" %% "scalaz-zio-interop-cats" % "1.0-RC5"
  lazy val cats = "org.typelevel" %% "cats-core" % "2.0.0-M1"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "1.3.1"
}
