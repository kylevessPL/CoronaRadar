import librosa
import numpy as np
import os
import tflite_runtime.interpreter as tflite
from keras_preprocessing import image
from matplotlib import pyplot as plt


def predict(model_path, test_image):
    interpreter = tflite.Interpreter(model_path)
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    interpreter.set_tensor(input_details[0]["index"], test_image)
    interpreter.invoke()
    prediction = interpreter.get_tensor(output_details[0]["index"])
    return float(prediction)


def generate(audio_path, model_path):
    img_file = os.path.dirname(audio_path) + '/spec.png'
    x, sr = librosa.load(audio_path, mono=True, sr=16000)
    plt.specgram(x, NFFT=2048, Fs=2, Fc=0, noverlap=128, cmap='inferno', sides='default',
                 mode='default', scale='dB')
    plt.axis('off')
    plt.savefig(img_file)
    img = image.load_img(img_file, target_size=(640, 480, 3))
    y = image.img_to_array(img)
    y = y / 255.
    expanded = np.expand_dims(y, axis=0)
    test_image = np.vstack([expanded])
    prediction = predict(model_path, test_image)
    return prediction
