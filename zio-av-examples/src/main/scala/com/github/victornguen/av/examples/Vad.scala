package com.github.victornguen.av.examples

import com.github.victornguen.av.Audio
import com.github.victornguen.av.settings.FFMpegLogLevel
import com.github.victornguen.av.storage.DefaultTempFileStorage
import zio._

import java.io.File

object Vad extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    val audioFilePath = "path-to-audio.wav" // audio must be pcm-encoded
    val file          = new File(audioFilePath)
    for {
      audio             <- Audio.fromFile(file)
      audio             <- ZIO.succeed(audio.withLogLevel(FFMpegLogLevel.Info))
      inMemoryVadResult <- audio.vadInMemory(intervalsThreshold = 1.1f)
      inFileVadResult   <- audio.vadInFile()
      _                 <- Console.printLine(inMemoryVadResult)
      _                 <- Console.printLine(inFileVadResult)
    } yield ()
  }
    .provide(
      DefaultTempFileStorage.makeLayer,
      Scope.default,
    )

}
