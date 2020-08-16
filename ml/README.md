This directory contains the resources relevant to the machine learning aspect of the project. Feel free to follow along the notebooks to learn how to save/convert the models and add metadata, or download the TensorFlow Lite models from TensorFlow Hub.

## Colab notebooks

```
├── CartoonGAN+ESRGAN.ipynb (an experiment combining two different vision models to see if they produce better quality images)
├── CartoonGAN_TFLite.ipynb (exports TFLite models with dynamic shape support)
├── CartoonGAN_TFLite_Fixed_Shaped.ipynb (this is the main TFLite model export notebook and it exports models with fixed shape)
└── metadata
    └── Add_Metadata.ipynb (shows how to populate the TFLite models with relevant metadata) 
``` 

## TFLite models (with metadata)

The white-box CartooGAN TensorFlow Lite models (with metatdata) are available on [TensorFlow Hub](https://tfhub.dev/sayakpaul/lite-model/cartoongan/dr/1) in three different formats:

- Dynamic-range
- Integer
- float16

## Misc
This [issue](https://github.com/ml-gde/e2e-tflite-tutorials/issues/9) contains an informative thread on the conversion process of the original white-box CartoonGAN TensorFlow Lite models. 

