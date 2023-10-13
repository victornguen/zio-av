import LibsDsl.scalaLib
import sbt.*

import scala.languageFeature.postfixOps

object Dependencies {

  object V {
    val scala          = "2.13.11"
    val derevo         = "0.13.0"
    val chimney        = "0.6.1"
    val zio            = "2.0.16"
    val zioJson        = "0.6.0"
    val zioNio         = "2.0.1"
    val logback        = "1.4.7"
    val scalaLogging   = "3.9.5"
    val pureConfig     = "0.17.4"
    val zioLogging     = "2.1.13"
    val scalatest      = "3.2.15"
    val silencer       = "1.7.13"
    val enumeratum     = "1.7.2"
    val ulid           = "5.2.0"
    val testcontainers = "0.40.12"
    val zioSchema      = "0.4.12"
    val catsCore       = "2.10.0"
    val catsEffect     = "3.5.1"
    val ffmpeg4j       = "5.1.2-1.5.8-4"
    val jave           = "3.3.1"
    val javaCv         = "1.5.9"
  }

  object ZIO {
    lazy val core    = "dev.zio" %% "zio"         % V.zio
    lazy val nio     = "dev.zio" %% "zio-nio"     % V.zioNio
    lazy val macros  = "dev.zio" %% "zio-macros"  % V.zio
    lazy val streams = "dev.zio" %% "zio-streams" % V.zio
    lazy val zioJson = "dev.zio" %% "zio-json"    % V.zioJson
  }

  object CATS {
    lazy val catsCore = "org.typelevel" %% "cats-core"   % V.catsCore
    lazy val effect   = "org.typelevel" %% "cats-effect" % V.catsEffect withSources () withJavadoc ()
  }

  object LOGS {
    lazy val core       = "ch.qos.logback" % "logback-classic" % V.logback
    lazy val zioLogging = "dev.zio"       %% "zio-logging"     % V.zioLogging
  }

  object TEST {
    val zioTest    = "dev.zio"       %% "zio-test"     % V.zio       % "test"
    val zioTestSbt = "dev.zio"       %% "zio-test-sbt" % V.zio       % "test"
    def scalatest  = "org.scalatest" %% "scalatest"    % V.scalatest % Test
  }

  object AUDIO {
    val ffmpeg4j = "com.github.manevolent" % "ffmpeg4j"      % V.ffmpeg4j
    val jave2    = "ws.schild"             % "jave-all-deps" % V.jave
    val javaCv   = "org.bytedeco"          % "javacv"        % V.javaCv
  }

  def silencer = Seq(
    compilerPlugin("com.github.ghik" % "silencer-plugin" % V.silencer cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % V.silencer % Provided cross CrossVersion.full,
  )

  def ulid = "com.github.f4b6a3" % "ulid-creator" % V.ulid

  def enumeratum = "com.beachape" %% "enumeratum" % V.enumeratum

  def zioSchema = scalaLib("dev.zio", V.zioSchema)(
    "zio-schema",
    "zio-schema-protobuf",
    "zio-schema-json",
    "zio-schema-derivation",
  )

  def betterMonadicFor = compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

  def kindProjector = compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)

  def all =
    Seq(
      zioSchema,
      silencer,
    ).flatten

  lazy val global = all ++ Seq(
    ZIO.core,
    ZIO.nio,
    ZIO.macros,
    ZIO.streams,
    ZIO.zioJson,
    CATS.catsCore,
    CATS.effect,
    LOGS.core,
    LOGS.zioLogging,
    TEST.zioTest,
    TEST.zioTestSbt,
    TEST.scalatest,
    AUDIO.ffmpeg4j,
    AUDIO.jave2,
    AUDIO.javaCv,
    ulid,
    enumeratum,
    kindProjector,
    betterMonadicFor,
  )

}
