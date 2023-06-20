package com.example.dlsandroidredesign.data

import android.content.Context
import androidx.room.Room
import com.example.dlsandroidredesign.Converters
import com.example.dlsandroidredesign.data.local.DLSDatabase
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import com.example.dlsandroidredesign.data.remote.DLSService
import com.example.dlsandroidredesign.domain.DLSRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    //region remote
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val mHttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(mHttpLoggingInterceptor)
            .build()


        return Retrofit.Builder()
            .baseUrl("https://www.abadata.ca/Abadata2/api/mobileforms/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideDLSService(retrofit: Retrofit): DLSService {
        return retrofit.create(DLSService::class.java)
    }
    //endregion

    //region local
    @Singleton
    @Provides
    fun provideDLSDatabase(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        DLSDatabase::class.java,
        "contacts.db"
    ).addTypeConverter(Converters()).build()

    @Singleton
    @Provides
    fun provideImageLocationInfoDao(db: DLSDatabase) = db.imageLocationInfoDAO

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext appContext: Context): PreferencesDataStore {
        return PreferencesDataStore(appContext)
    }
    //endregion

    @Provides
    @Singleton
    fun provideDLSRepository(
        dlsRepositoryImpl: DLSRepositoryImpl
    ): DLSRepository = dlsRepositoryImpl
}
