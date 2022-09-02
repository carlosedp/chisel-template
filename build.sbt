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
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

Global / semanticdbEnabled    := true
Global / semanticdbVersion    := scalafixSemanticdb.revision
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val toplevel = (project in file("."))
  .settings(
    name         := "project",
    version      := "0.0.1",
    scalaVersion := "2.13.8"
  )

// Default library versions
lazy val versions = new {
  val chisel3    = "3.5.4"
  val chiseltest = "0.5.4"
  val scalatest  = "3.2.13"
  val scalautils = "0.10.2"
  val oslib      = "0.8.1"
}

// Import libraries
libraryDependencies ++= Seq(
  "edu.berkeley.cs" %% "chisel3"    % versions.chisel3,
  "edu.berkeley.cs" %% "chiseltest" % versions.chiseltest % "test",
  "org.scalatest"   %% "scalatest"  % versions.scalatest  % "test",
  "com.carlosedp"   %% "scalautils" % versions.scalautils,
  "com.lihaoyi"     %% "os-lib"     % versions.oslib
)
addCompilerPlugin(("edu.berkeley.cs" % "chisel3-plugin" % versions.chisel3).cross(CrossVersion.full))

// Aliases
addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll;all Compile / scalafix; Test / scalafix")
addCommandAlias("fix", "all Compile / scalafixAll; Test / scalafixAll")
addCommandAlias("lint", "fmt;fix")
addCommandAlias("deps", "dependencyUpdates")

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-language:reflectiveCalls",
  "-feature",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-unused",
  "-P:chiselplugin:genBundleElements"
)
