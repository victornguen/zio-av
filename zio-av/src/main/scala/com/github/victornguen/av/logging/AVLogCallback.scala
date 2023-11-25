package com.github.victornguen.av.logging

import com.github.victornguen.av.settings.AVLogLevel
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacv.FFmpegLogCallback
import zio.ZIO

trait AVLogCallback[R, +E] extends FFmpegLogCallback {

  def log(logLevel: AVLogLevel, msg: String): ZIO[R, E, Unit]

  def runtime: zio.Runtime[R]

  override def call(level: Int, msg: BytePointer): Unit =
    zio.Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.run(log(level, msg.getString))
    }

}
