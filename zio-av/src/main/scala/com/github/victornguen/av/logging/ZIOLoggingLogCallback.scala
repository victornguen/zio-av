package com.github.victornguen.av.logging
import com.github.victornguen.av.settings.AVLogLevel
import zio.ZIO

case object ZIOLoggingLogCallback extends AVLogCallbackDefault[Nothing] {
  override def log(logLevel: AVLogLevel, msg: String): ZIO[Any, Nothing, Unit] =
    logLevel match {
      case AVLogLevel.Quiet                     => ZIO.unit
      case AVLogLevel.Panic | AVLogLevel.Fatal  => ZIO.logFatal(msg)
      case AVLogLevel.Error                     => ZIO.logError(msg)
      case AVLogLevel.Warning                   => ZIO.logWarning(msg)
      case AVLogLevel.Info | AVLogLevel.Verbose => ZIO.logInfo(msg)
      case _                                    => ZIO.log(msg)
    }
}
