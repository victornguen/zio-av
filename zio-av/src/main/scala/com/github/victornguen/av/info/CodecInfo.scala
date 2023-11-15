package com.github.victornguen.av.info

import org.bytedeco.ffmpeg.global.avcodec

final case class CodecInfo(private val capabilities: Int) extends AnyVal {

  def isSigned: Boolean = haveCapability(avcodec.AV_CODEC_CAP_VARIABLE_FRAME_SIZE)

  /** Codec is experimental and is thus avoided in favor of non experimental encoders
    */
  def isExperimental: Boolean = haveCapability(avcodec.AV_CODEC_CAP_EXPERIMENTAL)

  /** Codec supports frame-level multithreading.
    */
  def supportsFrameThreads: Boolean = haveCapability(avcodec.AV_CODEC_CAP_FRAME_THREADS)

  /** Codec supports slice-based (or partition-based) multithreading.
    */
  def supportsSliceThreads: Boolean = haveCapability(avcodec.AV_CODEC_CAP_SLICE_THREADS)

  /** Codec supports changed parameters at any point.
    */
  def supportsChangeParams: Boolean = haveCapability(avcodec.AV_CODEC_CAP_PARAM_CHANGE)

  /** Audio encoder supports receiving a different number of samples in each call.
    */
  def variableFrameSize: Boolean = haveCapability(avcodec.AV_CODEC_CAP_VARIABLE_FRAME_SIZE)

  private def haveCapability(capabilityFlag: Int) = (capabilityFlag & capabilities) != 0

}
