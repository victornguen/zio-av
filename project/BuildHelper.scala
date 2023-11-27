import org.scalafmt.sbt.ScalafmtPlugin.autoImport.{scalafmtCheckAll, scalafmtSbtCheck}
import sbt.*
import sbt.Keys.*
import sbtbuildinfo.BuildInfoKeys.*
import scalafix.sbt.ScalafixPlugin.autoImport.*
import zio.sbt.ZioSbtEcosystemPlugin.autoImport.optionsOn

object BuildHelper {

  private def compileOnlyDeps(scalaVersion: String): Seq[ModuleID] =
    (
      CrossVersion.partialVersion(scalaVersion) match {
        case Some((2, x)) =>
          Seq(
            compilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.2").cross(CrossVersion.full)),
          )
        case _ => Seq.empty
      }
    ) ++ (
      CrossVersion.partialVersion(scalaVersion) match {
        case Some((2, x)) if x <= 12 =>
          Seq(
            compilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full)),
          )
        case _ => Seq.empty
      }
    )

  def stdSetting(projectName: String, packageName: String) =
    Seq(
      name                                   := projectName,
      buildInfoPackage                       := packageName,
      ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value),
      ThisBuild / scalafixDependencies ++= List(
        "com.github.liancheng" %% "organize-imports" % "0.6.0",
        "com.github.vovapolu"  %% "scaluzzi"         % "0.1.23",
      ),
      libraryDependencies ++= compileOnlyDeps(scalaVersion.value),
      Test / parallelExecution := !sys.env.contains("CI"),
      incOptions ~= (_.withLogRecompileOnMacro(true)),
      autoAPIMappings := true,
      testFrameworks  := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),

      // scalafmt settings
      Compile / compile := (Compile / compile)
        .dependsOn(
          Compile / scalafmtCheckAll,
          Compile / scalafmtSbtCheck,
        )
        .value,
      scalacOptions ++=
        optionsOn("2.13")(Settings.compilerOptions *).value,
    )
}
