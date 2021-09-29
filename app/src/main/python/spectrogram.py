import librosa
import librosa.display
from matplotlib import pyplot as plt


def generate(audio):
    x, sr = librosa.load(audio, mono=True, sr=16000)
    figname = '/sdcard/Download/spec.png'
    plt.specgram(x, NFFT=2048, Fs=2, Fc=0, noverlap=128, cmap='inferno', sides='default',
                 mode='default', scale='dB')
    plt.axis('off')
    plt.savefig(figname)
