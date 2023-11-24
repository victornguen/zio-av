package com.github.victornguen.av.settings

import enumeratum._

sealed class AudioFormat extends EnumEntry

object AudioFormat extends Enum[AudioFormat] {

  override def values: IndexedSeq[AudioFormat] = findValues

  case object MPEG    extends AudioFormat
  case object MP3     extends AudioFormat
  case object WAV     extends AudioFormat
  case object OGG     extends AudioFormat
  case object OPUS    extends AudioFormat
  case object FLAC    extends AudioFormat
  case object AMR     extends AudioFormat
  case object AAC     extends AudioFormat
  case object AC3     extends AudioFormat
  case object ATRAC   extends AudioFormat
  case object WMA     extends AudioFormat
  case object CELT    extends AudioFormat
  case object LDAC    extends AudioFormat
  case object AptX    extends AudioFormat
  case object DTS     extends AudioFormat
  case object MQA     extends AudioFormat
  case object MP2     extends AudioFormat
  case object MPC     extends AudioFormat
  case object ALAC    extends AudioFormat
  case object WavPack extends AudioFormat
  case object LDCT    extends AudioFormat

  implicit def toString(audioFormat: AudioFormat): String = audioFormat.entryName

}
