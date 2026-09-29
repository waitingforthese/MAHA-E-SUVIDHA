package rahul.jagtap.dmas.widget.areditor.styles.toolbar;

import android.content.Intent;

import java.util.List;

import rahul.jagtap.dmas.widget.areditor.AREditText;
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.IARE_ToolItem;

/**
 * Created by wliu on 13/08/2018.
 */

public interface IARE_Toolbar {

    public void addToolbarItem(IARE_ToolItem toolbarItem);

    public List<IARE_ToolItem> getToolItems();

    public void setEditText(AREditText editText);

    public AREditText getEditText();

    public void onActivityResult(int requestCode, int resultCode, Intent data);
}
