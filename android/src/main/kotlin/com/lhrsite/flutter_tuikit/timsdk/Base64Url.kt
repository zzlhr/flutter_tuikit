package com.lhrsite.flutter_tuikit.timsdk


import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException

object Base64Url {
    //int base64_encode_url(const unsigned char *in_str, int length, char *out_str,int *ret_length)
    fun base64EncodeUrl(in_str: ByteArray): ByteArray {
        val base64 = Base64.encode(in_str)
        for (i in base64.indices)
            when (base64[i]) {
                '+'.toByte() -> base64[i] = '*'.toByte()
                '/'.toByte() -> base64[i] = '-'.toByte()
                '='.toByte() -> base64[i] = '_'.toByte()
                else -> {
                }
            }
        return base64
    }

    //int base64_decode_url(const unsigned char *in_str, int length, char *out_str, int *ret_length)
    @Throws(DecoderException::class)
    fun base64DecodeUrl(in_str: ByteArray): ByteArray {
        val base64 = in_str.clone()
        for (i in base64.indices)
            when (base64[i]) {
                '*'.toByte() -> base64[i] = '+'.toByte()
                '-'.toByte() -> base64[i] = '/'.toByte()
                '_'.toByte() -> base64[i] = '='.toByte()
                else -> {
                }
            }
        return Base64.decode(base64)
    }
}
