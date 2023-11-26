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
    ciEnabledBranches        := Seq("master"),
    developers := List(
      Developer("vnguen", "Victor Nguen", "vnguen@beeline.ru", url("https://github.com/victornguen")),
    ),
    startYear := Some(2023),
    resolvers ++= List(
      "jitpack.io" at "https://jitpack.io",
      "be.0110.repo-releases" at "https://mvn.0110.be/releases",
    ),
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
      fork := true,
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

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
