package rahul.jagtap.dmas.widget.areditor.strategies.defaults;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import rahul.jagtap.dmas.R;


public class DefaultProfileActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView;

    private String mUserName;
    private String mUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_profile);

        mImageView = this.findViewById(R.id.sample_image);
        mTextView = this.findViewById(R.id.sample_text);
        mUserName = this.getIntent().getStringExtra("userName");
        mUserKey = this.getIntent().getStringExtra("userKey");

    }

    @Override
    protected void onResume() {
        super.onResume();
        showProfile();
    }

    private void showProfile() {
        int key = Integer.parseInt(mUserKey);
        mImageView.setImageResource(key);
        mTextView.setText(mUserName);
    }
}
