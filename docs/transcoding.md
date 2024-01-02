## Transcoding

ZIO AV provides functional to transcode your audio files to another format.

When performing transcoding, consider the following:

- **Choose the appropriate output codec** - library uses FFmpeg, that supports plenty of different audio and video
  codecs,
  but for some of them FFmpeg provides only decoding capabilities.
  See more in [this wiki page](https://en.wikipedia.org/wiki/Comparison_of_audio_coding_formats)
- **Set appropriate audio parameters** - some audio formats can have only 1 channel, allows specific sample rate, etc.
  Consider tou provided appropriate parameters.

### Example

```scala
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
```