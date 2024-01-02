package com.github.victornguen.av.logging

import com.github.victornguen.av.settings.AVLogLevel
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.ffmpeg.global.avutil.av_log_set_level
import zio.{Scope, UIO, URIO, ZIO}

import java.io.File

trait AVLogging {

  def setLogLevel(logLevel: AVLogLevel): UIO[Unit] = ZIO.succeed(av_log_set_level(logLevel))

  def logToFile(file: File): URIO[Scope, Unit] =
    for {
      scope <- ZIO.scope
      cb = WriteToFileLogCallback(file)
      _ <- ZIO.succeedBlocking(avutil.setLogCallback(cb))
      _ <- scope.addFinalizer(cb.closeWriter.orDie)
    } yield ()

  def logWith[R, E](callback: AVLogCallback[R, E]): UIO[Unit] = ZIO.succeedBlocking(avutil.setLogCallback(callback))

  def setZIOLogging(): URIO[Any, Unit] = ZIO.succeedBlocking(avutil.setLogCallback(ZIOLoggingLogCallback))

}
