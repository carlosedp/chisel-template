ThisBuild / organization := "$org$"
ThisBuild / description  := "$name$"
ThisBuild / homepage     := Some(url("$url$"))
ThisBuild / licenses     := Seq("MIT" -> url("https://opensource.org/licenses/BSD-3-Clause"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("$url$"), "git@github.com:myorg/sample.git")
)
ThisBuild / developers := List(
  Developer("$username$", "My Name", "me@email", url("https://github.com/$username$"))
)
Global / semanticdbEnabled    := true
Global / semanticdbVersion    := "4.4.28" //scalafixSemanticdb.revision // Force version due to compatibility issues
Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

Test / logBuffered := false

lazy val toplevel = (project in file("."))
  .settings(
    name         := "$project_name$",
    version      := "0.0.1",
    scalaVersion := "2.13.6"
  )

// Default library versions
val defaultVersions = Map(
  "chisel3" -> "3.5.0-RC1",
  // "chisel3"    -> "3.5-SNAPSHOT", // Opt for Chisel3 snapshots
  "chiseltest" -> "0.5-SNAPSHOT",
  "scalatest"  -> "3.2.10",
  "scalautils" -> "0.7.+"
)

// Import libraries
libraryDependencies ++= Seq(
  "edu.berkeley.cs" %% "chisel3"    % defaultVersions("chisel3"),
  "edu.berkeley.cs" %% "chiseltest" % defaultVersions("chiseltest") % "test",
  "org.scalatest"   %% "scalatest"  % defaultVersions("scalatest")  % "test",
  "com.carlosedp"   %% "scalautils" % defaultVersions("scalautils"),
  "com.lihaoyi"     %% "os-lib"     % "0.7.8",
  "edu.berkeley.cs" %% "firrtl"     % "1.5-SNAPSHOT" // Force using SNAPSHOT until next RC is cut (memory synth)
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
  Resolver.sonatypeRepo("releases")
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
