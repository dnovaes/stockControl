package com.dnovaes.stockcontrol.common.di.data

import com.apollographql.apollo3.ApolloClient
import com.dnovaes.stockcontrol.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CommonDataModule {

    @Singleton
    @Provides
    fun provideApolloClient(): ApolloClient {
        val graphQlStockURL = "${BuildConfig.BASE_API_URL}/query"
        return ApolloClient.Builder()
            .serverUrl(graphQlStockURL)
            .build()
    }

}