package com.github.victornguen.av.models

import enumeratum._


sealed class FFMpegLogLevel(val value: Int) extends EnumEntry

object FFMpegLogLevel extends Enum[FFMpegLogLevel] {

  override def values: IndexedSeq[FFMpegLogLevel] = findValues

  case object Quiet extends FFMpegLogLevel(-8)

  case object Panic extends FFMpegLogLevel(0)

  case object Fatal extends FFMpegLogLevel(8)

  case object Error extends FFMpegLogLevel(16)

  case object Warning extends FFMpegLogLevel(24)

  case object Info extends FFMpegLogLevel(32)

  case object Verbose extends FFMpegLogLevel(40)

  implicit def toInt(level: FFMpegLogLevel): Int = level.value

}
