package com.mzm.sample.cartoongan

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

/**
 * Author: Margaret Maynard-Reid
 *
 * This is an Android sample app that showcases the following:
 *
 * 1. Jetpack navigation component - navigate between Fragments
 * 2. CameraX - permission check, camera setup, and image capture use case
 * 3. ML Model binding - easy import of .tflite model in Android Studio
 * 4. Transform a selfie image to a cartoon image with the whitebox_cartoon+_*.tflite model
 *
 * This MainActivity.kt is the main entry point into the sample app.
 * There is one single Activity with 3 Fragments:
 *
 * 1. PermissionsFragment.kt - check camera permission
 * 2. CameraFragment.kt - capture photo
 * 3. Selfie2CartoonFragment.kt - display the selfie & cartoon images
 */
class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }
}