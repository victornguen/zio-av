package com.github.victornguen.av.logging

import com.github.victornguen.av.settings.AVLogLevel
import zio.{IO, ZIO}

import java.io.{File, FileWriter, IOException}

final case class WriteToFileLogCallBack(file: File) extends AVLogCallbackDefault[IOException] {

  private val writer = new FileWriter(file)

  override def log(level: AVLogLevel, msg: String): IO[IOException, Unit] = ZIO.attemptBlockingIO {
    writer.write(msg)
    writer.flush()
  }

  private[victornguen] val closeWriter: IO[IOException, Unit] = ZIO.attemptBlockingIO(writer.close())
}
