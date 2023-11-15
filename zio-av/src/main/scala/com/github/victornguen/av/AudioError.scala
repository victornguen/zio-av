package com.github.victornguen.av

sealed abstract class AudioError(message: String) extends Exception(message)

object AudioError {

  final case class UnsupportedCodecError(message: String) extends AudioError(message) {
    override def toString: String = s"UnsupportedCodecError: $message"
  }

}
