addSbtPlugin("org.scalameta" % "sbt-scalafmt"  % "2.4.3")
addSbtPlugin("com.eed3si9n"  % "sbt-buildinfo" % "0.11.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"  % "0.11.1")
addSbtPlugin("org.bytedeco"  % "sbt-javacpp"   % "1.17")

// workaround for scala-xml dependency conflict
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

val ZioSbtVersion = "0.4.0-alpha.22"

addSbtPlugin("com.thoughtworks.sbt-api-mappings" % "sbt-api-mappings"  % "3.0.2")
addSbtPlugin("dev.zio"                           % "zio-sbt-ci"        % ZioSbtVersion)
addSbtPlugin("dev.zio"                           % "zio-sbt-ecosystem" % ZioSbtVersion)
addSbtPlugin("dev.zio"                           % "zio-sbt-website"   % ZioSbtVersion)

resolvers ++= Resolver.sonatypeOssRepos("public")
