package rahul.jagtap.dmas.widget.areditor.spans;

import android.text.style.ForegroundColorSpan;

import androidx.annotation.ColorInt;

public class AreForegroundColorSpan extends ForegroundColorSpan implements AreDynamicSpan {
    public AreForegroundColorSpan(@ColorInt int color) {
        super(color);
    }

    @Override
    public int getDynamicFeature() {
        return this.getForegroundColor();
    }
}
