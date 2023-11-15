package com.github.victornguen.av.examples

import com.github.victornguen.av.Audio
import com.github.victornguen.av.settings.FFMpegLogLevel
import com.github.victornguen.av.storage.DefaultTempFileStorage
import zio._

import java.io.File

object CropAudio extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    val audioFilePath = "zio-av-examples\\src\\main\\resources\\Shooting Stars.mp3"
    val file          = new File(audioFilePath)
    for {
      audio     <- Audio.fromFile(file)
      audio     <- ZIO.succeed(audio.withLogLevel(FFMpegLogLevel.Info))
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
