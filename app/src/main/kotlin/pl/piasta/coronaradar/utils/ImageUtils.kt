package pl.piasta.coronaradar.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File

object ImageUtils {

    @JvmStatic
    fun loadImage(file: File): Bitmap? {
        Log.d(javaClass.name, "Image file $file loaded " + Thread.currentThread())
        return when (file.isFile) {
            true -> BitmapFactory.decodeFile(file.absolutePath)
            false -> null
        }
    }

    @JvmStatic
    fun generateSpectrogram(file: File, context: Context) {
        Log.d(
            javaClass.name,
            "Generating spectrogram for audio file $file " + Thread.currentThread()
        )
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
        val python = Python.getInstance()
        val script = python.getModule("spectrogram")
        script.callAttr("generate", file.absolutePath)
    }
}
