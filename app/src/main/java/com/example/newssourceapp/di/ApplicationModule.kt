package com.example.newssourceapp.di

import com.example.newssourceapp.data.Api
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://newsapi.org/v2/"

/**
 * This object is for providing various services, APIs and other class/object instances etc. which
 * are used multiple times throughout the project, while maintaining the proper structure of the app
 * and not instantiating them everywhere several times.
 *
 * The module is bound as a [SingletonComponent], meaning that it'd be created once at the start of
 * the app and it'll stay in memory until the application is fully closed.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    /**
     * Provider method to expose the [Api] to the other objects, so that they could consume it. This
     * method also creates instances of the [OkHttpClient], [Retrofit] and [Moshi] JSON converter.
     */
    @Provides
    @Singleton
    fun provideApi() : Api {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        val okHttpBuilder = OkHttpClient.Builder().addInterceptor(loggingInterceptor)

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpBuilder.build())
            .build()
            .create(Api::class.java)
    }

    /**
     * Provider method to expose the [FirebaseAnalytics] to the other objects, so that they could consume it.
     */
    @Provides
    @Singleton
    fun provideAnalytics() : FirebaseAnalytics {
        return Firebase.analytics
    }
}