package com.github.victornguen.av.info

import org.bytedeco.javacv.FFmpegFrameGrabber
import zio.Task

trait InfoRetriever[I] {
  def make(grabber: FFmpegFrameGrabber): Task[I]
}

object InfoRetriever {
  def apply[I: InfoRetriever]: InfoRetriever[I] = implicitly[InfoRetriever[I]]

  implicit val audioInfoRetriever: InfoRetriever[AudioInfo] =
    (grabber: FFmpegFrameGrabber) => AudioInfo.make(grabber)
}
