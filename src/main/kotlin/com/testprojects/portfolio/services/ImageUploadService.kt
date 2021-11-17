package com.testprojects.portfolio.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.multipart.MultipartFile
import java.lang.RuntimeException
import java.util.*

@Service
class ImageUploadService(
    @Value("\${imgur.clientId}") val clientId: String,
    @Value("\${imgur.clientSecret}") val clientSecret: String,
    @Value("\${imgur.albumHash}") val albumHash: String
) {
    fun uploadImage(image: MultipartFile): String {
        try {
            val client = OkHttpClient().newBuilder()
                .build()
            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", Base64.getEncoder().encodeToString(image.bytes))
                .build()
            val request = Request.Builder()
                .url("https://api.imgur.com/3/image")
                .method("POST", body)
                .addHeader("Authorization", "Client-ID $clientId")
                .build()
            val response = client.newCall(request).execute()
            val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            val imageUploadResponse = objectMapper.readValue(response.body?.string(), ImageUploadResponse::class.java)
            return imageUploadResponse.data!!.link!!
        } catch (ex: HttpStatusCodeException) {
            throw RuntimeException("Can not upload an image")
        }
    }
}

class ImageUploadResponse (
    val success: Boolean? = null,
    val status: Number? = null,
    val data: ImageUploadResponseData? = null
)

class ImageUploadResponseData (
    val id: String? = null,
    val link: String? = null
    // skip all other fields because they are not user in this case
)