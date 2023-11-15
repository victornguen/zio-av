package com.github.victornguen.av.settings

sealed trait ProcessingMode

object ProcessingMode {
  final case object InMemory extends ProcessingMode

  final case object File extends ProcessingMode
}
