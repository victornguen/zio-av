package com.github.victornguen.av

import com.github.victornguen.av.info.InfoRetriever
import com.github.victornguen.av.logging.AVLogging
import com.github.victornguen.av.storage.TempFileStorage
import org.bytedeco.javacv.FFmpegFrameGrabber
import zio.nio.file.{Files, Path}
import zio.stream.{ZSink, ZStream}
import zio.{Promise, RIO, Scope, UIO, ZIO}

import java.io.File

abstract class Multimedia[-R, +E <: Throwable, I: InfoRetriever](
    stream: ZStream[R, E, Byte],
    private val filePromise: Promise[Nothing, Path],
    private val infoPromise: Promise[Nothing, I],
) { self =>

  /** Get audio file. Creates temp file if its not present in filesystem. */
  def getFile: RIO[R with TempFileStorage, Path] =
    filePromise.poll.some.flatten.orElse {
      for {
        newFile <- TempFileStorage.createTempFile()
        _       <- stream.run(ZSink.fromFile(newFile.toFile))
        _       <- filePromise.completeWith(ZIO.succeed(newFile))
      } yield newFile
    }

  /** Get audio file. If a file is present in the filesystem, it makes a copy of the file and returns it. Otherwise, it creates a temp file,
    * writes audio into it, and returns the file.
    */
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

  /** Get information about audio. */
  def getInfo: RIO[R with Scope, I] =
    infoPromise.poll.some.flatten.orElse {
      for {
        grabber <- frameGrabber
        info    <- InfoRetriever[I].make(grabber)
        _       <- infoPromise.completeWith(ZIO.succeed(info))
      } yield info
    }

  protected[victornguen] def setFile(path: Path): UIO[Multimedia[R, E, I]] =
    filePromise.completeWith(ZIO.succeed(path)).as(self)

  protected[victornguen] def setFile(file: File): UIO[Multimedia[R, E, I]] =
    setFile(Path.fromJava(file.toPath))

  protected def frameGrabber: ZIO[R with Scope, E, FFmpegFrameGrabber] =
    ZIO.acquireRelease(
      stream.toInputStream.map(new FFmpegFrameGrabber(_)),
    ) { grabber =>
      ZIO.attempt {
        grabber.stop()
        grabber.release()
      }.orDie
    }

}

object Multimedia extends AVLogging
