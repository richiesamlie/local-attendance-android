package com.localattendance.client.di

import android.content.Context
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.api.AuthEvents
import com.localattendance.client.data.api.AuthInterceptor
import com.localattendance.client.data.api.CookieInterceptor
import com.localattendance.client.data.api.DynamicBaseUrlInterceptor
import com.localattendance.client.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideCookieInterceptor(@ApplicationContext context: Context): CookieInterceptor = CookieInterceptor(context)

    @Provides
    @Singleton
    fun provideDynamicBaseUrlInterceptor(settingsRepository: SettingsRepository): DynamicBaseUrlInterceptor {
        return DynamicBaseUrlInterceptor(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideAuthEvents(): AuthEvents {
        return AuthEvents()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authEvents: AuthEvents,
        @ApplicationContext context: Context
    ): AuthInterceptor {
        return AuthInterceptor(authEvents, context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
        cookieInterceptor: CookieInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(dynamicBaseUrlInterceptor)
            .addInterceptor(cookieInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAttendanceApi(
        okHttpClient: okhttp3.OkHttpClient
    ): AttendanceApi {
        return Retrofit.Builder()
            .baseUrl("http://placeholder.com/") // Now handled by DynamicBaseUrlInterceptor
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AttendanceApi::class.java)
    }
}
