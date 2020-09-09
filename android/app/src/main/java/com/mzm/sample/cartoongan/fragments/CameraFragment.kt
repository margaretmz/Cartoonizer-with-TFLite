package com.mzm.sample.cartoongan.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mzm.sample.cartoongan.ImageUtils
import com.mzm.sample.cartoongan.MainActivity.Companion.getOutputDirectory
import com.mzm.sample.cartoongan.R
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass that captures and saves a photo with CameraX
 */
class CameraFragment : Fragment() {

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var bitmap: Bitmap? = null
    private var modelSpinner: Spinner? = null
    private var modelType: Int = 0
    private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create a dropdown list to select a TensorFlow Lite model
        modelSpinner = view.findViewById(R.id.model_spinner)

        // Create adapter
        val modelAdapter = ArrayAdapter.createFromResource(
            requireContext(),                         // context
            R.array.tflite_models,                  // dropdown tflite model list
            R.layout.spinner_item
        )   // spinner layout

        // Set Dropdown resource layout
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set Adapter
        model_spinner.adapter = modelAdapter

        // Set on item selected listener
        model_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                modelType = position
//                val text: String = parent?.getItemAtPosition(position).toString()
//                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
                modelType = 0
            }
        }

        // Set camera switch
        camera_switch_button.setOnClickListener {
            lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            // Re-bind use cases to update selected camera
            startCamera()
        }


        // Setup the listener for take photo button
        camera_capture_button.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory(requireContext())

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        val screenAspectRatio = 1.0 / 1.0
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // set up Preview
            preview = Preview.Builder()
                .build()

            // set up Capture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(Surface.ROTATION_270)
                .setTargetRotation(viewFinder.display.rotation)
                .setTargetResolution(Size(512, 512)) // Margaret set target resolution to 512x512
                .build()

            // Select front camera as default for selfie
            val cameraSelector =
//                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                CameraSelector.Builder().requireLensFacing(lensFacing)
                    .build() //Margaret change camera to front facing

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    // Get rotation degree
                    val degrees: Int = rotationDegrees(photoFile)

                    // Create a bitmap from the .jpg image
                    bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    // Rotate image if needed
                    if (degrees != 0) {
                        bitmap = rotateBitmap(bitmap!!, degrees)
                    }

                    // Save bitmap image
                    val filePath = ImageUtils.saveBitmap(bitmap, photoFile)

                    // Pass the file path to the next screen
                    val action =
                        CameraFragmentDirections.actionCameraToSelfie2cartoon(filePath, modelType)
                    findNavController().navigate(action)

                }
            })
    }

    /**
     * Get rotation degree from image exif
     */
    private fun rotationDegrees(file: File): Int {
        val ei = ExifInterface(file.absolutePath);
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        // Return rotation degree based on orientation from exif
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {

        val rotationMatrix = Matrix()
        rotationMatrix.postRotate(rotationDegrees.toFloat())
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotationMatrix, true)
        bitmap.recycle()

        return rotatedBitmap
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still granted
        if (!PermissionsFragment.allPermissionsGranted(
                requireContext()
            )
        ) {
            findNavController().navigate(R.id.action_camera_to_permissions)
        }
    }

    companion object {
        private const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

}