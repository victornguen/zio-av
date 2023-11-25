package com.github.victornguen.av.examples

import com.github.victornguen.av.settings.AVLogLevel
import com.github.victornguen.av.storage.DefaultTempFileStorage
import com.github.victornguen.av.{Audio, Multimedia}
import zio._

import java.io.File

object CropAudioExample extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    val audioFilePath = "zio-av-examples\\src\\main\\resources\\Shooting Stars.mp3"
    val file          = new File(audioFilePath)
    for {
      _         <- Multimedia.setLogLevel(AVLogLevel.Info)
      _         <- Multimedia.setZIOLogging()
      audio     <- Audio.fromFile(file)
      info      <- audio.getInfo
      _         <- Console.printLine(info)
      cropped   <- audio.cropStream(4d, 20d)
      audioInfo <- cropped.getInfo
      meta = audioInfo.metadata
      _           <- Console.printLine(meta)
      croppedFile <- cropped.getFileScoped
      _           <- Console.printLine(audioInfo)
      _           <- Console.printLine(croppedFile.toString())
    } yield ()
  }
    .provide(
      DefaultTempFileStorage.makeLayer,
      Scope.default,
    )

}
