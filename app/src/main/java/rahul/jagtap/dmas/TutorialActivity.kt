package rahul.jagtap.dmas

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import rahul.jagtap.dmas.databinding.ActivityTutorialBinding
import rahul.jagtap.dmas.model.ImageDetails
import rahul.jagtap.dmas.utils.Utils


class TutorialActivity : BaseActivity() {
    lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_tutorial)
        setTalukaSuvidhaListImage()
    }

    private fun setTalukaSuvidhaListImage() {
        database.child(Utils.TALUKA_SUVIDHA_LIST_IMAGE_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageDetails = dataSnapshot.getValue(ImageDetails::class.java)
                if (imageDetails != null) {
                    if (!TextUtils.isEmpty(imageDetails.imageDownloadUrl)) mContext?.let { Glide.with(it).load(imageDetails.imageDownloadUrl).into(binding.imageView1) }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Utils.hideSoftKeyboard(this)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {}
}
