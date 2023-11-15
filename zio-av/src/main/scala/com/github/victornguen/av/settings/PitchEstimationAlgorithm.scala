package com.github.victornguen.av.settings

import be.tarsos.dsp.pitch.PitchProcessor.{PitchEstimationAlgorithm => PEA}
import enumeratum.{Enum, EnumEntry}

sealed class PitchEstimationAlgorithm(val algorithm: PEA) extends EnumEntry

object PitchEstimationAlgorithm extends Enum[PitchEstimationAlgorithm] {

  override def values: IndexedSeq[PitchEstimationAlgorithm] = findValues

  case object FftPitch       extends PitchEstimationAlgorithm(PEA.FFT_PITCH)
  case object FftYin         extends PitchEstimationAlgorithm(PEA.FFT_YIN)
  case object Yin            extends PitchEstimationAlgorithm(PEA.FFT_YIN)
  case object Mpm            extends PitchEstimationAlgorithm(PEA.MPM)
  case object DynamicWavelet extends PitchEstimationAlgorithm(PEA.DYNAMIC_WAVELET)
  case object Amdf           extends PitchEstimationAlgorithm(PEA.AMDF)

  implicit def toEnumValue(pitchEstimationAlgorithm: PitchEstimationAlgorithm): PEA = pitchEstimationAlgorithm.algorithm

}
