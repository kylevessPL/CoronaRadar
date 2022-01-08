import librosa
import librosa.display
import numpy as np
import os
import tflite_runtime.interpreter as tflite
from keras_preprocessing import image
from matplotlib import pyplot as plt


def generate_spectrogram(audio_path):
    img_path = os.path.dirname(audio_path) + '/spec.png'
    x, sr = librosa.load(audio_path, mono=True, sr=16000)
    s = librosa.feature.melspectrogram(x, sr=sr, n_mels=128)
    log_s = librosa.power_to_db(s, ref=np.max)
    plt.figure()
    librosa.display.specshow(log_s, sr=sr)
    plt.axis('off')
    plt.savefig(img_path, bbox_inches='tight', pad_inches=0)
    plt.clf()
    return img_path


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
    img_path = generate_spectrogram(audio_path)
    img = image.load_img(img_path, target_size=(640, 480, 3))
    y = image.img_to_array(img)
    y = y / 255.
    expanded = np.expand_dims(y, axis=0)
    test_image = np.vstack([expanded])
    prediction = predict(model_path, test_image)
    return prediction
