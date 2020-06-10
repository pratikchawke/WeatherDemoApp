package com.pratik.weatherdemoapp.retrofit

import androidx.annotation.NonNull
import com.pratik.weatherdemoapp.AppConstants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RetrofitModule {

    @NonNull
    @Provides
    @Singleton
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @NonNull
    @Provides
    @Singleton
    fun createApiRequest(retrofit: Retrofit): ApiRequest {
        return retrofit.create(ApiRequest::class.java)
    }
}