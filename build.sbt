import Dependencies.allDependencies
import BuildOptions._
import java.io.File
import scala.sys.process.Process

ThisBuild / scalaVersion := "2.12.9"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "coding-challenge-yuriy-ankudinov"
ThisBuild / organizationName := "coding-challenge-yuriy-ankudinov"

enablePlugins(FlywayPlugin)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

val host = Option(System.getenv("POSTGRES_DB_HOST")).getOrElse("localhost")
val port = Option(System.getenv("POSTGRES_DB_PORT")).getOrElse("8102")
val dbName = Option(System.getenv("POSTGRES_DB_NAME")).getOrElse("postgres")

val dockerLocalDependencies = settingKey[File]("Local docker-compose dependencies file.")
val localDependenciesUp = taskKey[Unit]("Spins up the local docker dependencies for this project.")
val localDependenciesDown = taskKey[Unit]("Tears down the local docker dependencies for this project.")

lazy val root = (project in file("."))
  .settings(
    name := "coding-challenge-yuriy-ankudinov",
    scalacOptions ++= compilerOptions,
    libraryDependencies ++= allDependencies,
    flywayUrl := s"jdbc:postgresql://$host:$port/$dbName",
    flywayPassword := Option(System.getenv("POSTGRES_DB_ADMIN_USER_PASS")).getOrElse(""),
    flywayUser := Option(System.getenv("POSTGRES_DB_ADMIN_USER")).getOrElse("postgres"),
    flywayLocations := Seq("filesystem:src/main/resources/migrations/"),
    flywayPlaceholders := Map(
      "DB_API_USER_PASS" -> Option(System.getenv("DB_API_USER_PASS")).getOrElse("")
    ),
    flywayBaselineVersion := "-1",
    dockerLocalDependencies := baseDirectory.value / "docker-compose-local-dependencies.yml",
    localDependenciesUp := {
      val log = streams.value.log
      val cwd = baseDirectory.value
      val dockerFile = dockerLocalDependencies.value.getAbsolutePath
      val down = Process(Seq("docker-compose", "-f", dockerFile, "down"), cwd)
      val build = Process(Seq("docker-compose", "-f", dockerFile, "build", "--no-cache"), cwd)
      val up = Process(Seq("docker-compose", "-f", dockerFile, "up", "--force-recreate", "-d"), cwd)
      (down #&& build #&& up) ! log
    },
    localDependenciesDown := {
      val log = streams.value.log
      val cwd = baseDirectory.value
      val dockerFile = dockerLocalDependencies.value.getAbsolutePath
      Process(Seq("docker-compose", "-f", dockerFile, "down"), cwd) ! log
    },
    Defaults.itSettings,
    fork in Test := true,
    fork in IntegrationTest := true
  ).configs(IntegrationTest)
