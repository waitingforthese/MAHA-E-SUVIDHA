package rahul.jagtap.dmas

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_BackgroundColor
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Bold
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_FontColor
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_FontSize
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Hr
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Italic
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Link
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_ListBullet
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_ListNumber
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Quote
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Strikethrough
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Subscript
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.ARE_ToolItem_Underline
import rahul.jagtap.dmas.widget.areditor.styles.toolitems.IARE_ToolItem
import rahul.jagtap.dmas.databinding.ActivityEditSuchnaBinding
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.utils.Utils


class EditSuchnaActivity : BaseActivity() {
    lateinit var binding: ActivityEditSuchnaBinding
    private var scrollerAtEnd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditSuchnaBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "Edit सूचना"
        initToolbar()
    }

    private fun initToolbar() {
        val bold: IARE_ToolItem = ARE_ToolItem_Bold() //        val youtube: IARE_ToolItem = ARE_ToolItem_Youtube()
        val italic: IARE_ToolItem = ARE_ToolItem_Italic()
        val underline: IARE_ToolItem = ARE_ToolItem_Underline()
        val strikethrough: IARE_ToolItem = ARE_ToolItem_Strikethrough()
        val fontSize: IARE_ToolItem = ARE_ToolItem_FontSize()
        val fontColor: IARE_ToolItem = ARE_ToolItem_FontColor()
        val backgroundColor: IARE_ToolItem = ARE_ToolItem_BackgroundColor()
        val quote: IARE_ToolItem = ARE_ToolItem_Quote()
        val listNumber: IARE_ToolItem = ARE_ToolItem_ListNumber()
        val listBullet: IARE_ToolItem = ARE_ToolItem_ListBullet()
        val hr: IARE_ToolItem = ARE_ToolItem_Hr()
        val link: IARE_ToolItem = ARE_ToolItem_Link()
        val subscript: IARE_ToolItem = ARE_ToolItem_Subscript() //        val superscript: IARE_ToolItem = ARE_ToolItem_Superscript()
        //        val left: IARE_ToolItem = ARE_ToolItem_AlignmentLeft()
        //        val center: IARE_ToolItem = ARE_ToolItem_AlignmentCenter()
        //        val right: IARE_ToolItem = ARE_ToolItem_AlignmentRight()
        //        val at: IARE_ToolItem = ARE_ToolItem_At()
        binding.areToolbar.addToolbarItem(bold)
        binding.areToolbar.addToolbarItem(italic)
//        binding.areToolbar.addToolbarItem(italic)
        binding.areToolbar.addToolbarItem(underline)
        binding.areToolbar.addToolbarItem(strikethrough)
        binding.areToolbar.addToolbarItem(fontSize)
        binding.areToolbar.addToolbarItem(fontColor)
        binding.areToolbar.addToolbarItem(backgroundColor)
        binding.areToolbar.addToolbarItem(quote)
        binding.areToolbar.addToolbarItem(listNumber)
        binding.areToolbar.addToolbarItem(listBullet)
        binding.areToolbar.addToolbarItem(hr)
        binding.areToolbar.addToolbarItem(link)
        binding.areToolbar.addToolbarItem(subscript) //        binding.areToolbar.addToolbarItem(superscript)
        //        binding.areToolbar.addToolbarItem(left)
        //        binding.areToolbar.addToolbarItem(center)
        //        binding.areToolbar.addToolbarItem(right)
        //        binding.areToolbar.addToolbarItem(at)

        binding.arEditText.setToolbar(binding.areToolbar)

        setHtml()

        initToolbarArrow()
    }

    private fun setHtml() {
        when (intent.getStringExtra("type")) {
            Utils.PAN_CARDS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.PAN_CARDS_SUCHNA))
            }

            Utils.SHOP_ACTS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.SHOP_ACTS_SUCHNA))
            }

            Utils.UDYAM_AADHAR_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.UDYAM_AADHAR_SUCHNA))
            }

            Utils.FOOD_LICENSE_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.FOOD_LICENSE_SUCHNA))
            }

            Utils.PROVIDENT_FUND_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.PROVIDENT_FUND_SUCHNA))
            }

            Utils.NEPAL_MONEY_TRANSFER_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.NEPAL_MONEY_TRANSFER_SUCHNA))
            }

            Utils.RAILWAY_TICKET_BOOKING_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.RAILWAY_TICKET_BOOKING_SUCHNA))
            }

            Utils.BUSINESS_PAN_CARDS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.BUSINESS_PAN_CARDS_SUCHNA))
            }

            Utils.ELECTION_CARDS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.ELECTION_CARDS_SUCHNA))
            }

            Utils.CIBIL_REPORTS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.CIBIL_REPORTS_SUCHNA))
            }

            Utils.DRIVING_LEARNING_LICENSES_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.DRIVING_LEARNING_LICENSES_SUCHNA))
            }

            Utils.POLICE_VERIFICATIONS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.POLICE_VERIFICATIONS_SUCHNA))
            }

            Utils.GST_REGS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.GST_REGS_SUCHNA))
            }

            Utils.AADHAR_CARD_UPDATE_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.AADHAR_CARD_UPDATE_SUCHNA))
            }

            Utils.EDIT_PAN_AADHAR_CARDS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.EDIT_PAN_AADHAR_CARDS_SUCHNA))
            }

            Utils.PASSPORTS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.PASSPORTS_SUCHNA))
            }

            Utils.ACHUK_JANMA_KUNDLI_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.ACHUK_JANMA_KUNDLI_SUCHNA))
            }

            Utils.AADHAR_PAN_LINK_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.AADHAR_PAN_LINK_SUCHNA))
            }

            Utils.GAZZETS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.GAZZETS_SUCHNA))
            }

            Utils.FREE_CREDIT_CARDS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.FREE_CREDIT_CARDS_SUCHNA))
            }

            Utils.ALL_GOVT_CARDS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.ALL_GOVT_CARDS_SUCHNA))
            }

            Utils.VERIFICATION_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.VERIFICATION_SUCHNA))
            }

            Utils.FARMER_POLICIES_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.FARMER_POLICIES_SUCHNA))
            }

            Utils.DEMAT_ACCOUNTS_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.DEMAT_ACCOUNTS_SUCHNA))
            }

            Utils.GOVT_SCHEMES_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.GOVT_SCHEMES_SUCHNA))
            }

            Utils.JYOTISH_SHASTRA_SUCHNA -> {
                binding.arEditText.fromHtml(intent.getStringExtra(Utils.JYOTISH_SHASTRA_SUCHNA))
            }
        }
    }

    private fun initToolbarArrow() {
        binding.areToolbar.viewTreeObserver.addOnScrollChangedListener {
            val scrollX = binding.areToolbar.scrollX
            val scrollWidth = binding.areToolbar.width
            val fullWidth = binding.areToolbar.getChildAt(0).width
            scrollerAtEnd = if (scrollX + scrollWidth < fullWidth) {
                binding.arrow.setImageResource(R.drawable.arrow_right)
                false
            } else {
                binding.arrow.setImageResource(R.drawable.arrow_left)
                true
            }
        }
        binding.arrow.setOnClickListener {
            scrollerAtEnd = if (scrollerAtEnd) {
                binding.areToolbar.smoothScrollBy(-Int.MAX_VALUE, 0)
                false
            } else {
                val hsWidth = binding.areToolbar.getChildAt(0).width
                binding.areToolbar.smoothScrollBy(hsWidth, 0)
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                val html: String = this.binding.arEditText.html
                database.child(Utils.SUCHNA_TABLE).child(intent.getStringExtra("type")!!).setValue(html)
                setResult(RESULT_OK)
                toast("Entry updated successfully")
                finish()
                return true
            }
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
