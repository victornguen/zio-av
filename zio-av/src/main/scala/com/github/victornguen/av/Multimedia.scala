package com.github.victornguen.av

import com.github.victornguen.av.Audio._
import com.github.victornguen.av.internal.VAD
import com.github.victornguen.av.info.{AudioInfo, Interval}
import com.github.victornguen.av.settings.{FFMpegLogLevel, PitchEstimationAlgorithm}
import com.github.victornguen.av.storage.TempFileStorage
import org.bytedeco.ffmpeg.global.avutil.av_log_set_level
import org.bytedeco.javacv.{FFmpegFrameGrabber, FFmpegFrameRecorder}
import zio.nio.file.{Files, Path}
import zio.stream.{ZSink, ZStream}
import zio.{Promise, RIO, Scope, Task, UIO, URIO, ZIO}

import java.io._
import scala.jdk.CollectionConverters.MapHasAsJava

trait Multimedia[-R, +E]

final case class Audio[-R, +E <: Throwable](
    stream: ZStream[R, E, Byte],
    private val filePromise: Promise[Nothing, Path],
    private val infoPromise: Promise[Nothing, AudioInfo],
    logLevel: FFMpegLogLevel = FFMpegLogLevel.Quiet,
) extends Multimedia[R, E] { self =>

  private def frameGrabber: ZIO[R with Scope, E, FFmpegFrameGrabber] = ZIO.acquireRelease {
    for {
      is <- stream.toInputStream
      _  <- ZIO.succeed(av_log_set_level(logLevel))
    } yield new FFmpegFrameGrabber(is)
  } { grabber =>
    ZIO.attempt {
      grabber.stop()
      grabber.release()
    }.orDie
  }

  def getFile: RIO[R with TempFileStorage, Path] = {
    filePromise.poll.some.flatten.orElse {
      for {
        newFile <- TempFileStorage.createTempFile()
        _       <- stream.run(ZSink.fromFile(newFile.toFile))
        _       <- filePromise.complete(ZIO.succeed(newFile))
      } yield newFile
    }
  }

  def getFileScoped: RIO[R with TempFileStorage with Scope, Path] =
    filePromise.poll.some.flatten
      .flatMap { file =>
        TempFileStorage
          .createTempFileScoped()
          .tap(tempFile => Files.copy(file, tempFile))
      } orElse {
      TempFileStorage
        .createTempFileScoped()
        .tap(tempFile => stream.run(ZSink.fromFile(tempFile.toFile)))
    }

  def getInfo: RIO[R with Scope, AudioInfo] = {
    infoPromise.poll.some.flatten.orElse {
      for {
        grabber <- frameGrabber
        info    <- AudioInfo.make(grabber)
        _       <- infoPromise.complete(ZIO.succeed(info))
      } yield info
    }
  }

  def cropStream(
      from: Double,
      to: Double,
      withMeta: Boolean = true,
  ): RIO[R with Scope, Audio[Any, Throwable]] = {
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

  def crop(
      from: Double,
      to: Double,
      withMeta: Boolean = true,
  ): RIO[R with Scope with TempFileStorage, ZAudio] =
    for {
      info         <- getInfo
      file         <- TempFileStorage.createTempFileScoped().map(_.toFile)
      recorder     <- fileFrameRecorder(info.channels, file)
      grabber      <- frameGrabber
      _            <- recordSegment(grabber, recorder, from, to, info, withMeta)
      croppedAudio <- Audio.fromFile(file)
    } yield croppedAudio

  private def recordSegment(
      grabber: FFmpegFrameGrabber,
      recorder: FFmpegFrameRecorder,
      from: Double,
      to: Double,
      info: AudioInfo,
      copyMeta: Boolean,
      additionalMeta: Map[String, String] = Map.empty,
  ): Task[Unit] =
    ZIO.attempt {
      grabber.start()
      val start = secondsToMicros(from)
      val end   = secondsToMicros(to)
      grabber.setAudioTimestamp(start)
      recorder.setFormat(info.format)
      recorder.setSampleRate(info.sampleRate)
      recorder.setAudioCodec(info.codecId)
      info.bitrate.foreach(recorder.setAudioBitrate)
      recorder.setAudioChannels(info.channels)
      if (copyMeta) recorder.setMetadata((info.metadata ++ additionalMeta).asJava)
      else if (additionalMeta.nonEmpty) recorder.setMetadata(additionalMeta.asJava)
      recorder.start()
      while (grabber.getTimestamp <= end) {
        recorder.record(grabber.grabSamples())
      }
    }

  private def bufferFrameRecorder(channels: Int, buffer: OutputStream): URIO[Scope, FFmpegFrameRecorder] =
    useFrameRecorder(new FFmpegFrameRecorder(buffer, channels))

  private def fileFrameRecorder(channel: Int, file: File): URIO[Scope, FFmpegFrameRecorder] =
    useFrameRecorder(new FFmpegFrameRecorder(file, channel))

  private def useFrameRecorder(recorder: FFmpegFrameRecorder): URIO[Scope, FFmpegFrameRecorder] = ZIO.acquireRelease {
    ZIO.succeed {
      av_log_set_level(logLevel)
      recorder
    }
  } { recorder =>
    ZIO.attempt {
      recorder.stop()
      recorder.release()
    }.orDie
  }

  def withLogLevel(logLevel: FFMpegLogLevel): Audio[R, E] =
    self.copy(logLevel = logLevel)

  def vadInMemory(
      intervalsThreshold: Float = 1.05f,
      probabilityBoarder: Float = 0.8f,
      bufferSize: Int = 4000,
      pitchEstimationAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FftYin,
  ): RIO[R with Scope, List[Interval]] =
    VAD.vadInMemory(self, intervalsThreshold, probabilityBoarder, bufferSize, pitchEstimationAlgorithm)

  def vadInFile(
      intervalsThreshold: Float = 0.99f,
      probabilityBoarder: Float = 0.8f,
      bufferSize: Int = 1024,
      pitchEstimationAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FftPitch,
  ): RIO[R with TempFileStorage with Scope, List[Interval]] =
    VAD.vadInFile(self, intervalsThreshold, probabilityBoarder, bufferSize, pitchEstimationAlgorithm)

}

object Audio {

  type ZAudio = Audio[Any, Throwable]
  def fromFile(file: java.io.File): UIO[ZAudio] = fromStream(ZStream.fromFile(file))

  def fromStream[R, E <: Throwable](
      stream: ZStream[R, E, Byte],
  ): UIO[Audio[R, E]] =
    for {
      filePromise <- Promise.make[Nothing, Path]
      infoPromise <- Promise.make[Nothing, AudioInfo]
    } yield Audio(stream, filePromise, infoPromise)

  private def secondsToMicros(s: Double): Long = (s * 1_000_000).toLong

}
