package com.develofer.languagedecoder2

import com.develofer.languagedecoder2.model.Language
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/0.2/languages")
    suspend fun getLanguages(): Response<List<Language>>

}