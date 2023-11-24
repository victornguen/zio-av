package com.github.victornguen.av.internal

import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.pitch.{PitchDetectionHandler, PitchDetectionResult, PitchProcessor}
import be.tarsos.dsp.{AudioDispatcher, AudioEvent}
import com.github.victornguen.av.info.{AudioInfo, TimeInterval}
import com.github.victornguen.av.settings.PitchEstimationAlgorithm
import com.github.victornguen.av.storage.TempFileStorage
import com.github.victornguen.av.{Audio, AudioError}
import zio.prelude.Validation
import zio.{IO, RIO, Scope, Task, ZIO}

import javax.sound.sampled.AudioFormat
import scala.collection.mutable

private[victornguen] object PitchDetection {

  def vadInMemory[R, E <: Throwable](
      audio: Audio[R, E],
      intervalsThreshold: Float = 1.05f,
      probabilityBoarder: Float = 0.8f,
      bufferSize: Int = 4000,
      pitchEstimationAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FftYin,
  ): RIO[R with Scope, List[TimeInterval]] = {
    for {
      chunk       <- audio.stream.runCollect
      info        <- audio.getInfo
      isBigEndian <- isBigEndian(info.codec)
      format     = new AudioFormat(info.sampleRate.toFloat, info.bitsPerSample, info.channels, info.codecInfo.isSigned, isBigEndian)
      dispatcher = AudioDispatcherFactory.fromByteArray(chunk.toArray, format, bufferSize, 0)
      result <- vad(pitchEstimationAlgorithm, info, intervalsThreshold, probabilityBoarder, dispatcher, bufferSize)
    } yield result
  }

  def vadInFile[R, E <: Throwable](
      audio: Audio[R, E],
      intervalsThreshold: Float = 0.99f,
      probabilityBoarder: Float = 0.8f,
      bufferSize: Int = 1024,
      pitchEstimationAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FftPitch,
  ): RIO[R with TempFileStorage with Scope, List[TimeInterval]] = {
    for {
      file <- audio.getFileScoped
      info <- audio.getInfo
      _    <- validateCodec(info.codec)
      dispatcher = AudioDispatcherFactory.fromFile(file.toFile, bufferSize, 0)
      result <- vad(pitchEstimationAlgorithm, info, intervalsThreshold, probabilityBoarder, dispatcher, bufferSize)
    } yield result
  }

  /** VAD implementation.
    * @param algorithm
    *   see [[be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm]]
    * @param intervalsThreshold
    *   time interval threshold, signals with a time difference greater than this value will be considered different phrases
    * @param probabilityBoarder
    *   only signals with pitch probability upper this value would be considered as a speech
    * @param audioDispatcher
    *   see [[be.tarsos.dsp.AudioDispatcher]]
    * @param bufferSize
    *   buffer size, in frames
    * @return
    *   list of time intervals with speech
    */
  private def vad[E <: Throwable](
      algorithm: PitchProcessor.PitchEstimationAlgorithm,
      audioInfo: AudioInfo,
      intervalsThreshold: Float,
      probabilityBoarder: Float,
      audioDispatcher: AudioDispatcher,
      bufferSize: Int,
  ): Task[List[TimeInterval]] =
    for {
      resultBuffer <- ZIO.succeed(mutable.ListBuffer.empty[TimeInterval])
      pitchDetectionHandler = makePitchDetectionHandler(
        resultBuffer,
        _.getProbability > probabilityBoarder,
        (_, audioEvent) => TimeInterval(audioEvent.getTimeStamp, audioEvent.getEndTimeStamp),
      )
      pitchProcessor = new PitchProcessor(
        algorithm,
        audioInfo.sampleRate.toFloat,
        bufferSize,
        pitchDetectionHandler,
      )
      _ <- ZIO.attempt(audioDispatcher.addAudioProcessor(pitchProcessor))
      _ <- ZIO.attempt(audioDispatcher.run())
      result = unionIntervals(resultBuffer.toList, intervalsThreshold)
    } yield result

  private def makePitchDetectionHandler[T](
      putInto: mutable.ListBuffer[T],
      rule: PitchDetectionResult => Boolean,
      extract: (PitchDetectionResult, AudioEvent) => T,
  ): PitchDetectionHandler =
    (pitchDetectionResult: PitchDetectionResult, audioEvent: AudioEvent) => {
      if (rule(pitchDetectionResult)) {
        putInto += extract(pitchDetectionResult, audioEvent)
      }
    }

  private def unionIntervals(intervals: List[TimeInterval], threshold: Float): List[TimeInterval] =
    intervals
      .foldLeft(List.empty[TimeInterval]) {
        case (Nil, el) => List(el)
        case ((last @ TimeInterval(lastStart, lastEnd)) :: rest, el @ TimeInterval(newStart, newEnd)) =>
          if (newStart - lastEnd < threshold) TimeInterval(lastStart, newEnd) :: rest
          else el :: last :: rest
      }
      .reverse

  private def validateCodec(codec: String): IO[AudioError.UnsupportedCodecError, String] =
    Validation
      .fromPredicateWith(
        AudioError.UnsupportedCodecError("audio must be encoded in pcm"),
      )(codec.toLowerCase)(c => c.startsWith("pcm_"))
      .toZIO

  private def isBigEndian(codecName: String): IO[AudioError.UnsupportedCodecError, Boolean] =
    Validation
      .fromPredicateWith(
        AudioError.UnsupportedCodecError("audio must be encoded with big endian or little endian byte order"),
      )(codecName.toLowerCase)(c => c.startsWith("pcm_") && (c.endsWith("le") || c.endsWith("be")))
      .map(_.takeRight(2) == "be")
      .toZIO

}
