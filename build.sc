import mill._, mill.scalalib._
import scalafmt._
import coursier.maven.MavenRepository
import $ivy.`com.goyeau::mill-scalafix::0.2.10`
import com.goyeau.mill.scalafix.ScalafixModule

object versions {
  val chisel3         = "3.5.4"
  val chiseltest      = "0.5.4"
  val scalatest       = "3.2.13"
  val organizeimports = "0.6.0"
  val scalautils      = "0.10.2"
  val oslib           = "0.8.1"
  val semanticdb      = "4.5.13"
}

trait BaseProject extends CrossSbtModule {
  def crossScalaVersion       = "2.13.8"
  def mainClass               = Some("Toplevel")
  override def millSourcePath = super.millSourcePath
  def repositoriesTask = T.task {
    super.repositoriesTask() ++ Seq("oss", "s01.oss")
      .map("https://" + _ + ".sonatype.org/content/repositories/snapshots")
      .map(MavenRepository(_))
  }
}

trait HasChisel3 extends CrossSbtModule {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"edu.berkeley.cs::chisel3:${versions.chisel3}"
  )
  override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(
    ivy"edu.berkeley.cs:::chisel3-plugin:${versions.chisel3}"
  )
  object test extends Tests {
    def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"org.scalatest::scalatest:${versions.scalatest}",
      ivy"edu.berkeley.cs::chiseltest:${versions.chiseltest}"
    )
    def testFramework = "org.scalatest.tools.Framework"
    def testOne(args: String*) = T.command {
      super.runMain("org.scalatest.run", args: _*)
    }
  }
}

trait CodeQuality extends ScalafmtModule with ScalafixModule {
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${versions.organizeimports}")
  override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(
    ivy"org.scalameta:::semanticdb-scalac:${versions.semanticdb}"
  )
}

trait ScalacOptions extends CrossSbtModule {
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
      "-P:chiselplugin:genBundleElements"
    )
  }
}

// Toplevel commands
def lint(ev: eval.Evaluator) = T.command {
  mill.main.MainModule.evaluateTasks(
    ev,
    Seq("__.fix", "+", "mill.scalalib.scalafmt.ScalafmtModule/reformatAll", "__.sources"),
    mill.define.SelectMode.Separated
  )(identity)
}
def deps(ev: eval.Evaluator) = T.command {
  mill.scalalib.Dependency.showUpdates(ev)
}

// Final object definition
object toplevel extends CrossSbtModule with BaseProject with HasChisel3 with CodeQuality with ScalacOptions {}
