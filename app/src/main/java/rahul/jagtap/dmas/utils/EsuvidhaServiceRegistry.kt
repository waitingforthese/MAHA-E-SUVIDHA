package rahul.jagtap.dmas.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.model.EsuvidhaServiceTile
import rahul.jagtap.dmas.user.*

/**
 * Code-side source of truth for the e-suvidha services grid.
 *
 * Destinations are compiled Activities, so this registry (not Firebase) owns the list of services, which
 * screen each opens, and whether a service is expanded into its subtypes. Admin-editable presentation
 * (title / icon / order / show-hide) lives in Firebase as [EsuvidhaServiceTile] overrides, keyed by the
 * tile's itemKey. Subtypes come live from the esuvidha_dynamic_types map (same map the parent screens
 * already receive). [buildTiles] merges the three sources into the final ordered list.
 *
 * Adding a genuinely new service still requires a new form Activity + a [ServiceDef] here + a release.
 */
object EsuvidhaServiceRegistry {

    /** Fallback icon when an admin has not uploaded one for a tile yet. */
    val DEFAULT_ICON = R.drawable.ic_svc_receipt

    /**
     * @param serviceKey       stable id for the service group; also the standalone tile's itemKey.
     * @param activityClass    screen opened on tap.
     * @param dynamicTypesKey  key into the esuvidha_dynamic_types map for this group's subtypes (null = no subtypes).
     * @param suchnaKey        Utils.*_SUCHNA key used to pass the group's notice text (null = none).
     * @param defaultTitle     shown when a standalone tile has no admin title override.
     * @param defaultIconRes   built-in icon used until an admin uploads one for the tile.
     */
    data class ServiceDef(
        val serviceKey: String,
        val activityClass: Class<out Activity>,
        val dynamicTypesKey: String?,
        val suchnaKey: String?,
        val defaultTitle: String,
        val defaultIconRes: Int = DEFAULT_ICON
    ) {
        val hasSubtypes: Boolean get() = dynamicTypesKey != null
    }

    /** A resolved, ready-to-render grid tile after merging registry + subtypes + admin overrides. */
    data class GridTile(
        val itemKey: String,
        val def: ServiceDef,
        val subtypeKey: String?,   // null for a standalone service tile
        val title: String,
        val iconUrl: String,       // uploaded icon; blank => use def.DEFAULT_ICON
        val order: Int,
        val enabled: Boolean,
        val feeText: String = "",  // e.g. "₹150", the resolved fee badge; blank if none
        val defaultTitle: String = ""  // registry / subtype fallback title (fee stripped), for the admin editor
    )

    // Fees are embedded in subtype titles ("...फी 150 रुपये"); same parsing the fee table uses.
    private val feeRegex = Regex("""फी\s*[-–:]?\s*(\d+)\s*(?:/-|रुपये)?""")
    private val feeSuffixRegex = Regex("""[\s\-–—:|]*फी\s*[-–:]?\s*\d+\s*(?:/-|रुपये)?\s*$""")

    private fun extractFee(title: String): String {
        val n = feeRegex.find(title)?.groupValues?.getOrNull(1)
        return if (!n.isNullOrBlank()) "₹$n" else ""
    }

    /** Normalise an admin-entered fee ("150", "₹150", "150/-") to a "₹150" badge; blank stays blank. */
    fun formatFee(raw: String?): String {
        val s = raw?.trim().orEmpty()
        if (s.isEmpty()) return ""
        if (s.startsWith("₹")) return s
        val digits = Regex("""\d+""").find(s)?.value
        return if (!digits.isNullOrBlank()) "₹$digits" else s
    }

    /** Remove a trailing "फी NNN रुपये/-" so the title and the fee badge don't duplicate it. */
    private fun stripFeeSuffix(title: String): String = title.replace(feeSuffixRegex, "").trim()

    // Natural order = order of this list; subtypes inherit their group's slot. Admin overrides can re-order.
    val SERVICES: List<ServiceDef> = listOf(
        // --- services WITH subtypes (dynamicTypesKey set) -> flattened into one tile per subtype ---
        ServiceDef("pan_cards", PanCardActivity::class.java, "pan_cards", Utils.PAN_CARDS_SUCHNA, "पॅन कार्ड", R.drawable.ic_svc_id),
        ServiceDef("business_pan_cards", BusinessPanCardActivity::class.java, "business_pan_cards", Utils.BUSINESS_PAN_CARDS_SUCHNA, "बिझनेस पॅन कार्ड", R.drawable.ic_svc_store),
        ServiceDef("election_cards", ElectionCardActivity::class.java, "election_cards", Utils.ELECTION_CARDS_SUCHNA, "मतदान कार्ड (नवीन / दुरुस्ती)", R.drawable.ic_svc_group),
        ServiceDef("shop_acts", ShopActActivity::class.java, "shop_acts", Utils.SHOP_ACTS_SUCHNA, "शॉप ॲक्ट", R.drawable.ic_svc_store),
        ServiceDef("udyam_aadhar", UdyamAadharActivity::class.java, "udyam_aadhar", Utils.UDYAM_AADHAR_SUCHNA, "उद्यम आधार", R.drawable.ic_svc_office),
        ServiceDef("food_license", FoodLicenseActivity::class.java, "food_license", Utils.FOOD_LICENSE_SUCHNA, "फूड लायसन्स", R.drawable.ic_svc_store),
        ServiceDef("gst_regs", GstRegistrationActivity::class.java, "gst_regs", Utils.GST_REGS_SUCHNA, "GST नोंदणी", R.drawable.ic_svc_receipt),
        ServiceDef("cibil_reports", CibilReportActivity::class.java, "cibil_reports", Utils.CIBIL_REPORTS_SUCHNA, "सिबिल रिपोर्ट", R.drawable.ic_svc_report),
        ServiceDef("all_govt_cards", AllGovtCardsActivity::class.java, "all_govt_cards", Utils.ALL_GOVT_CARDS_SUCHNA, "सर्व शासकीय कार्ड", R.drawable.ic_svc_shield),
        ServiceDef("verification", VerificationActivity::class.java, "verification", Utils.VERIFICATION_SUCHNA, "व्हेरिफिकेशन", R.drawable.ic_svc_report),
        ServiceDef("farmer_policies", FarmerPolicyActivity::class.java, "farmer_policies", Utils.FARMER_POLICIES_SUCHNA, "शेतकरी योजना", R.drawable.ic_svc_farm),
        ServiceDef("govt_schemes", GovtSchemesActivity::class.java, "govt_schemes", Utils.GOVT_SCHEMES_SUCHNA, "नवीन शासकीय योजना", R.drawable.ic_svc_gift),
        ServiceDef("police_verifications", PoliceVerificationActivity::class.java, "other_new_schemes", Utils.POLICE_VERIFICATIONS_SUCHNA, "रजिस्ट्रेशन / नोंदणी", R.drawable.ic_svc_shield),
        ServiceDef("tax_agent_work", CreditCardActivity::class.java, "tax_agent_work", Utils.FREE_CREDIT_CARDS_SUCHNA, "टॅक्स एजंट ची कामे", R.drawable.ic_svc_card),
        ServiceDef("driving_learning_licenses", DrivingLearningLicenseActivity::class.java, "driving_learning_licenses", Utils.DRIVING_LEARNING_LICENSES_SUCHNA, "आर टी ओ ची कामे", R.drawable.ic_svc_car),
        ServiceDef("passports", PassportActivity::class.java, "passports", Utils.PASSPORTS_SUCHNA, "पासपोर्ट (नवीन / दुरूस्ती)", R.drawable.ic_svc_book),
        ServiceDef("provident_fund", ProvidentFundActivity::class.java, "provident_fund", Utils.PROVIDENT_FUND_SUCHNA, "प्रोव्हिडंट फंड/ पी एफ काढणे", R.drawable.ic_svc_wallet),
        ServiceDef("gazzets", GazzetActivity::class.java, "gazzets", Utils.GAZZETS_SUCHNA, "गँझेट / राजपत्र - नावात बदल", R.drawable.ic_svc_receipt),
        ServiceDef("primary_school_work", DematAccountActivity::class.java, "primary_school_work", Utils.DEMAT_ACCOUNTS_SUCHNA, "इतर सेवा / सुविधा", R.drawable.ic_svc_group),

        // --- standalone services (no subtypes) -> exactly one tile each ---
        ServiceDef("aadhar_card_update", AadharCardUpdateActivity::class.java, null, Utils.AADHAR_CARD_UPDATE_SUCHNA, "आधार कार्ड अपडेट", R.drawable.ic_svc_id),
        ServiceDef("edit_pan_aadhar_cards", EditPanOrAadharCardActivity::class.java, null, Utils.EDIT_PAN_AADHAR_CARDS_SUCHNA, "फोटो शॉप पँण कार्ड / आधार कार्ड", R.drawable.ic_svc_edit),
        ServiceDef("janma_kundli", JanmaKundliActivity::class.java, null, Utils.ACHUK_JANMA_KUNDLI_SUCHNA, "जन्म कुंडली", R.drawable.ic_svc_star),
        ServiceDef("nepal_money_transfer", NepalMoneyTransferActivity::class.java, null, Utils.NEPAL_MONEY_TRANSFER_SUCHNA, "नेपाल मनी ट्रान्सफर", R.drawable.ic_svc_money),
        ServiceDef("railway_ticket_booking", RailwayTicketBookingActivity::class.java, null, Utils.RAILWAY_TICKET_BOOKING_SUCHNA, "रेल्वे तिकीट बुकिंग", R.drawable.ic_svc_train),
        ServiceDef("manual_aadhar_pan", ManualAadharPanActivity::class.java, null, null, "मॅन्युअल आधार पॅन कार्ड", R.drawable.ic_svc_id),
        ServiceDef("taluka_setu", ShriGondaSetuKendraActivity::class.java, null, null, "तालुका सेतू सुविधा", R.drawable.ic_svc_office)
    )

    private val byKey: Map<String, ServiceDef> = SERVICES.associateBy { it.serviceKey }

    fun defFor(serviceKey: String?): ServiceDef? = byKey[serviceKey]

    /** Composite itemKey used to store/look up a tile's override. */
    fun itemKeyFor(serviceKey: String, subtypeKey: String?): String =
        if (subtypeKey.isNullOrEmpty()) serviceKey else "${serviceKey}__$subtypeKey"

    /**
     * Merge registry + live subtypes + admin overrides into the final ordered tile list.
     *
     * @param dynamicTypes  the esuvidha_dynamic_types map: serviceKey -> (subtypeKey -> fields). May be null.
     * @param overrides     admin tile overrides keyed by itemKey. May be empty.
     * @param includeDisabled  true for the admin editor (show hidden tiles), false for the user grid.
     */
    fun buildTiles(
        dynamicTypes: Map<String, Map<String, Map<String, String>>>?,
        overrides: Map<String, EsuvidhaServiceTile>,
        includeDisabled: Boolean
    ): List<GridTile> {
        val tiles = ArrayList<GridTile>()
        var slot = 0
        for (def in SERVICES) {
            val groupBase = slot * 1000
            slot++
            if (!def.hasSubtypes) {
                // Standalone service -> exactly one tile.
                resolveTile(def, null, def.defaultTitle, overrides, groupBase, includeDisabled)?.let { tiles += it }
                continue
            }
            val subtypeMap = dynamicTypes?.get(def.dynamicTypesKey)
            if (subtypeMap == null) {
                // Subtypes didn't load (e.g. network) -> keep a single fallback parent tile so the
                // service stays reachable. (Distinct from "loaded but all hidden" below.)
                resolveTile(def, null, def.defaultTitle, overrides, groupBase, includeDisabled)?.let { tiles += it }
                continue
            }
            // Subtypes loaded: emit the GROUPED HEADER tile (itemKey = serviceKey, opens the parent screen
            // so the user picks a subtype — same as ESuvidhaMenuActivity), then one tile per VISIBLE subtype
            // (non-blank type_title, not the "0" placeholder). If no subtype is visible (all hidden / not yet
            // configured), show NOTHING for this service — a header that leads to an empty parent is useless.
            val visibleSubtypes = subtypeMap.entries
                .filter { e -> e.value["type_title"].let { !it.isNullOrBlank() && it != "0" } }
            if (visibleSubtypes.isEmpty()) continue
            resolveTile(def, null, def.defaultTitle, overrides, groupBase, includeDisabled)?.let { tiles += it }
            visibleSubtypes.forEachIndexed { idx, entry ->
                resolveTile(def, entry.key, entry.value["type_title"] ?: def.defaultTitle,
                    overrides, groupBase + 1 + idx, includeDisabled)?.let { tiles += it }
            }
        }
        return tiles.sortedWith(compareBy({ it.order }, { it.itemKey }))
    }

    private fun resolveTile(
        def: ServiceDef,
        subtypeKey: String?,
        defaultTitle: String,
        overrides: Map<String, EsuvidhaServiceTile>,
        naturalOrder: Int,
        includeDisabled: Boolean
    ): GridTile? {
        val itemKey = itemKeyFor(def.serviceKey, subtypeKey)
        val ov = overrides[itemKey]
        val enabled = ov?.enabled ?: true
        if (!enabled && !includeDisabled) return null
        val rawTitle = ov?.title?.takeIf { it.isNotBlank() } ?: defaultTitle
        // Prefer the admin-managed fee; else fall back to a fee embedded in the title text.
        val feeText = ov?.fee?.takeIf { it.isNotBlank() }?.let { formatFee(it) } ?: extractFee(defaultTitle)
        val iconUrl = ov?.iconUrl.orEmpty()
        val order = ov?.order?.takeIf { it >= 0 } ?: naturalOrder
        return GridTile(itemKey, def, subtypeKey, stripFeeSuffix(rawTitle), iconUrl, order, enabled, feeText, stripFeeSuffix(defaultTitle))
    }

    /**
     * Build the launch Intent for a tile: opens its Activity, passes the group's subtype map + notice text
     * exactly as the old buttons did, and (for a subtype tile) a preselect key so the parent screen can
     * auto-select and configure that subtype's form.
     */
    fun buildIntent(
        context: Context,
        tile: GridTile,
        dynamicTypes: Map<String, Map<String, Map<String, String>>>?,
        suchnaMap: Map<String, String>?
    ): Intent {
        val intent = Intent(context, tile.def.activityClass)
        tile.def.suchnaKey?.let { intent.putExtra("suchna", suchnaMap?.get(it) ?: "") }
        // Parent screens with subtypes cast the "hashMap" extra to a non-null map, so always pass one
        // (empty if the group's subtypes didn't load) to avoid a crash on the fallback path.
        tile.def.dynamicTypesKey?.let { key ->
            intent.putExtra("hashMap", toSerializableTypeMap(dynamicTypes?.get(key) ?: emptyMap()))
        }
        if (!tile.subtypeKey.isNullOrEmpty()) intent.putExtra(EXTRA_PRESELECT_SUBTYPE, tile.subtypeKey)
        return intent
    }

    /** Colorful flat icons for the grouped "इतर" menu (distinct colour per service, shown untinted). */
    private val groupIcons: Map<String, Int> = mapOf(
        "pan_cards" to R.drawable.ic_svcc_pan_cards,
        "business_pan_cards" to R.drawable.ic_svcc_business_pan_cards,
        "election_cards" to R.drawable.ic_svcc_election_cards,
        "shop_acts" to R.drawable.ic_svcc_shop_acts,
        "udyam_aadhar" to R.drawable.ic_svcc_udyam_aadhar,
        "food_license" to R.drawable.ic_svcc_food_license,
        "gst_regs" to R.drawable.ic_svcc_gst_regs,
        "cibil_reports" to R.drawable.ic_svcc_cibil_reports,
        "all_govt_cards" to R.drawable.ic_svcc_all_govt_cards,
        "farmer_policies" to R.drawable.ic_svcc_farmer_policies,
        "govt_schemes" to R.drawable.ic_svcc_govt_schemes,
        "police_verifications" to R.drawable.ic_svcc_police_verifications,
        "tax_agent_work" to R.drawable.ic_svcc_tax_agent_work,
        "driving_learning_licenses" to R.drawable.ic_svcc_driving_learning_licenses,
        "passports" to R.drawable.ic_svcc_passports,
        "provident_fund" to R.drawable.ic_svcc_provident_fund,
        "gazzets" to R.drawable.ic_svcc_gazzets,
        "primary_school_work" to R.drawable.ic_svcc_primary_school_work,
        "verification" to R.drawable.ic_svcc_verification
    )

    /** Colourful grouped-menu icon for a service (falls back to the monochrome grid icon). */
    fun groupIconFor(def: ServiceDef): Int = groupIcons[def.serviceKey] ?: def.defaultIconRes

    /** Subtype-services (for the grouped "इतर" menu) that currently have ≥1 visible subtype. */
    fun groupedServices(dynamicTypes: Map<String, Map<String, Map<String, String>>>?): List<ServiceDef> =
        SERVICES.filter { def ->
            def.hasSubtypes && (dynamicTypes?.get(def.dynamicTypesKey)?.values?.any { f ->
                f["type_title"].let { !it.isNullOrBlank() && it != "0" }
            } == true)
        }

    /** A grouped-menu service matched by its (default) title. */
    fun groupedDefForTitle(title: String?): ServiceDef? =
        SERVICES.firstOrNull { it.hasSubtypes && it.defaultTitle == title }

    /** Open a service's PARENT screen (with its subtype map + notice) so the user picks the subtype — no preselect. */
    fun buildParentIntent(
        context: Context,
        def: ServiceDef,
        dynamicTypes: Map<String, Map<String, Map<String, String>>>?,
        suchnaMap: Map<String, String>?
    ): Intent {
        val intent = Intent(context, def.activityClass)
        def.suchnaKey?.let { intent.putExtra("suchna", suchnaMap?.get(it) ?: "") }
        def.dynamicTypesKey?.let { key ->
            intent.putExtra("hashMap", toSerializableTypeMap(dynamicTypes?.get(key) ?: emptyMap()))
        }
        return intent
    }

    const val EXTRA_PRESELECT_SUBTYPE = "preselectSubtypeKey"

    /** Parent screens read the extra as HashMap<String, HashMap<String, String>>; ensure that concrete type. */
    private fun toSerializableTypeMap(group: Map<String, Map<String, String>>): HashMap<String, HashMap<String, String>> {
        val out = HashMap<String, HashMap<String, String>>()
        for ((k, v) in group) out[k] = HashMap(v)
        return out
    }
}
