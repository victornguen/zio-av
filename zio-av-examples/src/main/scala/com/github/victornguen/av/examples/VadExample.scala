package com.github.victornguen.av.examples

import com.github.victornguen.av.storage.DefaultTempFileStorage
import com.github.victornguen.av.{Audio, Multimedia}
import zio._

import java.io.File

object VadExample extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    val audioFilePath = "path-to-audio.wav" // audio must be pcm-encoded
    val file          = new File(audioFilePath)
    for {
      _                 <- Multimedia.setZIOLogging()
      audio             <- Audio.fromFile(file)
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
