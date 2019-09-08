import sbt._

object Dependencies {

  val catsVersion = "2.0.0-RC1"
  val mtlVersion = "0.6.0"
  val taglessVersion = "0.9"
  val newtypeVersion = "0.4.3"
  val finagleVersion = "19.8.0"
  val finchVersion = "0.22.0"
  val circeVersion = "0.11.1"
  val scalacheckVersion = "1.14.0"
  val scalatestVersion = "3.0.8"
  val scalaUriVersion = "1.5.1"
  val doobieVersion = "0.7.0"
  val pureconfigVersion = "0.11.1"
  val kindProjectorVersion = "0.10.3"

  val compilerPlugins = Seq(
    compilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion)
  )

  lazy val cats = "org.typelevel" %% "cats-core" % catsVersion
  lazy val mtl = "org.typelevel" %% "cats-mtl-core" % mtlVersion
  lazy val tagless = "org.typelevel" %% "cats-tagless-macros" % taglessVersion
  lazy val newtype = "io.estatico" %% "newtype" % newtypeVersion

  lazy val finch = Seq(
    "com.github.finagle" %% "finch-core" % finchVersion,
    "com.github.finagle" %% "finch-circe" % finchVersion,
    "com.github.finagle" %% "finch-test" % finchVersion
  )

  lazy val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  lazy val scalaUri = "io.lemonlabs" %% "scala-uri" % scalaUriVersion

  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-hikari" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion,
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "it,test"
  )

  val pureconfig = "com.github.pureconfig" %% "pureconfig" % pureconfigVersion

  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalatestVersion % "it,test"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion % "it,test"

  lazy val allDependencies = Seq(
    cats,
    mtl,
    tagless,
    newtype,
    scalaUri,
    pureconfig,
    scalaTest,
    scalaCheck
  ) ++ finch ++ circe ++ doobie ++ compilerPlugins
}
