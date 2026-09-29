package rahul.jagtap.dmas.user

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.databinding.ActivityViewSuchnaBinding
import rahul.jagtap.dmas.utils.Utils

class ViewSuchnaActivity : BaseActivity() {
    private val TAG = ViewSuchnaActivity::class.java.simpleName
    lateinit var binding: ActivityViewSuchnaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewSuchnaBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = intent.getStringExtra("title") ?: ""
        binding.tvInstructions.fromHtml(intent.getStringExtra("suchna") ?: "")
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
