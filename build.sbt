val projVersion = "0.0.1"
val projName    = "Triton Client"

ThisBuild / scalaVersion := "2.13.11"
ThisBuild / version      := "0.0.1"
ThisBuild / organization := "com.github.victornguen"

enablePlugins(ZioSbtEcosystemPlugin)

inThisBuild(
  List(
    name               := projName,
    crossScalaVersions := Seq(scala213.value),
    developers := List(
      Developer("vnguen", "Victor Nguen", "vnguen@beeline.ru", url("https://github.com/victornguen")),
    ),
    startYear := Some(2023),
    resolvers += "jitpack.io" at "https://jitpack.io",
  ),
)

lazy val root: Project =
  project
    .in(file("."))
    .settings(
      publish / skip     := true,
      crossScalaVersions := Nil,
      Compile / console / scalacOptions --= Seq("-Xlint"),
    )
    .aggregate(av)

lazy val av = {
  project
    .in(file("zio-av"))
    .settings(enableZIO(enableStreaming = true))
    .settings(BuildHelper.stdSetting("zio-av", "av"))
    .settings(
      Compile / scalacOptions ++= Settings.compilerOptions,
      javaCppPresetLibs ++= Seq("ffmpeg" -> "4.3.1"),
    )
    .settings(libraryDependencies ++= Dependencies.global)
}

lazy val examples =
  project
    .in(file("zio-av-examples"))
    .settings(enableZIO(enableStreaming = true))
    .settings(BuildHelper.stdSetting("zio-av-examples", "av.examples"))
    .settings(publish / skip := true)
    .settings(libraryDependencies ++= Dependencies.global)
    .dependsOn(av)
