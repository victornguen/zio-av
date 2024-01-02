import LibsDsl.*
import sbt.*

import scala.languageFeature.postfixOps

object Dependencies {

  object V {
    val scala      = "2.13.11"
    val zio        = "2.0.20"
    val zioNio     = "2.0.2"
    val zioLogging = "2.1.13"
    val prelude    = "1.0.0-RC21"
    val enumeratum = "1.7.2"
    val javaCv     = "1.5.9"
    val tarsosDps  = "2.5"
  }

  object ZIO {
    lazy val core    = "dev.zio" %% "zio"         % V.zio
    lazy val nio     = "dev.zio" %% "zio-nio"     % V.zioNio
    lazy val streams = "dev.zio" %% "zio-streams" % V.zio
    lazy val logging = "dev.zio" %% "zio-logging" % V.zioLogging
    lazy val prelude = "dev.zio" %% "zio-prelude" % V.prelude
  }

  object TEST {
    val zioTest    = "dev.zio" %% "zio-test"     % V.zio % "test"
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % V.zio % "test"
  }

  object AUDIO {
    val javaCv    = "org.bytedeco" % "javacv-platform" % V.javaCv
    val tarsosDsp = javaLib("be.tarsos.dsp", V.tarsosDps)("core", "jvm")
  }

  object Utils {
    lazy val enumeratum = "com.beachape" %% "enumeratum" % V.enumeratum
  }

  object Compiler {
    lazy val betterMonadicFor = compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  }

  def global(scalaVersion: String): Seq[ModuleID] =
    Lib(
      ZIO.core,
      ZIO.nio,
      ZIO.streams,
      ZIO.logging,
      ZIO.prelude,
      TEST.zioTest,
      TEST.zioTestSbt,
      AUDIO.javaCv,
      AUDIO.tarsosDsp,
      Utils.enumeratum,
      CrossVersion.partialVersion(scalaVersion) match {
        case Some((2, _)) => Seq(Compiler.betterMonadicFor)
        case _            => List.empty
      },
    ).modules

}
