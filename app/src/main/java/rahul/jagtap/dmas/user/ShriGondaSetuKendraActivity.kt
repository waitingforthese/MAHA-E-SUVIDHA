package rahul.jagtap.dmas.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import rahul.jagtap.dmas.*
import rahul.jagtap.dmas.databinding.ActivityShriGondaSetuKendraBinding
import rahul.jagtap.dmas.utils.Utils

class ShriGondaSetuKendraActivity : BaseActivity() {
    private val TAG = ShriGondaSetuKendraActivity::class.java.simpleName
    lateinit var binding: ActivityShriGondaSetuKendraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShriGondaSetuKendraBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "तालुका सुविधा केंद्र"
//        binding.tvBaramatiSetuKendra?.setOnClickListener {
//            startActivity(Intent(mContext, AllOtherDocsActivity::class.java))
//        }
//        binding.tvShirurSetuKendra?.setOnClickListener {
//            startActivity(Intent(mContext, AllOtherDocsActivity::class.java))
//        }
//        binding.tvDaundSetuKendra?.setOnClickListener {
//            startActivity(Intent(mContext, AllOtherDocsActivity::class.java))
//        }
        binding.tvShriGondaSetuKendra?.setOnClickListener {
            startActivity(Intent(mContext, AllOtherDocsActivity::class.java))
        }
//        binding.tvKarjatSetuKendra?.setOnClickListener {
//            startActivity(Intent(mContext, AllOtherDocsActivity::class.java))
//        }
//        binding.tvIndapurSetuKendra?.setOnClickListener {
//            startActivity(Intent(mContext, AllOtherDocsActivity::class.java))
//        }
        binding.tvIncomeCertificate?.setOnClickListener {
            startActivity(Intent(mContext, IncomeCertificateActivity::class.java))
        }
        binding.tvAgeNationality?.setOnClickListener {
            startActivity(Intent(mContext, AgeCertificateActivity::class.java))
        }
//        binding.tvOtherDocs?.setOnClickListener {
//            startActivity(Intent(mContext, AllOtherDocsActivity::class.java))
//        }
//        binding.tvGazzet?.setOnClickListener {
//            startActivity(Intent(mContext, GazzetActivity::class.java))
//        }
//        binding.tvPoliceVerification?.setOnClickListener {
//            startActivity(Intent(mContext, PoliceVerificationActivity::class.java))
//        }
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

    companion object {
    }

    fun openWhatsapp(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("https://wa.me/919552789899?text")))
    }
}
