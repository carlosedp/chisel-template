ThisBuild / organization := "myorganization"
ThisBuild / description  := "Chisel3 Template Project"
ThisBuild / homepage     := Some(url("https://project.url"))
ThisBuild / licenses     := Seq("MIT" -> url("https://opensource.org/licenses/BSD-3-Clause"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/myuser/proj"), "git@github.com:myorg/sample.git")
)
ThisBuild / developers := List(
  Developer("myuser", "My Name", "me@email", url("https://github.com/myuser"))
)
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % defaultVersions("organize-imports")

Global / semanticdbEnabled    := true
Global / semanticdbVersion    := defaultVersions("semanticdb")
Global / onChangedBuildSource := ReloadOnSourceChanges

Test / logBuffered := false

lazy val toplevel = (project in file("."))
  .settings(
    name         := "project",
    version      := "0.0.1",
    scalaVersion := defaultVersions("scala")
  )

// Default library versions
val defaultVersions = Map(
  "scala"            -> "2.13.6",
  "chisel3"          -> "3.5.0-RC1",
  "chiseltest"       -> "0.5-SNAPSHOT",
  "scalatest"        -> "3.2.10",
  "scalautils"       -> "0.7.2",
  "semanticdb"       -> "4.4.30",
  "organize-imports" -> "0.6.0",
  "os-lib"           -> "0.7.8"
)

// Import libraries
libraryDependencies ++= Seq(
  "edu.berkeley.cs" %% "chisel3"    % defaultVersions("chisel3"),
  "edu.berkeley.cs" %% "chiseltest" % defaultVersions("chiseltest") % "test",
  "org.scalatest"   %% "scalatest"  % defaultVersions("scalatest")  % "test",
  "com.carlosedp"   %% "scalautils" % defaultVersions("scalautils"),
  "com.lihaoyi"     %% "os-lib"     % defaultVersions("os-lib")
)
addCompilerPlugin(("edu.berkeley.cs" % "chisel3-plugin" % defaultVersions("chisel3")).cross(CrossVersion.full))

// Aliases
addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll;all Compile / scalafix; Test / scalafix")
addCommandAlias("fix", "all Compile / scalafixAll; Test / scalafixAll")
addCommandAlias("lint", "fmt;fix")
addCommandAlias("deps", "dependencyUpdates")

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  "Sonatype New OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-language:reflectiveCalls",
  "-feature",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)
