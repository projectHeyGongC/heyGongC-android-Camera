package com.heyGongC.heyGongCCamera.data.source.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    @Singleton
    @Provides
    fun provideMasterDataStore(
        @ApplicationContext appContext: Context
    ): DataStoreManager = LocalDataStoreManager(appContext)
}