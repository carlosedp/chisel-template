import mill._, mill.scalalib._
import mill.scalalib.TestModule.ScalaTest
import scalafmt._
import coursier.maven.MavenRepository
import $ivy.`com.goyeau::mill-scalafix::0.2.10`
import com.goyeau.mill.scalafix.ScalafixModule

// Define the library versions
object versions {
  val scala           = "2.13.8"
  val chisel3         = "3.5.4"
  val chiseltest      = "0.5.4"
  val scalatest       = "3.2.13"
  val organizeimports = "0.6.0"
  val semanticdb      = "4.5.13"
}

trait BaseProject extends ScalaModule with ScalafmtModule with ScalafixModule {
  def scalaVersion = versions.scala
  def repositoriesTask = T.task { // Add snapshot repositories in case needed
    super.repositoriesTask() ++ Seq("oss", "s01.oss")
      .map(r => s"https://$r.sonatype.org/content/repositories/snapshots")
      .map(MavenRepository(_))
  }
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${versions.organizeimports}")

  // Add the project module dependencies
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"edu.berkeley.cs::chisel3:${versions.chisel3}",
  )
  // Define the project plugin dependencies
  override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(
    ivy"org.scalameta:::semanticdb-scalac:${versions.semanticdb}",
    ivy"edu.berkeley.cs:::chisel3-plugin:${versions.chisel3}",
  )
  object test extends Tests with TestModule.ScalaTest {
    // Define the project test dependencies
    def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"org.scalatest::scalatest:${versions.scalatest}",
      ivy"edu.berkeley.cs::chiseltest:${versions.chiseltest}",
    )
  }
}

trait ScalacOptions extends ScalaModule {
  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-unchecked",
      "-deprecation",
      "-language:reflectiveCalls",
      "-feature",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Ywarn-dead-code",
      "-Ywarn-unused",
      "-P:chiselplugin:genBundleElements",
    )
  }
}

// Final object definition name matches the sources/tests directory
object toplevel extends ScalaModule with BaseProject with ScalacOptions {
  // This is the name of your main class (instantiated as `object x extends App`)
  def mainClass = Some("Toplevel")
  // Matches your project directory where sources and tests are placed
  def projectName = "toplevel"
}

// Toplevel commands
def lint(ev: eval.Evaluator) = T.command {
  mill.main.MainModule.evaluateTasks(
    ev,
    Seq("__.fix", "+", "mill.scalalib.scalafmt.ScalafmtModule/reformatAll", "__.sources"),
    mill.define.SelectMode.Separated,
  )(identity)
}
def deps(ev: eval.Evaluator) = T.command {
  mill.scalalib.Dependency.showUpdates(ev)
}
