import sbt.*

object LibsDsl {

  case class LibSet[T](f: String => T) {
    def apply(xs: String*): Seq[T] = xs map f
  }

  private val scalaLibraryExclusion = LibSet(ExclusionRule("org.scala-lang", _))("scala-library", "scala-reflect")

  def scalaLib(
      n: String,
      v: String,
      modifyModule: ModuleID => ModuleID = identity,
  ) = {
    val createModule: String => ModuleID = n %% _ % v excludeAll (scalaLibraryExclusion: _*)
    LibSet(createModule andThen modifyModule)
  }
}
