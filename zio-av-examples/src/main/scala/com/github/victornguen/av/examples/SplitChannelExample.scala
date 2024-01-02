package com.github.victornguen.av.examples

import com.github.victornguen.av.settings.AVLogLevel
import com.github.victornguen.av.storage.DefaultTempFileStorage
import com.github.victornguen.av.{Audio, Multimedia}
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.File

object SplitChannelExample extends ZIOAppDefault {

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    val audioFilePath = "zio-av-examples\\src\\main\\resources\\Shooting Stars.mp3"
    val file          = new File(audioFilePath)
    for {
      _                <- Multimedia.setLogLevel(AVLogLevel.Info)
      _                <- Multimedia.setZIOLogging()
      audio            <- Audio.fromFile(file)
      info             <- audio.getInfo
      _                <- Console.printLine(info)
      extractedChannel <- audio.extractChannel(channelNumber = 0)
      audioInfo        <- extractedChannel.getInfo
      meta = audioInfo.metadata
      _           <- Console.printLine(meta)
      croppedFile <- extractedChannel.getFileScoped
      _           <- Console.printLine(audioInfo)
      _           <- Console.printLine(croppedFile.toString())
    } yield ()
  }
    .provide(
      DefaultTempFileStorage.makeLayer,
      Scope.default,
    )

}
