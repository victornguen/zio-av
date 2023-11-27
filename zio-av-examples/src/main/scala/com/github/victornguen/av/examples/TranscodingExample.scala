package com.github.victornguen.av.examples

import com.github.victornguen.av.settings.{AVLogLevel, AudioCodec, AudioFormat}
import com.github.victornguen.av.storage.DefaultTempFileStorage
import com.github.victornguen.av.{Audio, Multimedia}
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.File

object TranscodingExample extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    val audioFilePath = "zio-av-examples\\src\\main\\resources\\Shooting Stars.mp3"
    val file          = new File(audioFilePath)
    for {
      _            <- Multimedia.setLogLevel(AVLogLevel.Info)
      _            <- Multimedia.logToFile(new File("./log.log"))
      audio        <- Audio.fromFile(file)
      newAudio     <- audio.transcode(AudioCodec.PCM.S16LE, AudioFormat.WAV, Some(8000))
      newAudioInfo <- newAudio.getInfo
      newAudioFile <- newAudio.getFile
      _            <- Console.printLine(newAudioInfo)
      _            <- Console.printLine(newAudioFile.toString)
    } yield ()
  }
    .provide(
      DefaultTempFileStorage.makeLayer,
      Scope.default,
    )

}
