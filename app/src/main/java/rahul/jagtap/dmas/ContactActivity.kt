package rahul.jagtap.dmas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import rahul.jagtap.dmas.databinding.ActivityContactUsBinding
import rahul.jagtap.dmas.model.ContactConfig
import rahul.jagtap.dmas.utils.Utils

class ContactActivity : BaseActivity() {
    lateinit var binding: ActivityContactUsBinding
    private var config: ContactConfig = ContactConfig()
    private val isAdmin get() = app?.preferences?.loggedInUser?.isAdmin == "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "Contact Us"

        bindConfig(config)
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadConfig()
    }

    private fun loadConfig() {
        database.child(Utils.CONTACT_US_TABLE).child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (checkIfActivityDestroying()) return
                snapshot.getValue(ContactConfig::class.java)?.let {
                    config = it
                    bindConfig(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun bindConfig(c: ContactConfig) {
        binding.tvIntroTitle.text = c.introTitle
        binding.tvIntroText.text = c.introText
        binding.tvMarketingLabel.text = c.marketingLabel
        binding.tvAddress1.text = c.marketingAddress
        binding.tvBackOfficeLabel.text = c.backOfficeLabel
        binding.tvAddress2.text = c.backOfficeAddress
        binding.tvPhone1.text = c.phone1
        binding.tvPhone2.text = c.phone2
        binding.tvWhatsappLabel.text = c.whatsappLabel
        binding.tvEmail1.text = c.email
        binding.tvWebsite.text = c.website
        binding.websiteSection.visibility = if (c.website.isNullOrBlank()) android.view.View.GONE else android.view.View.VISIBLE
        binding.tvTrustTitle.text = c.trustTitle
        binding.tvTrustSubtitle.text = c.trustSubtitle
    }

    private fun setClickListeners() {
        binding.rowMarketing.setOnClickListener { openMap(config.marketingAddress) }
        binding.rowBackOffice.setOnClickListener { openMap(config.backOfficeAddress) }

        val dialPhone1 = { dial(config.phone1) }
        binding.rowPhone1.setOnClickListener { dialPhone1() }
        binding.btnCall1.setOnClickListener { dialPhone1() }

        val dialPhone2 = { dial(config.phone2) }
        binding.rowPhone2.setOnClickListener { dialPhone2() }
        binding.btnCall2.setOnClickListener { dialPhone2() }

        binding.btnContact.setOnClickListener {
            config.whatsappNumber?.takeIf { it.isNotBlank() }?.let { openWhatsapp(it) }
        }

        binding.rowEmail.setOnClickListener { composeEmail(config.email) }
        binding.pillEmail.setOnClickListener { composeEmail(config.email) }

        binding.rowWebsite.setOnClickListener { openLink(normalizeUrl(config.website)) }

        binding.pillYoutube.setOnClickListener { openLink(config.youtubeUrl) }
        binding.pillInstagram.setOnClickListener { openLink(config.instagramUrl) }
        binding.pillFacebook.setOnClickListener { openLink(config.facebookUrl) }
    }

    private fun dial(phone: String?) {
        if (!phone.isNullOrBlank()) callPhone(phone)
    }

    private fun openMap(address: String?) {
        if (address.isNullOrBlank()) return
        openLink("http://maps.google.co.in/maps?q=$address")
    }

    private fun composeEmail(email: String?) {
        if (email.isNullOrBlank()) return
        // Use ACTION_SENDTO with a mailto: uri so only email apps match, and rely on
        // ActivityNotFoundException instead of resolveActivity() (which returns null on
        // API 30+ without a <queries> entry even when an email app is installed).
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${Uri.encode(email)}"))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    /** Prefix a bare domain (e.g. "www.site.com") with https:// so ACTION_VIEW can open it. */
    private fun normalizeUrl(url: String?): String? {
        val u = url?.trim().orEmpty()
        if (u.isEmpty()) return null
        return if (u.startsWith("http://", true) || u.startsWith("https://", true)) u else "https://$u"
    }

    /** Opens a link, ignoring the rare case where no app can handle it. */
    private fun openLink(url: String?) {
        if (url.isNullOrBlank()) return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No app found to open this link", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isAdmin) menuInflater.inflate(R.menu.menu_contact_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_edit_contact -> {
                startActivity(Intent(mContext, EditContactActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
