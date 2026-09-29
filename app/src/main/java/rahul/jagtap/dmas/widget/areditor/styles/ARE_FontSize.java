package rahul.jagtap.dmas.widget.areditor.styles;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import rahul.jagtap.dmas.widget.areditor.AREditText;
import rahul.jagtap.dmas.widget.areditor.Constants;
import rahul.jagtap.dmas.widget.areditor.Util;
import rahul.jagtap.dmas.widget.areditor.spans.AreFontSizeSpan;
import rahul.jagtap.dmas.widget.areditor.styles.toolbar.ARE_Toolbar;
import rahul.jagtap.dmas.widget.areditor.styles.windows.FontSizeChangeListener;
import rahul.jagtap.dmas.widget.areditor.styles.windows.FontsizePickerWindow;

public class ARE_FontSize extends ARE_ABS_Dynamic_Style<AreFontSizeSpan> implements FontSizeChangeListener {

//	public static boolean S_CHECKED = true;

	private ImageView mFontsizeImageView;

	private ARE_Toolbar mToolbar;

	private AREditText mEditText;

	private int mSize = Constants.DEFAULT_FONT_SIZE;

	private FontsizePickerWindow mFontPickerWindow;

	private static final int DEFAULT_FONT_SIZE = 18;

	private boolean mIsChecked;

	/**
	 * @param fontSizeImage
	 */
	public ARE_FontSize(ImageView fontSizeImage, ARE_Toolbar toolbar) {
		super(fontSizeImage.getContext());
		this.mToolbar = toolbar;
		this.mFontsizeImageView = fontSizeImage;
		setListenerForImageView(this.mFontsizeImageView);
	}


	/**
	 * @param editText
	 */
	public void setEditText(AREditText editText) {
		this.mEditText = editText;
	}

	@Override
	public EditText getEditText() {
		return mEditText;
	}

	@Override
	public void setListenerForImageView(final ImageView imageView) {
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			showFontsizePickerWindow();
			}
		});
	}

	private void showFontsizePickerWindow() {
		if (mFontPickerWindow == null) {
			mFontPickerWindow = new FontsizePickerWindow(mContext, this);
		}
		mFontPickerWindow.setFontSize(mSize);
		int yOff = 0 - Util.getPixelByDp(mContext, 150);
		mFontPickerWindow.showAsDropDown(mFontsizeImageView,0, yOff);
	}

	@Override
	protected void changeSpanInsideStyle(Editable editable, int start, int end, AreFontSizeSpan existingSpan) {
		int currentSize = existingSpan.getSize();
		if (currentSize != mSize) {
			applyNewStyle(editable, start, end, mSize);
		}
	}

	@Override
	public AreFontSizeSpan newSpan() {
		return new AreFontSizeSpan(mSize);
	}

	@Override
	public ImageView getImageView() {
		return this.mFontsizeImageView;
	}

	@Override
	public void setChecked(boolean isChecked) {
		// Do nothing.
	}

	@Override
	public boolean getIsChecked() {
		return mIsChecked;
	}

	@Override
	public void onFontSizeChange(int fontSize) {
		mIsChecked = true;
		mSize = fontSize;
		if (null != mEditText) {
			Editable editable = mEditText.getEditableText();
			int start = mEditText.getSelectionStart();
			int end = mEditText.getSelectionEnd();

			if (end > start) {
				applyNewStyle(editable, start, end, mSize);
			}
		}
	}

	@Override
	protected void featureChangedHook(int lastSpanFontSize) {
		mSize = lastSpanFontSize;
		if (mFontPickerWindow != null) {
			mFontPickerWindow.setFontSize(mSize);
		}
	}

	@Override
	protected AreFontSizeSpan newSpan(int size) {
		return new AreFontSizeSpan(size);
	}
}
