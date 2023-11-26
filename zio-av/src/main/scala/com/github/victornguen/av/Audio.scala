package com.github.victornguen.av

import com.github.victornguen.av.Audio._
import com.github.victornguen.av.info.{AudioInfo, TimeInterval}
import com.github.victornguen.av.internal.PitchDetection
import com.github.victornguen.av.settings.AudioCodec.CodecId
import com.github.victornguen.av.settings.{AudioCodec, AudioFormat, PitchEstimationAlgorithm}
import com.github.victornguen.av.storage.TempFileStorage
import org.bytedeco.javacv.{FFmpegFrameGrabber, FFmpegFrameRecorder, Frame}
import zio.nio.file.Path
import zio.stream.ZStream
import zio.{Promise, RIO, Scope, Task, UIO, URIO, ZIO}

import java.io._
import scala.jdk.CollectionConverters.MapHasAsJava

final case class Audio[-R, +E <: Throwable] private (
    stream: ZStream[R, E, Byte],
    private val filePromise: Promise[Nothing, Path],
    private val infoPromise: Promise[Nothing, AudioInfo],
) extends Multimedia[R, E, AudioInfo](stream, filePromise, infoPromise) { self =>

  /** extract a segment of audio from the original audio file. Processing without creating temp file.
    *
    * @param from
    *   The starting timestamp of the segment to be extracted from the original audio file. In seconds.
    * @param to
    *   The ending timestamp of the segment to be extracted from the original audio file. In seconds.
    * @param withMeta
    *   if true will include metadata of original audio to cropped one.
    */
  def cropStream(
      from: Double,
      to: Double,
      withMeta: Boolean = true,
  ): RIO[R with Scope, ZAudio] = {
    val buffer = new ByteArrayOutputStream()
    for {
      info     <- getInfo
      recorder <- bufferFrameRecorder(info.channels, buffer)
      grabber  <- frameGrabber
      _        <- recordSegment(grabber, recorder, from, to, info, withMeta)
      stream = ZStream.fromIterator(buffer.toByteArray.iterator)
      croppedAudio <- Audio.fromStream(stream)
    } yield croppedAudio
  }

  /** extract a segment of audio from the original audio file. Processing with creating temp file.
    * @param from
    *   The starting timestamp of the segment to be extracted from the original audio file. In seconds.
    * @param to
    *   The ending timestamp of the segment to be extracted from the original audio file. In seconds.
    * @param withMeta
    *   if true will include metadata of original audio to cropped one.
    */
  def crop(
      from: Double,
      to: Double,
      withMeta: Boolean = true,
  ): RIO[R with Scope with TempFileStorage, ZAudio] =
    for {
      info         <- getInfo
      file         <- TempFileStorage.createTempFileScoped().map(_.toFile)
      grabber      <- frameGrabber
      recorder     <- fileFrameRecorder(info.channels, file)
      _            <- recordSegment(grabber, recorder, from, to, info, withMeta)
      croppedAudio <- Audio.fromFile(file)
      _            <- croppedAudio.setFile(file)
    } yield croppedAudio

  /** Transcode the audio file to a different format using specified parameters
    * @param audioCodec
    *   Target audio codec to transcode the audio file to.
    * @param audioFormat
    *   Target audio format to transcode the audio file to.
    * @param sampleRate
    *   Optional parameter to specify the sample rate of the transcoded audio. If not provided, the sample rate of the original audio will
    *   be used.
    * @param audioChannels
    *   Number of audio channels in the transcoded audio. If not provided, the number of channels in the original audio will be used.
    * @param bitRate
    *   Bit rate of the transcoded audio. If not provided, the bit rate of the original audio will be used.
    * @param copyMeta
    *   Boolean flag to indicate whether to copy the metadata from the original audio to the transcoded audio. Default is true
    * @return
    *   ZIO with ZAudio object, representing the transcoded audio.
    */
  def transcode(
      audioCodec: AudioCodec,
      audioFormat: AudioFormat,
      sampleRate: Option[Int] = None,
      audioChannels: Option[Int] = None,
      bitRate: Option[Int] = None,
      copyMeta: Boolean = true,
  ): RIO[R with Scope with TempFileStorage, ZAudio] = {
    for {
      info     <- getInfo
      file     <- TempFileStorage.createTempFileScoped().map(_.toFile)
      grabber  <- frameGrabber
      recorder <- fileFrameRecorder(info.channels, file)
      _ <- recordSamples(
        grabber,
        recorder,
        audioCodec,
        audioFormat,
        sampleRate,
        Some(audioChannels.getOrElse(info.channels)),
        bitRate,
        if (copyMeta) info.metadata else Map.empty,
        recordFunc = (g, r) => {
          var samples: Frame = g.grabSamples()
          while (samples != null) {
            r.record(samples)
            samples = g.grabSamples()
          }
        },
      )
      transcodedAudio <- Audio.fromFile(file)
      _               <- transcodedAudio.setFile(file)
    } yield transcodedAudio
  }

  private def recordSegment(
      grabber: FFmpegFrameGrabber,
      recorder: FFmpegFrameRecorder,
      from: Double,
      to: Double,
      info: AudioInfo,
      copyMeta: Boolean,
      additionalMeta: Map[String, String] = Map.empty,
  ): Task[Unit] = {
    val start = secondsToMicros(from)
    val end   = secondsToMicros(to)
    recordSamples(
      grabber,
      recorder,
      info.codecId,
      info.format,
      Some(info.sampleRate),
      Some(info.channels),
      info.bitrate,
      if (copyMeta) info.metadata ++ additionalMeta else if (additionalMeta.nonEmpty) additionalMeta else Map.empty,
      _.setAudioTimestamp(start),
      (g, r) =>
        while (g.getTimestamp <= end) {
          r.record(grabber.grabSamples())
        },
    )
  }

  private def recordSamples[T](
      grabber: FFmpegFrameGrabber,
      recorder: FFmpegFrameRecorder,
      audioCodec: CodecId,
      audioFormat: String,
      sampleRate: Option[Int],
      audioChannels: Option[Int],
      bitrate: Option[Int],
      metadata: Map[String, String],
      grabberAction: FFmpegFrameGrabber => Unit = _ => (),
      recordFunc: (FFmpegFrameGrabber, FFmpegFrameRecorder) => T,
  ): Task[T] = ZIO.attempt {
    grabber.start()
    grabberAction(grabber)
    recorder.setAudioCodec(audioCodec)
    recorder.setFormat(audioFormat)
    sampleRate.foreach(recorder.setSampleRate)
    audioChannels.foreach(recorder.setAudioChannels)
    bitrate.foreach(recorder.setAudioBitrate)
    if (metadata.nonEmpty) recorder.setMetadata(metadata.asJava)
    recorder.start()
    recordFunc(grabber, recorder)
  }

  private def bufferFrameRecorder(channels: Int, buffer: OutputStream): URIO[Scope, FFmpegFrameRecorder] =
    useFrameRecorder(new FFmpegFrameRecorder(buffer, channels))

  private def fileFrameRecorder(channel: Int, file: File): URIO[Scope, FFmpegFrameRecorder] =
    useFrameRecorder(new FFmpegFrameRecorder(file, channel))

  private def useFrameRecorder(recorder: FFmpegFrameRecorder): URIO[Scope, FFmpegFrameRecorder] =
    ZIO.acquireRelease {
      ZIO.succeed(recorder)
    } { recorder =>
      ZIO.attempt {
        recorder.stop()
        recorder.release()
      }.orDie
    }

  /** Recognize speech intervals in audio. Processing without creating temp file.
    * @param intervalsThreshold
    *   time interval threshold, signals with a time difference greater than this value will be considered different phrases
    * @param probabilityBoarder
    *   only signals with pitch probability upper this value would be considered as a speech
    * @param bufferSize
    *   buffer size, in frames
    * @param pitchEstimationAlgorithm
    *   algorithm for pitch estimation
    * @return
    *   list of time intervals with voice(or music) in audio
    */
  def vadInMemory(
      intervalsThreshold: Float = 1.05f,
      probabilityBoarder: Float = 0.8f,
      bufferSize: Int = 4000,
      pitchEstimationAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FftYin,
  ): RIO[R with Scope, List[TimeInterval]] =
    PitchDetection.vadInMemory(self, intervalsThreshold, probabilityBoarder, bufferSize, pitchEstimationAlgorithm)

  /** Recognize speech intervals in audio. Processing with creating temp file.
    *
    * See [[vadInMemory]].
    */
  def vadInFile(
      intervalsThreshold: Float = 0.99f,
      probabilityBoarder: Float = 0.8f,
      bufferSize: Int = 4000,
      pitchEstimationAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FftPitch,
  ): RIO[R with TempFileStorage with Scope, List[TimeInterval]] =
    PitchDetection.vadInFile(self, intervalsThreshold, probabilityBoarder, bufferSize, pitchEstimationAlgorithm)

}

object Audio {

  type ZAudio = Audio[Any, Throwable]

  /** Constructs Audio from file.
    * @param file
    *   audio file.
    */
  def fromFile(file: java.io.File): UIO[ZAudio] =
    fromStream(ZStream.fromFile(file)).tap(_.setFile(file))

  /** Constructs Audio from byte stream. Useful when you receiving audio as byte stream from external storage.
    * @param stream
    *   byte stream containing bytes of audio file
    */
  def fromStream[R, E <: Throwable](
      stream: ZStream[R, E, Byte],
  ): UIO[Audio[R, E]] =
    for {
      filePromise <- Promise.make[Nothing, Path]
      infoPromise <- Promise.make[Nothing, AudioInfo]
    } yield Audio(stream, filePromise, infoPromise)

  /** FFMpeg expects time values in microseconds. Translates seconds to a long value representing microseconds */
  private def secondsToMicros(s: Double): Long = (s * 1_000_000).toLong

}
