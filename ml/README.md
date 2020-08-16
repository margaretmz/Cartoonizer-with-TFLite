This directory contains the resources relevant to the machine learning aspect of the project. 

## About the files

```
├── CartoonGAN+ESRGAN.ipynb (an experiment combining two different vision models to see if they produce better quality images)
├── CartoonGAN_TFLite.ipynb (exports TFLite models with dynamic shape support)
├── CartoonGAN_TFLite_Fixed_Shaped.ipynb (this is the main TFLite model export notebook and it exports models with fixed shape)
└── metadata
    └── Add_Metadata.ipynb (shows how to populate the TFLite models with relevant metadata) 
```

## TFLite models

Available on [TensorFlow Hub](https://tfhub.dev/sayakpaul/lite-model/cartoongan/dr/1) in three different formats - 

- Dynamic-range
- Integer
- float16
