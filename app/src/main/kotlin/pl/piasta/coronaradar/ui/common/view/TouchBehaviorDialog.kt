package pl.piasta.coronaradar.ui.common.view

import android.content.Context
import android.view.MotionEvent
import androidx.annotation.NonNull
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import pl.piasta.coronaradar.ui.util.dispatchActionDownTouchEvent

class TouchBehaviorDialog(@NonNull ctx: Context, @StyleRes themeResId: Int) :
    AlertDialog(ctx, themeResId) {

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        window?.dispatchActionDownTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }
}