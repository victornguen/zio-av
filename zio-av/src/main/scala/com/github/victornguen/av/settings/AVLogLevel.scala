package com.github.victornguen.av.settings

import enumeratum._

sealed class AVLogLevel(val value: Int) extends EnumEntry

object AVLogLevel extends Enum[AVLogLevel] {

  override def values: IndexedSeq[AVLogLevel] = findValues

  case object Quiet extends AVLogLevel(-8)

  case object Panic extends AVLogLevel(0)

  case object Fatal extends AVLogLevel(8)

  case object Error extends AVLogLevel(16)

  case object Warning extends AVLogLevel(24)

  case object Info extends AVLogLevel(32)

  case object Verbose extends AVLogLevel(40)

  implicit def toInt(level: AVLogLevel): Int = level.value

  implicit def intToLogLevel(i: Int): AVLogLevel = new AVLogLevel(i)

}
