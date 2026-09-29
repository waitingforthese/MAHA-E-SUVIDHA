package rahul.jagtap.dmas.user

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.EsuvidhaGridAdapter
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaServicesGridBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.EsuvidhaServiceTile
import rahul.jagtap.dmas.model.ImageDetails
import rahul.jagtap.dmas.utils.EsuvidhaCache
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry.GridTile
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.widget.MaterialSearchView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

/**
 * User-facing e-suvidha services grid. Replaces the old static-button OnlineESuvidhaOptionsActivity for
 * users: it flattens services + their subtypes into one grid, with title/icon/order/visibility driven by
 * admin overrides at esuvidha_grid (see [EsuvidhaServiceRegistry]). Admin/employee still use the dates path.
 */
class ESuvidhaServicesGridActivity : BaseActivity() {
    private val TAG = ESuvidhaServicesGridActivity::class.java.simpleName
    private lateinit var binding: ActivityEsuvidhaServicesGridBinding

    private var typesMap: HashMap<String, HashMap<String, HashMap<String, String>>>? = null
    private var suchnaMap: HashMap<String, String>? = null
    private val overrides = HashMap<String, EsuvidhaServiceTile>()
    private val allTiles = ArrayList<GridTile>()
    private var searchQuery = ""
    private var renderedFromCache = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsuvidhaServicesGridBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "महा ई सुविधा - लिस्ट"

        binding.rvServices.layoutManager = GridLayoutManager(mContext, 3)
//        setESuvidhaImage()
        renderFromCache()
        fetchDynamicTypes()
    }

    /**
     * Paint the grid instantly from the last-known snapshot so the screen never opens blank. The network
     * refresh in [fetchDynamicTypes] runs regardless and re-renders with fresh data when it lands.
     */
    private fun renderFromCache() {
        val prefs = app?.preferences
        typesMap = EsuvidhaCache.getDynamicTypes(prefs)
        suchnaMap = EsuvidhaCache.getSuchna(prefs)
        EsuvidhaCache.getGridOverrides(prefs)?.let {
            overrides.clear()
            overrides.putAll(it)
        }
        // Only treat it as a real cache hit if we actually have services to show.
        if (typesMap != null || overrides.isNotEmpty()) {
            renderedFromCache = true
            buildGrid()
        }
    }

//    private fun setESuvidhaImage() {
//        database.child(Utils.ESUVIDHA_LIST_IMAGE_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {}
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val imageDetails = dataSnapshot.getValue(ImageDetails::class.java)
//                if (imageDetails != null && !TextUtils.isEmpty(imageDetails.imageDownloadUrl)) {
//                    mContext?.let { Glide.with(it).load(imageDetails.imageDownloadUrl).into(binding.imageView1) }
//                    binding.imageView1.visible()
//                }
//            }
//        })
//    }

    private fun fetchDynamicTypes() {
        // Only block the screen with a spinner when we had nothing cached to show.
        if (!renderedFromCache) binding.progressBar.visible()
        app?.apiRequestHelper?.apiService?.esuvidhaDynamicTypes?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (!json.isNullOrEmpty() && json != "null") {
                        val type: Type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>?>() {}.type
                        typesMap = Gson().fromJson(json, type)
                        EsuvidhaCache.saveDynamicTypesJson(app?.preferences, json)
                    }
                }
                fetchSuchna()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "dynamicTypes failure", t)
                fetchSuchna()
            }
        })
    }

    private fun fetchSuchna() {
        app?.apiRequestHelper?.apiService?.suchna?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (!json.isNullOrEmpty() && json != "null") {
                        val type: Type = object : TypeToken<HashMap<String, String>?>() {}.type
                        suchnaMap = Gson().fromJson(json, type)
                        EsuvidhaCache.saveSuchnaJson(app?.preferences, json)
                    }
                }
                loadOverridesAndBuild()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "suchna failure", t)
                loadOverridesAndBuild()
            }
        })
    }

    private fun loadOverridesAndBuild() {
        database.child(Utils.ESUVIDHA_GRID_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                buildGrid()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                overrides.clear()
                for (child in snapshot.children) {
                    val tile = try {
                        child.getValue(EsuvidhaServiceTile::class.java)
                    } catch (e: Exception) {
                        null
                    }
                    val key = child.key
                    if (tile != null && !key.isNullOrEmpty()) overrides[key] = tile
                }
                EsuvidhaCache.saveGridOverrides(app?.preferences, overrides)
                buildGrid()
            }
        })
    }

    private fun buildGrid() {
        binding.progressBar.gone()
        allTiles.clear()
        allTiles.addAll(EsuvidhaServiceRegistry.buildTiles(typesMap, overrides, includeDisabled = false))
        applyFilter(searchQuery)
    }

    private fun applyFilter(query: String) {
        searchQuery = query
        val q = query.trim()
        val shown = if (q.isEmpty()) allTiles else allTiles.filter { matches(it, q) }
        if (shown.isEmpty()) {
            binding.tvEmpty.text = if (allTiles.isEmpty()) getString(R.string.txt_try_later) else "No matching services"
            binding.tvEmpty.visible()
        } else {
            binding.tvEmpty.gone()
        }
        binding.rvServices.adapter = EsuvidhaGridAdapter(mContext, shown) { tile ->
            startActivity(EsuvidhaServiceRegistry.buildIntent(this, tile, typesMap, suchnaMap))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        binding.toolbarLayout.searchView.setMenuItem(menu.findItem(R.id.action_search))
        binding.toolbarLayout.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false
            override fun onQueryTextChange(newText: String): Boolean { applyFilter(newText); return true }
        })
        return true
    }

    /** Match the query against the visible title and the English service key/default (so "pan", "gst" work). */
    private fun matches(t: GridTile, q: String): Boolean =
        t.title.contains(q, ignoreCase = true) ||
            t.def.serviceKey.contains(q, ignoreCase = true) ||
            t.defaultTitle.contains(q, ignoreCase = true)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Utils.hideSoftKeyboard(this)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
