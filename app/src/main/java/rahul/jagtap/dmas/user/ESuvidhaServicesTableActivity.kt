package rahul.jagtap.dmas.user

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.MainActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityServicesTableBinding
import rahul.jagtap.dmas.databinding.ItemServiceRowBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.isVisible
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.EsuvidhaCache
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

/**
 * Renders a 3-column "table":
 *  [सुविधेचे नाव | बटन | फी चार्जेस]
 * Data is flattened from the nested JSON (category -> typeKey -> {type_title}).
 *
 * Usage:
 * 1) If you have JSON in hand, pass it via intent extra "json".
 * 2) Otherwise it will call the same API you already use in ESuvidhaOptionsDynamicTypesActivity.
 */
class ESuvidhaServicesTableActivity : BaseActivity() {

    private lateinit var binding: ActivityServicesTableBinding
    private val rows = ArrayList<TableRowItem>()
    private lateinit var adapter: ServiceRowsAdapter
    private var renderedFromCache = false

    // ParentKey -> Button label (Marathi)
    private val categoryLabel = mapOf(
        "all_govt_cards" to "सर्व शासकीय कार्ड",
        "business_pan_cards" to "व्यवसाय पॅन कार्ड",
        "cibil_reports" to "सिबिल रिपोर्ट",
        "driving_learning_licenses" to "आर टी ओ ची कामे",
        "election_cards" to "मतदान कार्ड (नवीन / दुरुस्ती)",
        "farmer_policies" to "सर्व शेतकरी योजना",
        "food_license" to "फूड लायसन्स",
        "gazzets" to "गँझेट",
        "govt_schemes" to "नवीन शासकीय योजना",
        "gst_regs" to "GST नोंदणी",
        "jyotish_shastra" to "ज्योतिष शास्त्रींना प्रश्न विचारा",
        "other_new_schemes" to "रजिस्ट्रेशन / नोंदणी",
        "pan_cards" to "पॅन कार्ड(नवीन / दुरुस्ती)",
        "passports" to "पासपोर्ट (नवीन / दुरूस्ती)",
        "primary_school_work" to "इतर सेवा / सुविधा",
        "provident_fund" to "प्रोव्हिडंट फंड/ पी एफ काढणे",
        "shop_acts" to "शॉप ऍक्ट",
        "tax_agent_work" to "टॅक्स एजंट ची कामे",
        "udyam_aadhar" to "उद्यम आधार",
        "verification" to "व्हेरिफिकेशन"
    )

    // Define order (Marathi sequence → JSON keys)
    private val categoryOrder = listOf(
        "pan_cards",            // पॅन कार्ड (नवीन / दुरुस्ती)
        "election_cards",       // मतदान कार्ड (नवीन / दुरुस्ती)
        "business_pan_cards",   // व्यवसाय पॅन कार्ड
        "shop_acts",            // शॉप ऍक्ट
        "udyam_aadhar",         // उद्यम आधार
        "food_license",         // फूड लायसन्स
        "gst_regs",             // GST नोंदणी
        "cibil_reports",        // सिबिल रिपोर्ट
        "all_govt_cards",       // सर्व शासकीय कार्ड
        "farmer_policies",      // सर्व शेतकरी योजना
        "govt_schemes",         // नवीन शासकीय योजना
        "other_new_schemes",    // रजिस्ट्रेशन / नोंदणी
        "primary_school_work",  // इतर सेवा / सुविधा
        "tax_agent_work",       // टॅक्स एजंट ची कामे
        "driving_learning_licenses", // आर टी ओ ची कामे
        "passports",            // पासपोर्ट (नवीन / दुरूस्ती)
        "provident_fund",       // प्रोव्हिडंट फंड/ पी एफ काढणे
        "gazzets",              // गँझेट
        "jyotish_shastra",      // ज्योतिष शास्त्रींना प्रश्न विचारा
        "verification"          // व्हेरिफिकेशन
    )

    // The full JSON shape: HashMap<parentKey, HashMap<typeKey, HashMap<String,String>>>
    private var typesMap: HashMap<String, HashMap<String, HashMap<String, String>>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set status bar color to black
        window.statusBarColor = ContextCompat.getColor(this, R.color.font_black_0)
        // Ensure icons are light (white), so they're visible on dark bar
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
        if (Utils.disableScreenshot) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
        binding = ActivityServicesTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val fromLogin = intent.getBooleanExtra("fromLogin", false) == true
        supportActionBar?.setDisplayHomeAsUpEnabled(!fromLogin)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "फी/चार्जेस"//getString(R.string.txt_referral_program)
        // Optional simple title for this screen
//        title = "सुविधांची यादी"

        // Recycler
        adapter = ServiceRowsAdapter(rows,
            onCategoryClick = { parentKey ->
                // same style of navigation as your reference activity
//                redirectToCategory(parentKey)
            }
        )
        binding.rvServices.layoutManager = LinearLayoutManager(this)
        binding.rvServices.adapter = adapter
        // Thin divider line between rows (not after the last row)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider_table_row)?.let { divider.setDrawable(it) }
        binding.rvServices.addItemDecoration(divider)

        binding.btnSkip.isVisible = fromLogin
        binding.btnSkip?.setOnClickListener {
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
        }


        // Load from intent "json" if provided; else paint from cache and refresh over the network.
        val json = intent.getStringExtra("json")
        if (!json.isNullOrBlank() && json != "null") {
            parseAndBind(json)
        } else {
            renderFromCache()
            fetchDynamicTypesFromApi()
        }
    }

    /** Paint instantly from the last cached types so the post-login table never opens blank. */
    private fun renderFromCache() {
        val cached = EsuvidhaCache.getDynamicTypesJson(app?.preferences)
        if (cached != null) {
            renderedFromCache = true
            parseAndBind(cached)
        }
    }

    private fun fetchDynamicTypesFromApi() {
        if (!renderedFromCache) binding.progressBar.visible()
        app?.apiRequestHelper?.apiService?.esuvidhaDynamicTypes?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar.gone()
                val json = response.body()?.string()
                if (response.isSuccessful && !json.isNullOrBlank() && json != "null") {
                    EsuvidhaCache.saveDynamicTypesJson(app?.preferences, json)
                    parseAndBind(json)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar.gone()
                // You can show a snack/toast here
            }
        })
    }

    private fun parseAndBind(json: String) {
        val type: Type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>?>() {}.type
        typesMap = Gson().fromJson(json, type)

        rows.clear()
        // iterate in custom order
        for (parentKey in categoryOrder) {
            val childMap = typesMap?.get(parentKey) ?: continue
            val categoryText = categoryLabel[parentKey] ?: parentKey
            childMap.forEach inner@{ (typeKey, leaf) ->
                val title = leaf["type_title"] ?: return@inner
                if (title == "0") return@inner
                rows.add(
                    TableRowItem(
                        parentKey = parentKey,
                        typeKey = typeKey,
                        title = title,
                        categoryButton = categoryText,
                        fee = extractFee(title)
                    )
                )
            }
        }
//        // Flatten: parentKey -> typeKey -> { "type_title": ... }
//        typesMap?.forEach { (parentKey, childMap) ->
//            val categoryText = categoryLabel[parentKey] ?: parentKey
//            childMap.forEach inner@{ (typeKey, leaf) ->
//                val title = leaf["type_title"] ?: return@inner
//                if (title == "0") return@inner  // skip placeholders
//                rows.add(
//                    TableRowItem(
//                        parentKey = parentKey,
//                        typeKey = typeKey,
//                        title = title,
//                        categoryButton = categoryText,
//                        fee = "",//extractFee(title)
//                    )
//                )
//            }
//        }
//        rows.sortBy { it.title }
        adapter.notifyDataSetChanged()
    }

    private fun extractFee(title: String): String {
        // Tries formats like: "फी 150 रुपये", "फी - 100/-", "फी – 1200/-"
        val rx = Regex("""फी\s*[-–:]?\s*(\d+)\s*(?:/-|रुपये)?""")
        val m = rx.find(title)
        val num = m?.groupValues?.getOrNull(1)
        return if (!num.isNullOrBlank()) "₹$num" else "—"
    }

//    private fun redirectToCategory(parentKey: String) {
//        val map = typesMap?.get(parentKey)
//        val typeTitle = categoryLabel[parentKey] ?: parentKey
//        val intent = Intent(this, EditESuvidhaDynamicTypesActivity::class.java).apply {
//            putExtra("typeTitle", typeTitle)
//            putExtra("nodeName", parentKey)
//            putExtra("hashMap", HashMap(map ?: hashMapOf()))
//        }
//        startActivity(intent)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Utils.hideSoftKeyboard(this)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

/** Row model for the table */
data class TableRowItem(
    val parentKey: String,
    val typeKey: String,
    val title: String,
    val categoryButton: String,
    val fee: String
)

/** Adapter for the Recycler table */
class ServiceRowsAdapter(
    private val items: List<TableRowItem>,
    private val onCategoryClick: (parentKey: String) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<ServiceRowsAdapter.VH>() {

    inner class VH(val vb: ItemServiceRowBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(vb.root)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
        val inf = android.view.LayoutInflater.from(parent.context)
        return VH(ItemServiceRowBinding.inflate(inf, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.vb.tvTitle.text = item.title
        holder.vb.tvFee.text = item.categoryButton
        // Zebra striping for easier scanning of a long list
        val rowColor = if (position % 2 == 0) R.color.white else R.color.table_row_alt
        holder.vb.rowRoot.setBackgroundColor(
            ContextCompat.getColor(holder.vb.root.context, rowColor)
        )
//        holder.vb.btnAction.text = item.categoryButton
//        holder.vb.btnAction.setOnClickListener { onCategoryClick(item.parentKey) }
    }

    override fun getItemCount(): Int = items.size
}
