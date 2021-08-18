package com.indialone.workmanagerlearnadvance

import android.content.Context
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.*
import java.lang.Exception
import java.net.URL
import java.util.*

class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val firstUpdate = workDataOf(Constants.Progress to 0)
        val lastUpdate = workDataOf(Constants.Progress to 100)
        val dwLink = "https://picsum.photos/200/300?grayscale"

        try {
            var count = 0
            val folderDir = Environment.getExternalStorageDirectory()

            var fileName = "image1"
            Log.d("download worker: directory", "$folderDir")

            fileName = "$folderDir/workManagerDemo/${UUID.randomUUID()}.jpg"

            val fileIcon = File(fileName)
            if (!fileIcon.exists()) {
                fileIcon.parentFile.mkdirs()
            }

            val url = URL(dwLink)
            val connection = url.openConnection()
            connection.connect()

            val fileLength = connection.contentLength
            val input = BufferedInputStream(url.openStream())
            val output = FileOutputStream(fileName)
            val data = ByteArray(1024)

            var total = 0
            setProgress(firstUpdate)

            while (input.read(data).also { count = it } != -1) {
                total += count
                val p = (total.toDouble() / fileLength) * 100
                setProgress(workDataOf(Constants.Progress to p.toInt()))
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            setProgress(workDataOf(Constants.Progress to 100))

            return Result.success()
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.failure()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}