package com.mzm.sample.cartoongan

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class ImageUtils {

    companion object {

        fun saveBitmap(bitmap: Bitmap?, file: File): String {

            try {
                val stream: OutputStream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return file.absolutePath

        }
    }

}