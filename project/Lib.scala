import sbt.ModuleID

sealed trait Lib {
  def modules: Seq[ModuleID]
}

object Lib {
  final case class Libs(libs: Lib*) extends Lib {
    def modules: Seq[ModuleID] = libs.flatMap {
      case Single(moduleID) => Seq(moduleID)
      case Set(modules)     => modules
      case Libs(libs @ _*)  => libs.flatMap(_.modules)
    }
  }
  final case class Single(moduleID: ModuleID) extends Lib {
    override def modules: Seq[ModuleID] = Seq(moduleID)
  }

  final case class Set(modules: Seq[ModuleID]) extends Lib

  def apply(libs: Lib*): Libs = Libs(libs *)

  implicit def moduleIdToLib(moduleID: ModuleID): Lib = Lib.Single(moduleID)

  implicit def moduleSeqToSet(modules: Seq[ModuleID]): Lib = Lib.Set(modules)
}
