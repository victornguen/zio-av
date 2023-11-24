package com.github.victornguen.av.examples

import com.github.victornguen.av.Audio
import com.github.victornguen.av.settings.{AudioCodec, AudioFormat, FFMpegLogLevel}
import com.github.victornguen.av.storage.DefaultTempFileStorage
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.File

object TranscodingExample extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    val audioFilePath = "zio-av-examples\\src\\main\\resources\\Shooting Stars.mp3"
    val file          = new File(audioFilePath)
    for {
      audio        <- Audio.fromFile(file).map(_.withLogLevel(FFMpegLogLevel.Info))
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
