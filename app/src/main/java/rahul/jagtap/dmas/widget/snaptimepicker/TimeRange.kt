package rahul.jagtap.dmas.widget.snaptimepicker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import rahul.jagtap.dmas.widget.snaptimepicker.TimeValue

@Parcelize
data class TimeRange(
    var start: TimeValue?,
    var end: TimeValue?
) : Parcelable
