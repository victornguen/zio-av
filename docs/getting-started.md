## Getting started

### Installation


Add the following dependency to your `build.sbt` file:

```sbt
libraryDependencies += "io.github.victornguen" %% "zio-av" % "<version>"
```

Since library uses TarsosDSP, you need to add following resolver:

```sbt
resolvers += "be.0110.repo-releases" at "https://mvn.0110.be/releases"
```

### TempFileStorage

Some methods of `Multimedia` and it's subclasses use files to process data. These methods depends on `TempFileStorage`
layer,
that responsible for creating temp files.

`TempFileStorage` layer can be created following ways:

```scala
val defaultTempFileStorage: ULayer[TempFileStorage] = DefaultTempFileStorage.makeLayer

val customTempFileStorage: ULayer[TempFileStorage] = CustomTempFileLocalStorage.makeLayer(Path("custom/path/"))
```

- `DefaultTempFileStorage` creates temp files in default location
- `CustomTempFileStorage` will create files in specified directory

### Set log output of FFmpeg

By default, FFmpeg writes all logs in stdout. You need to set up log level and logging backend in the start of application.

Call `Multimedia.setLogLevel` to set log level.

Call one of the following functions to set logging backend:

- `Multimedia.setZIOLogging()` - to make FFmpeg use ZIO log methods
- `Multimedia.logToFile(file)` - to write FFmpeg logs in file
- `Multimedia.logWith(callback)` - to provide custom callback

See [examples](../zio-av-examples).