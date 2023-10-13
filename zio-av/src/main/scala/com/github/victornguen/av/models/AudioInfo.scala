package com.github.victornguen.av.models

import org.bytedeco.ffmpeg.avcodec.AVCodecParameters
import org.bytedeco.ffmpeg.avutil.{AVDictionary, AVDictionaryEntry}
import org.bytedeco.ffmpeg.global.avutil.av_dict_iterate
import org.bytedeco.javacv.FFmpegFrameGrabber
import zio.{Task, ZIO}

import scala.collection.mutable

case class AudioInfo private (
    format: String,
    duration: Long,
    sampleRate: Int,
    channels: Int,
    codec: String,
    bitrate: Option[Int],
    framerate: Option[Double],
    lengthInFrames: Int,
    private val parameters: AVCodecParameters,
    private val avMetadata: AVDictionary,
) {
  lazy val bitsPerSample: Int = parameters.bits_per_coded_sample()

  lazy val blockAlign: Int = parameters.block_align()

  lazy val bitsPerRawSample: Int = parameters.bits_per_raw_sample()

  lazy val metadata: Map[String, String] = {
    val map                      = mutable.Map.empty[String, String]
    var entry: AVDictionaryEntry = null
    while ({
      entry = av_dict_iterate(avMetadata, entry)
      entry != null
    }) {
      val key: String   = entry.key().getString
      val value: String = entry.value().getString
      map += key -> value
    }
    map.toMap
  }

  def codecId: Int = parameters.codec_id()

}

object AudioInfo {
  private[av] def make(grabber: FFmpegFrameGrabber): Task[AudioInfo] = ZIO.attempt {
    grabber.start()
    val formatContext   = grabber.getFormatContext
    val codecParameters = formatContext.streams(0).codecpar
    val metadata        = formatContext.metadata
    AudioInfo(
      format = grabber.getFormat,
      duration = grabber.getLengthInTime,
      sampleRate = grabber.getSampleRate,
      channels = grabber.getAudioChannels,
      codec = grabber.getAudioCodecName,
      bitrate = Option(grabber.getAudioBitrate),
      framerate = Option(grabber.getAudioFrameRate),
      lengthInFrames = grabber.getLengthInAudioFrames,
      parameters = codecParameters,
      avMetadata = metadata,
    )
  }
}
