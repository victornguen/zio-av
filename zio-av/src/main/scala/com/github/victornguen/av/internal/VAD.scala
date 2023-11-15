package com.github.victornguen.av.internal

import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.pitch.{PitchDetectionHandler, PitchDetectionResult, PitchProcessor}
import be.tarsos.dsp.{AudioDispatcher, AudioEvent}
import com.github.victornguen.av.info.{AudioInfo, Interval}
import com.github.victornguen.av.settings.PitchEstimationAlgorithm
import com.github.victornguen.av.storage.TempFileStorage
import com.github.victornguen.av.{Audio, AudioError}
import zio.prelude.Validation
import zio.{IO, RIO, Scope, ZIO}

import javax.sound.sampled.AudioFormat
import scala.collection.mutable

private[victornguen] object VAD {

  def vadInMemory[R, E <: Throwable](
      audio: Audio[R, E],
      intervalsThreshold: Float = 1.05f,
      probabilityBoarder: Float = 0.8f,
      bufferSize: Int = 4000,
      pitchEstimationAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FftYin,
  ): RIO[R with Scope, List[Interval]] = {
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
  ): RIO[R with TempFileStorage with Scope, List[Interval]] = {
    for {
      file <- audio.getFileScoped
      info <- audio.getInfo
      _    <- validateCodec(info.codec)
      dispatcher = AudioDispatcherFactory.fromFile(file.toFile, bufferSize, 0)
      result <- vad(pitchEstimationAlgorithm, info, intervalsThreshold, probabilityBoarder, dispatcher, bufferSize)
    } yield result
  }
  private def vad[R, E <: Throwable](
      algorithm: PitchProcessor.PitchEstimationAlgorithm,
      audioInfo: AudioInfo,
      intervalsThreshold: Float,
      probabilityBoarder: Float,
      audioDispatcher: AudioDispatcher,
      bufferSize: Int,
  ): ZIO[R with Scope, Throwable, List[Interval]] = {

    for {
      resultBuffer <- ZIO.succeed(mutable.ListBuffer.empty[Interval])
      pitchProcessor = new PitchProcessor(
        algorithm,
        audioInfo.sampleRate.toFloat,
        bufferSize,
        pitchDetectionHandler(resultBuffer, probabilityBoarder),
      )
      _ <- ZIO.attempt(audioDispatcher.addAudioProcessor(pitchProcessor))
      _ <- ZIO.attempt(audioDispatcher.run())
      result = unionIntervals(resultBuffer.toList, intervalsThreshold)
    } yield result
  }

  private def pitchDetectionHandler(
      putInto: mutable.ListBuffer[Interval],
      probabilityBoarder: Float,
  ): PitchDetectionHandler = { (pitchDetectionResult: PitchDetectionResult, audioEvent: AudioEvent) =>
    {
      val proba = pitchDetectionResult.getProbability
      if (proba > probabilityBoarder) {
        putInto += Interval(audioEvent.getTimeStamp, audioEvent.getEndTimeStamp)
      }
    }
  }

  private def unionIntervals(intervals: List[Interval], threshold: Float): List[Interval] =
    intervals
      .foldLeft(List.empty[Interval]) {
        case (Nil, el) => List(el)
        case ((last @ Interval(lastStart, lastEnd)) :: rest, el @ Interval(newStart, newEnd)) =>
          if (newStart - lastEnd < threshold) Interval(lastStart, newEnd) :: rest
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
