# Android Sync Integration - Checklist & Code Files

This file serves as a quick reference for what was added to the Android app to enable LeadShield command center sync.

## New Files Created

### 1. LeadShieldSyncManager.kt
**Location**: `app/src/main/java/com/mctb/autoreply/data/LeadShieldSyncManager.kt`

**Purpose**: Manages all sync operations to the Next.js `/api/sync` endpoint.

**Key Methods**:
- `syncMissedCall(phoneNumber, contactName)` - Sync a single missed call event
- `syncLead(phoneNumber, contactName, service, city, urgency, status)` - Sync a lead
- `syncConversationMessage(phoneNumber, role, content)` - Sync a message
- `syncBatch(leads, messages, callEvents)` - Batch sync multiple items
- `syncMissedCallAsync()` / `syncLeadAsync()` / etc - Non-blocking versions

**Subscription Check**: Only syncs if customer has OPERATOR tier or above.

**Features**:
- ✅ Validates subscription tier
- ✅ Checks for valid sync token
- ✅ Validates authenticated user
- ✅ Handles network errors gracefully
- ✅ ISO timestamp formatting
- ✅ Hilt dependency injection

## Modified Files

### 2. AppPreferences.kt
**Location**: `app/src/main/java/com/mctb/autoreply/data/AppPreferences.kt`

**Changes Added**:
```kotlin
// Preference keys
private val KEY_LEADSHIELD_SYNC_TOKEN = stringPreferencesKey("leadshield_sync_token")
private val KEY_LEADSHIELD_SYNC_URL = stringPreferencesKey("leadshield_sync_url")

// Flows for reactive UI
val leadShieldSyncToken: Flow<String>
val leadShieldSyncUrl: Flow<String>

// Async methods
suspend fun saveLeadShieldSyncCredentials(token: String, url: String)
suspend fun clearLeadShieldSyncCredentials()
suspend fun getLeadShieldSyncTokenSync(): String
suspend fun getLeadShieldSyncUrlSync(): String
```

### 3. SupabaseApi.kt (Network APIs)
**Location**: `app/src/main/java/com/mctb/autoreply/data/network/SupabaseApi.kt`

**New Interface & DTOs Added**:
```kotlin
interface LeadShieldApi {
    @POST("api/sync")
    suspend fun syncToLeadShield(
        @Header("Authorization") authorization: String,
        @Body payload: LeadShieldSyncPayload
    ): Response<LeadShieldSyncResponse>
}

data class LeadShieldSyncPayload(
    val user_id: String,
    val leads: List<Map<String, Any?>>? = null,
    val conversation_messages: List<Map<String, Any?>>? = null,
    val call_events: List<Map<String, Any?>>? = null
)

data class LeadShieldSyncResponse(
    val ok: Boolean,
    val summary: LeadShieldSyncSummary?
)

data class LeadShieldSyncSummary(
    val leadsReceived: Int,
    val leadsCreated: Int,
    val leadsUpdated: Int,
    val messagesReceived: Int,
    val messagesInserted: Int,
    val messagesUpdated: Int,
    val callEventsReceived: Int,
    val callEventsInserted: Int,
    val callEventsUpdated: Int
)
```

### 4. CallReceiver.kt
**Location**: `app/src/main/java/com/mctb/autoreply/receiver/CallReceiver.kt`

**Changes**:
```kotlin
@Inject
lateinit var leadShieldSync: LeadShieldSyncManager

private fun handleMissedCall(phoneNumber: String) {
    val pendingResult = goAsync()
    try {
        receiverScope.launch {
            try {
                // 1. Send auto-reply SMS (existing)
                smsHandler.processMissedCall(phoneNumber)
                
                // 2. Sync to web dashboard (NEW)
                leadShieldSync.syncMissedCallAsync(phoneNumber, null)
            } finally {
                pendingResult.finish()
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error triggering SmsHandler or sync", e)
        pendingResult.finish()
    }
}
```

## Build Dependencies

The new code uses existing dependencies already in the project:

- **Retrofit2** - For HTTP requests (already used for Supabase API)
- **Hilt** - For dependency injection (already used)
- **Kotlin Coroutines** - For async operations (already used)
- **DataStore** - For preferences (already used)

No new libraries need to be added.

## Wiring the LeadShieldApi to Retrofit

If not already configured, add to your Retrofit builder (likely in a Module or Application setup):

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideLeadShieldApi(
        @Named("leadshield_url") baseUrl: String
    ): LeadShieldApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LeadShieldApi::class.java)
    }
    
    @Provides
    @Named("leadshield_url")
    fun provideLleadShieldBaseUrl(): String {
        // Get from preferences or BuildConfig
        return "https://crm.leadshield.io"
    }
}
```

Or add to existing Retrofit configuration where you already have SupabaseApi.

## Testing the Implementation

### Unit Tests (Mock the API)

```kotlin
@Test
fun testSyncMissedCall() = runBlocking {
    // Mock dependencies
    val mockApi = mock<LeadShieldApi>()
    val mockPrefs = mock<AppPreferences>()
    val mockSubscriptionMgr = mock<SubscriptionManager>()
    val mockAuthMgr = mock<SupabaseAuthManager>()
    
    // Setup returns
    whenever(mockPrefs.getLeadShieldSyncTokenSync()).thenReturn("test_token")
    whenever(mockAuthMgr.getCurrentUserIdSync()).thenReturn("user-123")
    whenever(mockSubscriptionMgr.subscriptionStatus)
        .thenReturn(flowOf(SubscriptionStatus(SubscriptionTier.OPERATOR, 0)))
    
    // Create manager under test
    val manager = LeadShieldSyncManager(
        mockApi, mockPrefs, mockSubscriptionMgr, mockAuthMgr
    )
    
    // Execute
    manager.syncMissedCall("+1234567890", "John Doe")
    
    // Verify API was called
    verify(mockApi).syncToLeadShield(
        authorization = "Bearer test_token",
        payload = any()
    )
}
```

### Integration Test (Real API)

1. **Generate test token** on your backend
2. **Store in prefs** manually in test setup
3. **Call syncMissedCall()** with test phone number
4. **Check web dashboard** for the new call event
5. **Verify timestamp** is recent and correct

### End-to-End Test

1. Upgrade a test account to Operator tier
2. Receive sync token via FCM or download link
3. Store token in app: `prefs.saveLeadShieldSyncCredentials(token, url)`
4. Simulate missed call (or make real call)
5. Monitor Android logs: `adb logcat | grep LeadShieldSyncManager`
6. Verify call appears in web dashboard in real-time

## Dependency Injection (Hilt) Setup

The manager is injected via Hilt. Ensure you have:

1. **Android Application class** annotated with `@HiltAndroidApp`
   ```kotlin
   @HiltAndroidApp
   class MctbApplication : Application()
   ```

2. **Network Module** that provides `LeadShieldApi` and `SupabaseRestApi`
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object NetworkModule {
       @Provides
       @Singleton
       fun provideLeadShieldApi(/* ... */): LeadShieldApi { /* ... */ }
   }
   ```

3. **Data Module** that provides `AppPreferences`, `SubscriptionManager`, `SupabaseAuthManager`
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object DataModule {
       @Provides
       @Singleton
       fun provideLeadShieldSyncManager(
           api: LeadShieldApi,
           prefs: AppPreferences,
           subscriptionManager: SubscriptionManager,
           authManager: SupabaseAuthManager
       ): LeadShieldSyncManager = LeadShieldSyncManager(api, prefs, subscriptionManager, authManager)
   }
   ```

## Logs to Monitor

When syncing is active, you'll see logs like:

```
D/LeadShieldSyncManager: Syncing missed call from +1234567890 to LeadShield
D/LeadShieldSyncManager: Sync successful for call from +1234567890

// Failures (should be graceful):
D/LeadShieldSyncManager: Skipping sync: Subscription tier below OPERATOR
D/LeadShieldSyncManager: Skipping sync: No LeadShield sync token configured
W/LeadShieldSyncManager: Sync failed: HTTP 401 - Unauthorized
E/LeadShieldSyncManager: Error syncing missed call (network timeout)
```

## Next Steps

1. **Integrate with payment flow**
   - When customer upgrades in Google Play Billing → Generate token backend
   - Call `prefs.saveLeadShieldSyncCredentials()` when token received

2. **Add to other contact events** (optional)
   - Call `syncLeadAsync()` when user creates a lead
   - Call `syncConversationMessageAsync()` when AI replies
   - Call `syncBatch()` during periodic backup sync

3. **Implement token refresh**
   - Add token expiration logic
   - Regenerate tokens periodically
   - Handle 401 responses (token expired)

4. **Add bandwidth optimization** (optional)
   - Batch sync every 5 minutes instead of real-time
   - Compress payload for slow networks
   - Skip sync if data usage is high

## Troubleshooting Build Issues

### Missing LeadShieldApi interface

**Error**: `Unresolved reference: LeadShieldApi`

**Fix**: Ensure SupabaseApi.kt changes are saved and Gradle sync completes

```bash
./gradlew clean build
```

### Missing LeadShieldSyncManager import

**Error**: `Cannot find LeadShieldSyncManager`

**Fix**: Ensure the new file is created at the correct path and IDE cache is refreshed

```bash
# In Android Studio: File → Invalidate Caches → Restart
# Or from command line:
./gradlew cleanBuildCache
```

### Hilt injection fails at runtime

**Error**: `MissingBindingException: LeadShieldSyncManager`

**Fix**: Ensure the Module is properly configured with `@Provides` or `@Binds`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SyncModule {
    @Provides
    @Singleton
    fun provideLeadShieldSyncManager(
        api: LeadShieldApi,
        prefs: AppPreferences,
        subscriptionManager: SubscriptionManager,
        authManager: SupabaseAuthManager
    ): LeadShieldSyncManager = LeadShieldSyncManager(api, prefs, subscriptionManager, authManager)
}
```

## Performance Notes

- **Sync is non-blocking**: Uses `goAsync()` + coroutines, doesn't block call handling
- **Graceful failures**: Network errors are logged but don't crash app
- **Minimal data**: ~1-5 KB per sync, minimal battery impact
- **Backoff**: No automatic retry (fails silently once)

If sync fails once, it won't retry until the next event. For reliability, implement:
- WorkManager periodic sync (every 5 minutes)
- Queue failed syncs for retry on next network connection
- Add exponential backoff for transient errors

