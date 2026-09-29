package rahul.jagtap.dmas.widget.snaptimepicker

import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.databinding.LayoutSnapTimePickerNumberItemBinding

class TimeNumberViewHolder(
    private val binding: LayoutSnapTimePickerNumberItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun setNumber(number: String?) {
        binding.textViewNumber.text = number ?: "-"
    }
}
