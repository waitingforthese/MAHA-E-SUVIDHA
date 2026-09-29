package rahul.jagtap.dmas

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import rahul.jagtap.dmas.databinding.ActivityEditContactBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ContactConfig
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Admin-only screen to edit the Contact Us content stored at contact_us/admin. */
class EditContactActivity : BaseActivity() {
    private lateinit var binding: ActivityEditContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "Edit Contact Info"

        prefill(ContactConfig())
        loadConfig()
        binding.btnSave.setOnClickListener { save() }
    }

    private fun loadConfig() {
        binding.progressBar.visible()
        database.child(Utils.CONTACT_US_TABLE).child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (checkIfActivityDestroying()) return
                binding.progressBar.gone()
                snapshot.getValue(ContactConfig::class.java)?.let { prefill(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.gone()
            }
        })
    }

    private fun prefill(c: ContactConfig) {
        binding.etIntroTitle.setText(c.introTitle)
        binding.etIntroText.setText(c.introText)
        binding.etMarketingLabel.setText(c.marketingLabel)
        binding.etMarketingAddress.setText(c.marketingAddress)
        binding.etBackOfficeLabel.setText(c.backOfficeLabel)
        binding.etBackOfficeAddress.setText(c.backOfficeAddress)
        binding.etPhone1.setText(c.phone1)
        binding.etPhone2.setText(c.phone2)
        binding.etWhatsappNumber.setText(c.whatsappNumber)
        binding.etWhatsappLabel.setText(c.whatsappLabel)
        binding.etEmail.setText(c.email)
        binding.etWebsite.setText(c.website)
        binding.etYoutubeUrl.setText(c.youtubeUrl)
        binding.etInstagramUrl.setText(c.instagramUrl)
        binding.etFacebookUrl.setText(c.facebookUrl)
        binding.etTrustTitle.setText(c.trustTitle)
        binding.etTrustSubtitle.setText(c.trustSubtitle)
    }

    private fun save() {
        val email = binding.etEmail.text.toString().trim()
        val phone1 = binding.etPhone1.text.toString().trim()
        if (TextUtils.isEmpty(phone1)) {
            binding.etPhone1.error = "Enter Phone 1"
            binding.etPhone1.requestFocus()
            return
        }
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "Enter Email"
            binding.etEmail.requestFocus()
            return
        }

        val config = ContactConfig(
            introTitle = binding.etIntroTitle.text.toString().trim(),
            introText = binding.etIntroText.text.toString().trim(),
            marketingLabel = binding.etMarketingLabel.text.toString().trim(),
            marketingAddress = binding.etMarketingAddress.text.toString().trim(),
            backOfficeLabel = binding.etBackOfficeLabel.text.toString().trim(),
            backOfficeAddress = binding.etBackOfficeAddress.text.toString().trim(),
            phone1 = phone1,
            phone2 = binding.etPhone2.text.toString().trim(),
            whatsappNumber = binding.etWhatsappNumber.text.toString().trim(),
            whatsappLabel = binding.etWhatsappLabel.text.toString().trim(),
            email = email,
            website = binding.etWebsite.text.toString().trim(),
            youtubeUrl = binding.etYoutubeUrl.text.toString().trim(),
            instagramUrl = binding.etInstagramUrl.text.toString().trim(),
            facebookUrl = binding.etFacebookUrl.text.toString().trim(),
            trustTitle = binding.etTrustTitle.text.toString().trim(),
            trustSubtitle = binding.etTrustSubtitle.text.toString().trim(),
            updatedBy = app?.preferences?.loggedInUser?.name ?: "admin",
            timeStamp = System.currentTimeMillis(),
            updatedDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        )

        binding.progressBar.visible()
        val updates = hashMapOf<String, Any?>(
            "${Utils.CONTACT_US_TABLE}/admin" to config,
            "${Utils.CONTACT_US_TABLE}_backup/admin" to config
        )
        database.updateChildren(updates) { error: DatabaseError?, _: DatabaseReference? ->
            if (checkIfActivityDestroying()) return@updateChildren
            binding.progressBar.gone()
            if (error != null) {
                toast("Failed to save: ${error.message}")
            } else {
                toast("Contact info updated")
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
