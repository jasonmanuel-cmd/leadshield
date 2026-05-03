package com.mctb.autoreply

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mctb.autoreply.worker.DailySummaryWorker
import com.mctb.autoreply.worker.FollowUpReminderWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MctbApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        FollowUpReminderWorker.schedule(this)
        DailySummaryWorker.schedule(this)
    }
}
