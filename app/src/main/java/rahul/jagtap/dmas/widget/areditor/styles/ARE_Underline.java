package rahul.jagtap.dmas.widget.areditor.styles;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import rahul.jagtap.dmas.widget.areditor.AREditText;
import rahul.jagtap.dmas.widget.areditor.spans.AreUnderlineSpan;

public class ARE_Underline extends ARE_ABS_Style<AreUnderlineSpan> {

	private ImageView mUnderlineImageView;

	private boolean mUnderlineChecked;

	private AREditText mEditText;

	public ARE_Underline(ImageView imageView) {
		super(imageView.getContext());
		this.mUnderlineImageView = imageView;
		setListenerForImageView(this.mUnderlineImageView);
	}

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
				mUnderlineChecked = !mUnderlineChecked;
				ARE_Helper.updateCheckStatus(ARE_Underline.this, mUnderlineChecked);
				if (null != mEditText) {
					applyStyle(mEditText.getEditableText(), mEditText.getSelectionStart(), mEditText.getSelectionEnd());
				}
			}
		});
	}

	@Override
	public ImageView getImageView() {
		return this.mUnderlineImageView;
	}

	@Override
	public void setChecked(boolean isChecked) {
		this.mUnderlineChecked = isChecked;
	}

	@Override
	public boolean getIsChecked() {
		return this.mUnderlineChecked;
	}

	@Override
	public AreUnderlineSpan newSpan() {
		return new AreUnderlineSpan();
	}
}
