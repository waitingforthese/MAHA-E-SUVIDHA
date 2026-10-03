package rahul.jagtap.dmas.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import rahul.jagtap.dmas.*
import rahul.jagtap.dmas.databinding.ActivityOnlineESuvidhaOptionsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.Utils

class OnlineESuvidhaOptionsActivity : BaseActivity() {
    private val TAG = OnlineESuvidhaOptionsActivity::class.java.simpleName
    lateinit var binding: ActivityOnlineESuvidhaOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineESuvidhaOptionsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "पेमेंट / संपर्क"
        binding.llButtons.visible()

        // Disable all E-Suvidha service options. Keep payment and WhatsApp contact.
        listOf(
            binding.btnPanCard,
            binding.btnShopAct,
            binding.btnUdyamAadhar,
            binding.btnFoodLicense,
            binding.btnProvidentFund,
            binding.btnManualAadharPanCard,
            binding.btnNepalMoneyTransfer,
            binding.btnRailwayTicketBooking,
            binding.btnBusinessPanCard,
            binding.tvElectionCard,
            binding.tvCibilReport,
            binding.tvDrivingLearningLicence,
            binding.tvPoliceVerification,
            binding.tvGstReg,
            binding.tvAadharCardUpdate,
            binding.tvEditPanOrAadharCard,
            binding.tvPassport,
            binding.tvJanmaKundli,
            binding.tvAadharPanLink,
            binding.tvGazzet,
            binding.tvFreeCreditCard,
            binding.btnTalukaSetuSuvidha,
            binding.tvAllGovtCards,
            binding.tvFarmerPolicy,
            binding.tvDematAccount,
            binding.tvGovtScheme,
            binding.tvProjectReport,
            binding.tvItrReturn,
            binding.tvTdsReturn,
            binding.tvGstReturn,
            binding.tvPrimarySchoolWork,
            binding.tvSelfHelpGroupWork,
            binding.imageView1
        ).forEach { it?.gone() }

        binding.btnPay?.setOnClickListener {
            payUsingUpi("100", "9552064906@upi", "Mayur Gangurde", "Payment")
        }

        binding.btnContact?.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/919552789899?text")
                )
            )
        }
    }

    internal val UPI_PAYMENT = 0

    fun payUsingUpi(amount: String, upiId: String, name: String, note: String) {

        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()


        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show()
        }

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
