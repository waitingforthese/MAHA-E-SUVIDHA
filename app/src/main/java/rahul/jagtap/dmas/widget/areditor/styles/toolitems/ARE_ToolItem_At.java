package rahul.jagtap.dmas.widget.areditor.styles.toolitems;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import rahul.jagtap.dmas.widget.areditor.AREditText;
import rahul.jagtap.dmas.widget.areditor.models.AtItem;
import rahul.jagtap.dmas.widget.areditor.styles.IARE_Style;
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.styles.ARE_Style_At;

/**
 * Created by wliu on 13/08/2018.
 */

public class ARE_ToolItem_At extends ARE_ToolItem_Abstract {

    @Override
    public IARE_ToolItem_Updater getToolItemUpdater() {
        return null;
    }

    @Override
    public IARE_Style getStyle() {
        if (mStyle == null) {
            AREditText editText = this.getEditText();
            mStyle = new ARE_Style_At(editText);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        return null;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (ARE_Style_At.REQUEST_CODE == requestCode) {
                AtItem atItem = (AtItem) data.getSerializableExtra(ARE_Style_At.EXTRA_TAG);
                if (null == atItem) { return; }
                ((ARE_Style_At) this.getStyle()).insertAt(atItem);
            }
        }
    }
}
