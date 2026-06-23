package com.ecodala.core.data.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object MultipartRequestFactory {
    fun text(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun optionalText(value: String?): RequestBody? {
        return value
            ?.takeIf { it.isNotBlank() }
            ?.let { text(it) }
    }

    fun imagePart(name: String, path: String?): MultipartBody.Part? {
        val file = path
            ?.takeIf { it.isNotBlank() }
            ?.let(::File)
            ?.takeIf { it.exists() && it.isFile }
            ?: return null

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }
}
