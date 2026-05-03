package com.mctb.autoreply.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        AuditEntry::class,
        ContactMessage::class,
        AnalyticsEntry::class,
        ConversationEntity::class,
        ConversationMessageEntity::class,
        VipContactEntity::class,
        LeadCardEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun auditDao(): AuditDao
    abstract fun contactDao(): ContactDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun conversationDao(): ConversationDao
    abstract fun vipContactDao(): VipContactDao
    abstract fun leadCardDao(): LeadCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `contact_messages` (`phoneNumber` TEXT NOT NULL, `customMessage` TEXT NOT NULL, `contactName` TEXT, `lastModifiedAt` INTEGER NOT NULL, PRIMARY KEY(`phoneNumber`))")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `analytics_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `phoneNumber` TEXT NOT NULL, `status` TEXT NOT NULL, `failureReason` TEXT, `responseDelayMs` INTEGER NOT NULL, `subscriptionTier` TEXT NOT NULL)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ai_conversations` " +
                        "(`phoneNumber` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, " +
                        "`lastMessageAt` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, " +
                        "`exchangeCount` INTEGER NOT NULL, PRIMARY KEY(`phoneNumber`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ai_conversation_messages` " +
                        "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`phoneNumber` TEXT NOT NULL, `role` TEXT NOT NULL, " +
                        "`content` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `vip_contacts` " +
                        "(`phoneNumber` TEXT NOT NULL, `contactName` TEXT NOT NULL, " +
                        "`nickname` TEXT NOT NULL, `messageType` TEXT NOT NULL, " +
                        "`customMessage` TEXT NOT NULL DEFAULT '', PRIMARY KEY(`phoneNumber`))"
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `lead_cards` " +
                        "(`phoneNumber` TEXT NOT NULL, `contactName` TEXT, " +
                        "`serviceNeeded` TEXT, `city` TEXT, " +
                        "`urgencyLevel` TEXT NOT NULL DEFAULT 'normal', " +
                        "`status` TEXT NOT NULL DEFAULT 'new', " +
                        "`notes` TEXT NOT NULL DEFAULT '', " +
                        "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "`calledBackAt` INTEGER, PRIMARY KEY(`phoneNumber`))"
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE lead_cards ADD COLUMN leadScore INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
