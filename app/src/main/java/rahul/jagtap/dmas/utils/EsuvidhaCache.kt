package rahul.jagtap.dmas.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.model.EsuvidhaServiceTile
import rahul.jagtap.dmas.preferences.Preferences
import java.lang.reflect.Type

/**
 * Disk cache for the e-suvidha services grid payloads (dynamic types, suchna notices, admin grid
 * overrides). Both the user grid ([rahul.jagtap.dmas.user.ESuvidhaServicesGridActivity]) and the menu
 * grid ([rahul.jagtap.dmas.ESuvidhaMenuActivity]) fetch the same slow, rarely-changing data over the
 * network before they can render anything. Persisting the raw JSON lets those screens paint instantly
 * from the last known snapshot (stale-while-revalidate) while the network refresh runs in the
 * background and re-renders when it lands.
 *
 * Stored as raw JSON strings in [Preferences] so no schema migration is ever needed.
 */
object EsuvidhaCache {

    private const val KEY_DYNAMIC_TYPES = "cache_esuvidha_dynamic_types"
    private const val KEY_SUCHNA = "cache_esuvidha_suchna"
    private const val KEY_GRID_OVERRIDES = "cache_esuvidha_grid_overrides"

    private val gson = Gson()
    private val dynamicTypesType: Type =
        object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>?>() {}.type
    private val suchnaType: Type = object : TypeToken<HashMap<String, String>?>() {}.type
    private val overridesType: Type = object : TypeToken<HashMap<String, EsuvidhaServiceTile>?>() {}.type

    // --- dynamic types (esuvidha_dynamic_types) ---

    /** Persist the raw dynamic-types JSON straight from the network response. Blank/"null" is ignored. */
    fun saveDynamicTypesJson(prefs: Preferences?, json: String?) {
        if (!json.isNullOrEmpty() && json != "null") prefs?.setCachedString(KEY_DYNAMIC_TYPES, json)
    }

    fun getDynamicTypes(prefs: Preferences?): HashMap<String, HashMap<String, HashMap<String, String>>>? =
        parse(prefs?.getCachedString(KEY_DYNAMIC_TYPES), dynamicTypesType)

    /** Raw cached dynamic-types JSON, for callers that parse the string themselves. Null/"null" if absent. */
    fun getDynamicTypesJson(prefs: Preferences?): String? {
        val json = prefs?.getCachedString(KEY_DYNAMIC_TYPES)
        return if (json.isNullOrEmpty() || json == "null") null else json
    }

    // --- suchna notices ---

    fun saveSuchnaJson(prefs: Preferences?, json: String?) {
        if (!json.isNullOrEmpty() && json != "null") prefs?.setCachedString(KEY_SUCHNA, json)
    }

    fun getSuchna(prefs: Preferences?): HashMap<String, String>? =
        parse(prefs?.getCachedString(KEY_SUCHNA), suchnaType)

    // --- admin grid overrides (esuvidha_grid) ---

    /** Persist the resolved overrides map (Firebase has no raw JSON to hand back, so we serialize it). */
    fun saveGridOverrides(prefs: Preferences?, overrides: Map<String, EsuvidhaServiceTile>) {
        prefs?.setCachedString(KEY_GRID_OVERRIDES, gson.toJson(overrides))
    }

    fun getGridOverrides(prefs: Preferences?): HashMap<String, EsuvidhaServiceTile>? =
        parse(prefs?.getCachedString(KEY_GRID_OVERRIDES), overridesType)

    private fun <T> parse(json: String?, type: Type): T? {
        if (json.isNullOrEmpty() || json == "null") return null
        return try {
            gson.fromJson<T>(json, type)
        } catch (e: Exception) {
            null
        }
    }
}
