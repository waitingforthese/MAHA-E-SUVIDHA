package rahul.jagtap.dmas.extensions

import android.view.View
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*


fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.animateGone(duration: Long = 100) {
    animate().setDuration(duration)
            .alpha(0f)
            .withEndAction {
                visibility = View.GONE
            }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.animateVisible(duration: Long = 100) {
    alpha = 0f
    visibility = View.VISIBLE
    animate().setDuration(duration)
            .alpha(1f)
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.animateInvisible(duration: Long = 100) {
    animate().setDuration(duration)
            .alpha(0f)
            .withEndAction {
                visibility = View.INVISIBLE
            }
}

fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.visibleOrInvisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun View.isVisible() : Boolean {
    return visibility == View.VISIBLE
}

/* Edit Text */
fun EditText.text(): String = text.toString()

fun String.toDate(): Date{
    return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(this)
}
