# ZIO AV

[ZIO AV](https://github.com/victornguen/zio-av) is a library for audio (video coming soon) processing with ZIO, built on top of
the [FFmpeg libraries](https://github.com/FFmpeg/FFmpeg)
using [JavaCPP](https://github.com/bytedeco/javacpp) and [JavaCV](https://github.com/bytedeco/javacv/).
It provides a simple way to work with audio data, enabling you to perform various operations such
as transcoding (encode audio with different codec, sample rate, bitrate), getting audio metadata, cropping audio and
etc.

[![Development](https://img.shields.io/badge/Project%20Stage-Development-green.svg)](https://github.com/zio/zio/wiki/Project-Stages) [![Sonatype Snapshots](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.github.victornguen/zio-av_2.13.svg?label=Sonatype%20Snapshot)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/victornguen/zio-av_2.13/) [![ZIO AV](https://img.shields.io/github/stars/victornguen/zio-av?style=social)](https://github.com/victornguen/zio-av)

## Features

### Working with audio

- **Transcoding**: transcode your audios to other format, change codec and other parameters.
- **Metadata**: retrieve audio metadata
- **Cropping**: cut necessary passages from audio
- **VAD**: detect voice in audio (only for audio with dialogs, not for music)

## Getting Started

### Installation

Add the following dependency to your `build.sbt` file:

```sbt
libraryDependencies += "io.github.victornguen" %% "zio-av" % "<version>"
```

Since library uses TarsosDSP, you need to add following resolver:

```sbt
resolvers += "be.0110.repo-releases" at "https://mvn.0110.be/releases"
```

### Example

Here's a simple example that demonstrates how to transcode MP3 to WAV with downsampling to 8 kHz:

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

More examples available in [zio-av-examples](./zio-av-examples).

## Contributing

Contributions to zio-av are welcome! If you find any issues or have suggestions for improvements, please submit a bug
report or feature request in the [issue tracker](https://github.com/victornguen/zio-av/issues). Pull requests are also
appreciated.

## License

This project is licensed under the [Apache 2.0 License](../LICENSE).
