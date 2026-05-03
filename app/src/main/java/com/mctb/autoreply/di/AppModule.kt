package com.mctb.autoreply.di

import android.content.Context
import com.mctb.autoreply.BuildConfig
import com.mctb.autoreply.data.*
import com.mctb.autoreply.data.network.SupabaseAuthApi
import com.mctb.autoreply.data.network.SupabaseRestApi
import com.mctb.autoreply.service.CipherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.mctb.autoreply.data.network.NeonDataApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideNeonDataApi(client: OkHttpClient): NeonDataApi {
        val baseUrl = BuildConfig.NEON_DATA_API_URL.ifBlank { "https://example.invalid/" }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NeonDataApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseRestApi(client: OkHttpClient): SupabaseRestApi {
        return Retrofit.Builder()
            .baseUrl("https://expcinwdxxlfgkuxpirq.supabase.co/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseRestApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseAuthApi(client: OkHttpClient): SupabaseAuthApi {
        return Retrofit.Builder()
            .baseUrl("https://expcinwdxxlfgkuxpirq.supabase.co/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseSyncManager(
        api: SupabaseRestApi,
        prefs: AppPreferences
    ): SupabaseSyncManager {
        return SupabaseSyncManager(api, prefs)
    }

    @Provides
    @Singleton
    fun provideSupabaseAuthManager(
        authApi: SupabaseAuthApi,
        prefs: AppPreferences
    ): SupabaseAuthManager {
        return SupabaseAuthManager(authApi, prefs)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideCallHistoryManager(@ApplicationContext context: Context): CallHistoryManager {
        return CallHistoryManager(context)
    }

    @Provides
    @Singleton
    fun provideCipherService(): CipherService {
        return CipherService(BuildConfig.GEMINI_API_KEY)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideAuditDao(database: AppDatabase): AuditDao {
        return database.auditDao()
    }

    @Provides
    @Singleton
    fun provideContactDao(database: AppDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    @Singleton
    fun provideAnalyticsDao(database: AppDatabase): AnalyticsDao {
        return database.analyticsDao()
    }

    @Provides
    @Singleton
    fun provideConversationDao(database: AppDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideVipContactDao(database: AppDatabase): VipContactDao {
        return database.vipContactDao()
    }

    @Provides
    @Singleton
    fun provideLeadCardDao(database: AppDatabase): LeadCardDao {
        return database.leadCardDao()
    }

    @Provides
    @Singleton
    fun provideContactRoutingManager(database: AppDatabase): ContactRoutingManager {
        return ContactRoutingManager(database)
    }

    @Provides
    @Singleton
    fun provideAiConversationManager(
        prefs: AppPreferences,
        database: AppDatabase
    ): AiConversationManager {
        return AiConversationManager(prefs, database)
    }

    @Provides
    @Singleton
    fun provideAuditRepository(
        auditDao: AuditDao,
        prefs: AppPreferences,
        cloudSyncManager: CloudSyncManager
    ): AuditRepository {
        return AuditRepository(auditDao, prefs, cloudSyncManager)
    }

    @Provides
    @Singleton
    fun provideContactRepository(contactDao: ContactDao): ContactRepository {
        return ContactRepository(contactDao)
    }

    @Provides
    @Singleton
    fun provideBillingManager(@ApplicationContext context: Context): BillingManager {
        return BillingManager(context).apply { startConnection() }
    }

    @Provides
    @Singleton
    fun provideSubscriptionManager(
        @ApplicationContext context: Context,
        prefs: AppPreferences,
        billingManager: BillingManager
    ): SubscriptionManager {
        return SubscriptionManager(context, prefs, billingManager)
    }

    @Provides
    @Singleton
    fun provideCloudSyncManager(
        @ApplicationContext context: Context
    ): CloudSyncManager {
        return CloudSyncManager(context)
    }
}
