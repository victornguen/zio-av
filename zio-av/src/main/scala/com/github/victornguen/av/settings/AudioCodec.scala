package com.github.victornguen.av.settings

import com.github.victornguen.av.settings.AudioCodec.CodecId
import enumeratum._
import org.bytedeco.ffmpeg.global.avcodec

sealed class AudioCodec(val codecId: CodecId) extends EnumEntry

object AudioCodec extends Enum[AudioCodec] {
  private[victornguen] type CodecId = Int

  override def values: IndexedSeq[AudioCodec] = findValues

  implicit def codecToCodecId(codec: AudioCodec): CodecId = codec.codecId

  object PCM {
    case object S16LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE)
    case object S16BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S16BE)
    case object U16LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_U16LE)
    case object U16BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_U16BE)
    case object S8           extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S8)
    case object U8           extends AudioCodec(avcodec.AV_CODEC_ID_PCM_U8)
    case object MULAW        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_MULAW)
    case object ALAW         extends AudioCodec(avcodec.AV_CODEC_ID_PCM_ALAW)
    case object S32LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S32LE)
    case object S32BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S32BE)
    case object U32BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_U32BE)
    case object S24LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S24LE)
    case object S24BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S24BE)
    case object U24LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_U24LE)
    case object U24BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_U24BE)
    case object S24DAUD      extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S24DAUD)
    case object ZORK         extends AudioCodec(avcodec.AV_CODEC_ID_PCM_ZORK)
    case object S16LE_PLANAR extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE_PLANAR)
    case object DVD          extends AudioCodec(avcodec.AV_CODEC_ID_PCM_DVD)
    case object F32BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_F32BE)
    case object F32LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_F32LE)
    case object F64BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_F64BE)
    case object F64LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_F64LE)
    case object BLURAY       extends AudioCodec(avcodec.AV_CODEC_ID_PCM_BLURAY)
    case object LXF          extends AudioCodec(avcodec.AV_CODEC_ID_PCM_LXF)
    case object M            extends AudioCodec(avcodec.AV_CODEC_ID_S302M)
    case object S8_PLANAR    extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S8_PLANAR)
    case object S24LE_PLANAR extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S24LE_PLANAR)
    case object S32LE_PLANAR extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S32LE_PLANAR)
    case object S16BE_PLANAR extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S16BE_PLANAR)
    case object S64LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S64LE)
    case object S64BE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_S64BE)
    case object F16LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_F16LE)
    case object F24LE        extends AudioCodec(avcodec.AV_CODEC_ID_PCM_F24LE)
    case object VIDC         extends AudioCodec(avcodec.AV_CODEC_ID_PCM_VIDC)
    case object SGA          extends AudioCodec(avcodec.AV_CODEC_ID_PCM_SGA)
  }

  object ADPCM {
    case object IMA_QT      extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_QT)
    case object IMA_WAV     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_WAV)
    case object IMA_DK3     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_DK3)
    case object IMA_DK4     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_DK4)
    case object IMA_WS      extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_WS)
    case object IMA_SMJPEG  extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_SMJPEG)
    case object MS          extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_MS)
    case object ADPCM_4XM   extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_4XM)
    case object XA          extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_XA)
    case object ADX         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_ADX)
    case object EA          extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_EA)
    case object G726        extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_G726)
    case object CT          extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_CT)
    case object SWF         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_SWF)
    case object YAMAHA      extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_YAMAHA)
    case object SBPRO_4     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_SBPRO_4)
    case object SBPRO_3     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_SBPRO_3)
    case object SBPRO_2     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_SBPRO_2)
    case object THP         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_THP)
    case object IMA_AMV     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_AMV)
    case object EA_R1       extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_EA_R1)
    case object EA_R3       extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_EA_R3)
    case object EA_R2       extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_EA_R2)
    case object IMA_EA_SEAD extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_EA_SEAD)
    case object IMA_EA_EACS extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_EA_EACS)
    case object EA_XAS      extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_EA_XAS)
    case object EA_MAXIS_XA extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_EA_MAXIS_XA)
    case object IMA_ISS     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_ISS)
    case object G722        extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_G722)
    case object IMA_APC     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_APC)
    case object VIMA        extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_VIMA)
    case object AFC         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_AFC)
    case object IMA_OKI     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_OKI)
    case object DTK         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_DTK)
    case object IMA_RAD     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_RAD)
    case object G726LE      extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_G726LE)
    case object THP_LE      extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_THP_LE)
    case object PSX         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_PSX)
    case object AICA        extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_AICA)
    case object IMA_DAT4    extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_DAT4)
    case object MTAF        extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_MTAF)
    case object AGM         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_AGM)
    case object ARGO        extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_ARGO)
    case object IMA_SSI     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_SSI)
    case object ZORK        extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_ZORK)
    case object IMA_APM     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_APM)
    case object IMA_ALP     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_ALP)
    case object IMA_MTF     extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_MTF)
    case object IMA_CUNNING extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_CUNNING)
    case object IMA_MOFLEX  extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_MOFLEX)
    case object IMA_ACORN   extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_IMA_ACORN)
    case object XMD         extends AudioCodec(avcodec.AV_CODEC_ID_ADPCM_XMD)
  }

  object AMR {
    case object NB extends AudioCodec(avcodec.AV_CODEC_ID_AMR_NB)
    case object WB extends AudioCodec(avcodec.AV_CODEC_ID_AMR_WB)
  }

  object RealAudio {
    case object RA_144 extends AudioCodec(avcodec.AV_CODEC_ID_RA_144)

    case object RA_288 extends AudioCodec(avcodec.AV_CODEC_ID_RA_288)
  }

  object DPCM {
    case object ROQ       extends AudioCodec(avcodec.AV_CODEC_ID_ROQ_DPCM)
    case object INTERPLAY extends AudioCodec(avcodec.AV_CODEC_ID_INTERPLAY_DPCM)
    case object XAN       extends AudioCodec(avcodec.AV_CODEC_ID_XAN_DPCM)
    case object SOL       extends AudioCodec(avcodec.AV_CODEC_ID_SOL_DPCM)
    case object SDX2      extends AudioCodec(avcodec.AV_CODEC_ID_SDX2_DPCM)
    case object GREMLIN   extends AudioCodec(avcodec.AV_CODEC_ID_GREMLIN_DPCM)
    case object DERF      extends AudioCodec(avcodec.AV_CODEC_ID_DERF_DPCM)
    case object WADY      extends AudioCodec(avcodec.AV_CODEC_ID_WADY_DPCM)
    case object CBD2      extends AudioCodec(avcodec.AV_CODEC_ID_CBD2_DPCM)
  }

  object AUDIO {
    case object MP2             extends AudioCodec(avcodec.AV_CODEC_ID_MP2)
    case object MP3             extends AudioCodec(avcodec.AV_CODEC_ID_MP3)
    case object AAC             extends AudioCodec(avcodec.AV_CODEC_ID_AAC)
    case object AC3             extends AudioCodec(avcodec.AV_CODEC_ID_AC3)
    case object DTS             extends AudioCodec(avcodec.AV_CODEC_ID_DTS)
    case object VORBIS          extends AudioCodec(avcodec.AV_CODEC_ID_VORBIS)
    case object DVAUDIO         extends AudioCodec(avcodec.AV_CODEC_ID_DVAUDIO)
    case object WMAV1           extends AudioCodec(avcodec.AV_CODEC_ID_WMAV1)
    case object WMAV2           extends AudioCodec(avcodec.AV_CODEC_ID_WMAV2)
    case object MACE3           extends AudioCodec(avcodec.AV_CODEC_ID_MACE3)
    case object MACE6           extends AudioCodec(avcodec.AV_CODEC_ID_MACE6)
    case object VMDAUDIO        extends AudioCodec(avcodec.AV_CODEC_ID_VMDAUDIO)
    case object FLAC            extends AudioCodec(avcodec.AV_CODEC_ID_FLAC)
    case object MP3ADU          extends AudioCodec(avcodec.AV_CODEC_ID_MP3ADU)
    case object MP3ON4          extends AudioCodec(avcodec.AV_CODEC_ID_MP3ON4)
    case object SHORTEN         extends AudioCodec(avcodec.AV_CODEC_ID_SHORTEN)
    case object ALAC            extends AudioCodec(avcodec.AV_CODEC_ID_ALAC)
    case object WESTWOOD_SND1   extends AudioCodec(avcodec.AV_CODEC_ID_WESTWOOD_SND1)
    case object GSM             extends AudioCodec(avcodec.AV_CODEC_ID_GSM)
    case object QDM2            extends AudioCodec(avcodec.AV_CODEC_ID_QDM2)
    case object COOK            extends AudioCodec(avcodec.AV_CODEC_ID_COOK)
    case object TRUESPEECH      extends AudioCodec(avcodec.AV_CODEC_ID_TRUESPEECH)
    case object TTA             extends AudioCodec(avcodec.AV_CODEC_ID_TTA)
    case object SMACKAUDIO      extends AudioCodec(avcodec.AV_CODEC_ID_SMACKAUDIO)
    case object QCELP           extends AudioCodec(avcodec.AV_CODEC_ID_QCELP)
    case object WAVPACK         extends AudioCodec(avcodec.AV_CODEC_ID_WAVPACK)
    case object DSICINAUDIO     extends AudioCodec(avcodec.AV_CODEC_ID_DSICINAUDIO)
    case object IMC             extends AudioCodec(avcodec.AV_CODEC_ID_IMC)
    case object MUSEPACK7       extends AudioCodec(avcodec.AV_CODEC_ID_MUSEPACK7)
    case object MLP             extends AudioCodec(avcodec.AV_CODEC_ID_MLP)
    case object GSM_MS          extends AudioCodec(avcodec.AV_CODEC_ID_GSM_MS)
    case object ATRAC3          extends AudioCodec(avcodec.AV_CODEC_ID_ATRAC3)
    case object APE             extends AudioCodec(avcodec.AV_CODEC_ID_APE)
    case object NELLYMOSER      extends AudioCodec(avcodec.AV_CODEC_ID_NELLYMOSER)
    case object MUSEPACK8       extends AudioCodec(avcodec.AV_CODEC_ID_MUSEPACK8)
    case object SPEEX           extends AudioCodec(avcodec.AV_CODEC_ID_SPEEX)
    case object WMAVOICE        extends AudioCodec(avcodec.AV_CODEC_ID_WMAVOICE)
    case object WMAPRO          extends AudioCodec(avcodec.AV_CODEC_ID_WMAPRO)
    case object WMALOSSLESS     extends AudioCodec(avcodec.AV_CODEC_ID_WMALOSSLESS)
    case object ATRAC3P         extends AudioCodec(avcodec.AV_CODEC_ID_ATRAC3P)
    case object EAC3            extends AudioCodec(avcodec.AV_CODEC_ID_EAC3)
    case object SIPR            extends AudioCodec(avcodec.AV_CODEC_ID_SIPR)
    case object MP1             extends AudioCodec(avcodec.AV_CODEC_ID_MP1)
    case object TWINVQ          extends AudioCodec(avcodec.AV_CODEC_ID_TWINVQ)
    case object TRUEHD          extends AudioCodec(avcodec.AV_CODEC_ID_TRUEHD)
    case object MP4ALS          extends AudioCodec(avcodec.AV_CODEC_ID_MP4ALS)
    case object ATRAC1          extends AudioCodec(avcodec.AV_CODEC_ID_ATRAC1)
    case object BINKAUDIO_RDFT  extends AudioCodec(avcodec.AV_CODEC_ID_BINKAUDIO_RDFT)
    case object BINKAUDIO_DCT   extends AudioCodec(avcodec.AV_CODEC_ID_BINKAUDIO_DCT)
    case object AAC_LATM        extends AudioCodec(avcodec.AV_CODEC_ID_AAC_LATM)
    case object QDMC            extends AudioCodec(avcodec.AV_CODEC_ID_QDMC)
    case object CELT            extends AudioCodec(avcodec.AV_CODEC_ID_CELT)
    case object G723_1          extends AudioCodec(avcodec.AV_CODEC_ID_G723_1)
    case object G729            extends AudioCodec(avcodec.AV_CODEC_ID_G729)
    case object _8SVX_EXP       extends AudioCodec(avcodec.AV_CODEC_ID_8SVX_EXP)
    case object _8SVX_FIB       extends AudioCodec(avcodec.AV_CODEC_ID_8SVX_FIB)
    case object BMV_AUDIO       extends AudioCodec(avcodec.AV_CODEC_ID_BMV_AUDIO)
    case object RALF            extends AudioCodec(avcodec.AV_CODEC_ID_RALF)
    case object IAC             extends AudioCodec(avcodec.AV_CODEC_ID_IAC)
    case object ILBC            extends AudioCodec(avcodec.AV_CODEC_ID_ILBC)
    case object OPUS            extends AudioCodec(avcodec.AV_CODEC_ID_OPUS)
    case object COMFORT_NOISE   extends AudioCodec(avcodec.AV_CODEC_ID_COMFORT_NOISE)
    case object TAK             extends AudioCodec(avcodec.AV_CODEC_ID_TAK)
    case object METASOUND       extends AudioCodec(avcodec.AV_CODEC_ID_METASOUND)
    case object PAF_AUDIO       extends AudioCodec(avcodec.AV_CODEC_ID_PAF_AUDIO)
    case object ON2AVC          extends AudioCodec(avcodec.AV_CODEC_ID_ON2AVC)
    case object DSS_SP          extends AudioCodec(avcodec.AV_CODEC_ID_DSS_SP)
    case object CODEC2          extends AudioCodec(avcodec.AV_CODEC_ID_CODEC2)
    case object FFWAVESYNTH     extends AudioCodec(avcodec.AV_CODEC_ID_FFWAVESYNTH)
    case object SONIC           extends AudioCodec(avcodec.AV_CODEC_ID_SONIC)
    case object SONIC_LS        extends AudioCodec(avcodec.AV_CODEC_ID_SONIC_LS)
    case object EVRC            extends AudioCodec(avcodec.AV_CODEC_ID_EVRC)
    case object SMV             extends AudioCodec(avcodec.AV_CODEC_ID_SMV)
    case object DSD_LSBF        extends AudioCodec(avcodec.AV_CODEC_ID_DSD_LSBF)
    case object DSD_MSBF        extends AudioCodec(avcodec.AV_CODEC_ID_DSD_MSBF)
    case object DSD_LSBF_PLANAR extends AudioCodec(avcodec.AV_CODEC_ID_DSD_LSBF_PLANAR)
    case object DSD_MSBF_PLANAR extends AudioCodec(avcodec.AV_CODEC_ID_DSD_MSBF_PLANAR)
    case object _4GV            extends AudioCodec(avcodec.AV_CODEC_ID_4GV)
    case object INTERPLAY_ACM   extends AudioCodec(avcodec.AV_CODEC_ID_INTERPLAY_ACM)
    case object XMA1            extends AudioCodec(avcodec.AV_CODEC_ID_XMA1)
    case object XMA2            extends AudioCodec(avcodec.AV_CODEC_ID_XMA2)
    case object DST             extends AudioCodec(avcodec.AV_CODEC_ID_DST)
    case object ATRAC3AL        extends AudioCodec(avcodec.AV_CODEC_ID_ATRAC3AL)
    case object ATRAC3PAL       extends AudioCodec(avcodec.AV_CODEC_ID_ATRAC3PAL)
    case object DOLBY_E         extends AudioCodec(avcodec.AV_CODEC_ID_DOLBY_E)
    case object APTX            extends AudioCodec(avcodec.AV_CODEC_ID_APTX)
    case object APTX_HD         extends AudioCodec(avcodec.AV_CODEC_ID_APTX_HD)
    case object SBC             extends AudioCodec(avcodec.AV_CODEC_ID_SBC)
    case object ATRAC9          extends AudioCodec(avcodec.AV_CODEC_ID_ATRAC9)
    case object HCOM            extends AudioCodec(avcodec.AV_CODEC_ID_HCOM)
    case object ACELP_KELVIN    extends AudioCodec(avcodec.AV_CODEC_ID_ACELP_KELVIN)
    case object MPEGH_3D_AUDIO  extends AudioCodec(avcodec.AV_CODEC_ID_MPEGH_3D_AUDIO)
    case object SIREN           extends AudioCodec(avcodec.AV_CODEC_ID_SIREN)
    case object HCA             extends AudioCodec(avcodec.AV_CODEC_ID_HCA)
    case object FASTAUDIO       extends AudioCodec(avcodec.AV_CODEC_ID_FASTAUDIO)
    case object MSNSIREN        extends AudioCodec(avcodec.AV_CODEC_ID_MSNSIREN)
    case object DFPWM           extends AudioCodec(avcodec.AV_CODEC_ID_DFPWM)
    case object BONK            extends AudioCodec(avcodec.AV_CODEC_ID_BONK)
    case object MISC4           extends AudioCodec(avcodec.AV_CODEC_ID_MISC4)
    case object APAC            extends AudioCodec(avcodec.AV_CODEC_ID_APAC)
    case object FTR             extends AudioCodec(avcodec.AV_CODEC_ID_FTR)
    case object WAVARC          extends AudioCodec(avcodec.AV_CODEC_ID_WAVARC)
    case object RKA             extends AudioCodec(avcodec.AV_CODEC_ID_RKA)
  }

}
