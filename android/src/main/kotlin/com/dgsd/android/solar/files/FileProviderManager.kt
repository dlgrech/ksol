package com.dgsd.android.solar.files

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.dgsd.android.solar.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileProviderManager(
  private val context: Context
) {

  suspend fun save(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
    val imageFile = File.createTempFile("bitmap", ".png", context.cacheDir)
    imageFile.outputStream().use { fos ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }

    FileProvider.getUriForFile(context, PROVIDER_AUTHORITY, imageFile)
  }

  companion object {
    private val PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"
  }
}