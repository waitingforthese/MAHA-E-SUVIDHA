package rahul.jagtap.dmas.widget.snaptimepicker.extension

import androidx.lifecycle.ViewModel
import rahul.jagtap.dmas.widget.snaptimepicker.extension.TimePickedEvent
import rahul.jagtap.dmas.widget.snaptimepicker.extension.TimePickedLiveData

class SnapTimePickerViewModel : ViewModel() {
    val timePickedEvent =
        TimePickedLiveData<TimePickedEvent>()

    fun onTimePicked(hour: Int, minute: Int) {
        timePickedEvent.value = TimePickedEvent(hour, minute)
    }
}
