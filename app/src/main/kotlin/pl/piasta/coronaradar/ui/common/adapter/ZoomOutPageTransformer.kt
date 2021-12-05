package pl.piasta.coronaradar.ui.common.adapter

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

class ZoomOutPageTransformer : ViewPager.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.9F
        private const val MIN_ALPHA = 0.5F
    }

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height

        when {
            position < -1 -> view.alpha = 0f
            position <= 1 -> {
                val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                val vertMargin = pageHeight * (1 - scaleFactor) / 2
                val horzMargin = pageWidth * (1 - scaleFactor) / 2
                position.takeIf { it < 0 }?.let {
                    view.translationX = horzMargin - vertMargin / 2
                } ?: run {
                    view.translationX = -horzMargin + vertMargin / 2
                }
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.alpha =
                    MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
            else -> view.alpha = 0F
        }
    }
}