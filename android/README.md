# Cartoonizer Android App

This is an Android sample app with the White-box CartoonGAN TensorFlow Lite models.   

Please read the detailed instructions on how to create the Android app application in this blog post (TODO: add link).

## TensorFlow Lite Model
There are three TensorFlow Lite Models included in the Android app and see the [ml](../ml/) README for details.  
Android Studio ML Model Binding was used to import these models into the Android project.

## Requirements
* Android Studio Preview Beta version - download [here](https://developer.android.com/studio/preview).
* Android device (with at least 3GB RAM) in developer mode with USB debugging enabled
* USB cable to connect an Android device to computer

## Build and run
* Clone the project repo:  
`git clone https://github.com/margaretmz/CartoonGAN-e2e-tflite-tutorial.git`  
* Open the Android code android/selfie2anime in Android Studio.
* Connect your Android device to computer then click on `"Run -> Run 'app'`.
* Once the app is launched on device, grant camera permission.
* Take a selfie and wait for the TensorFlow Lite model to process the selfie. 
* You will then see a screen with both the selfie and anime image.
