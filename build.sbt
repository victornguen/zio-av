ThisBuild / scalaVersion := "2.13.12"
ThisBuild / organization := "io.github.victornguen"

enablePlugins(ZioSbtEcosystemPlugin, ZioSbtCiPlugin)

inThisBuild(
  List(
    name               := "ZIO AV",
    crossScalaVersions := Seq(scala213.value, scala3.value),
    ciEnabledBranches  := Seq("master"),
    developers := List(
      Developer("vnguen", "Victor Nguen", "vnguen@beeline.ru", url("https://github.com/victornguen")),
    ),
    startYear := Some(2023),
    resolvers ++= List(
      "jitpack.io" at "https://jitpack.io",
      "be.0110.repo-releases" at "https://mvn.0110.be/releases",
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/victornguen/zio-av"),
        "https://github.com/victornguen/zio-av.git",
      ),
    ),
    semanticdbEnabled      := true,
    semanticdbVersion      := scalafixSemanticdb.revision,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
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
    .aggregate(zioAv, examples)

lazy val zioAv = {
  project
    .in(file("zio-av"))
    .settings(enableZIO(enableStreaming = true))
    .settings(BuildHelper.stdSetting("zio-av", "av"))
    .settings()
    .settings(
      javaCppPresetLibs ++= Seq("ffmpeg" -> "4.3.1"),
      fork := true,
    )
    .settings(libraryDependencies ++= Dependencies.global(scalaVersion.value))
}

lazy val examples =
  project
    .in(file("zio-av-examples"))
    .settings(enableZIO(enableStreaming = true))
    .settings(BuildHelper.stdSetting("zio-av-examples", "av.examples"))
    .settings(publish / skip := true)
    .settings(libraryDependencies ++= Dependencies.global(scalaVersion.value))
    .disablePlugins(ScalafixPlugin)
    .dependsOn(zioAv)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("checkFmt", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

lazy val docs = project
  .in(file("zio-av-docs"))
  .settings(
    moduleName := "zio-av-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    projectName                                := "ZIO AV",
    mainModuleName                             := (zioAv / moduleName).value,
    projectStage                               := ProjectStage.Development,
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(zioAv),
    headerLicense                              := None,
    sonatypeCredentialHost                     := "s01.oss.sonatype.org",
  )
  .enablePlugins(WebsitePlugin)
  .dependsOn(zioAv)
