package com.rvirin.onvif.onvifcamera

import android.util.Log
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import java.security.MessageDigest

class OnvifDigestInformation(val username: String, val password: String, val uri: String, val digestHeader: String) {
    var realm: String = ""
    var nonce: String = ""

    var authorizationHeader: String? = ""
        get() {
            extractDigest()
            val a1 = md5("$username:$realm:$password")
            val a2 = md5("POST:$uri")
            val response = md5("$a1:$nonce:$a2")
            return "Digest username=\"$username\", realm=\"$realm\", nonce=\"$nonce\", uri=\"$uri\", response=\"$response\""
        }

    private fun md5(string: String): String? {
        val HEX_CHARS = "0123456789abcdef"
        val bytes = MessageDigest.getInstance(MessageDigestAlgorithms.MD5)
                .digest(string.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }

    private fun extractDigest() {
        val authFields = splitAuthFields(digestHeader.substring(7))
        realm = authFields["realm"] ?: "realm"
        nonce = authFields["nonce"] ?: "nonce"
    }

    private fun splitAuthFields(authString: String): Map<String, String> {
        val fields = HashMap<String, String>()

        return authString.split(",").associate({
            val pair = it.split("=", limit = 2)
            val key = pair[0].trim()
            val value = pair[1].replace("\"", "").trim()
            Pair(key, value)
        })
    }
}