package com.develofer.languagedecoder

import com.develofer.languagedecoder.model.DetectionResponse
import com.develofer.languagedecoder.model.Language
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/0.2/languages")
    suspend fun getLanguages(): Response<List<Language>>

    @Headers("Authorization: Bearer 6b91e3482851359e3c4cf2e63e06ac75")
    @FormUrlEncoded
    @POST("/0.2/detect")
    suspend fun getTextLanguage(@Field("q") text:String): Response<DetectionResponse>
}