package rahul.jagtap.dmas.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun String.convertStringToArrayList(): ArrayList<String> {
    return ArrayList(this.split(",").map { it.trim() })
}

fun String.addToCommaSeparatedString(newElement: String): String {
    return if (this.isEmpty()) {
        newElement
    } else {
        "$this, $newElement"
    }
}

fun String.copyToClipboard(context: Context) {
    val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("label",this)
    clipBoard.setPrimaryClip(clipData)
    context.toast("Copied")
}