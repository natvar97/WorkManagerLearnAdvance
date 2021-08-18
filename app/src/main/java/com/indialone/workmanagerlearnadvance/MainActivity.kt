package com.indialone.workmanagerlearnadvance

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import com.indialone.workmanagerlearnadvance.databinding.ActivityMainBinding
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private val workManager = WorkManager.getInstance(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.max = 100
        progressDialog.setTitle("Downloading...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)

        val constraints = Constraints
            .Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val requestBuilder = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(requestBuilder)
        workManager.getWorkInfoByIdLiveData(requestBuilder.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    val progress = workInfo.progress
                    val value = progress.getInt(Constants.Progress,0)

                    progressDialog.incrementProgressBy(value)
                    progressDialog.show()
                }
                if (workInfo.state.isFinished) {
                    progressDialog.dismiss()
                }
            }
    }

    /*
    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private fun createOneTimeTask() = OneTimeWorkRequest
        .Builder(DownloadWorker::class.java)
        .setConstraints(createConstraints())
        .build()

    private fun enqueueOneTimeWork() {
        workManager.enqueue(createOneTimeTask())
    }

    private fun createPeriodicTask() = PeriodicWorkRequest
        .Builder(
            DownloadWorker::class.java,
            Constants.PERIODIC_DURATION,
            TimeUnit.MILLISECONDS
        )
        .setConstraints(createConstraints())
        .build()

    private fun enqueuePeriodicWork() {
        workManager.enqueue(createPeriodicTask())
    }
     */

}