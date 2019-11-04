package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import timber.log.Timber

class BlurWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val context = applicationContext
        makeStatusNotification("Blurring image", context)
        sleep()
        return try {
            val inputUri = inputData.getString(KEY_IMAGE_URI)
            if (TextUtils.isEmpty(inputUri)) {
                Timber.e("Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val picture = BitmapFactory.decodeStream(context.contentResolver.openInputStream(Uri.parse(inputUri)))
            val blurredPicture = blurBitmap(picture, context)
            val fileUri = writeBitmapToFile(context, blurredPicture)
            val outputData = workDataOf(KEY_IMAGE_URI to fileUri.toString())
            Result.success(outputData)
        }catch (e: Throwable) {
            Timber.e(e)
            Result.failure()
        }
    }

}