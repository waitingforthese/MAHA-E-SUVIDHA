package rahul.jagtap.dmas.admin.esuvidha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.adapter.EsuvidhaGridAdminAdapter
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaGridAdminBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.EsuvidhaServiceTile
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry.GridTile
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Admin-only management of the e-suvidha services grid. Lists every tile (including hidden ones) and lets
 * an admin rename / re-icon / set fee (via [EditEsuvidhaTileActivity]), toggle visibility, and reorder —
 * all written to esuvidha_grid/{itemKey} (+ backup) as [EsuvidhaServiceTile] overrides.
 */
class EsuvidhaGridAdminActivity : BaseActivity() {
    private val TAG = EsuvidhaGridAdminActivity::class.java.simpleName
    private lateinit var binding: ActivityEsuvidhaGridAdminBinding

    private var typesMap: HashMap<String, HashMap<String, HashMap<String, String>>>? = null
    private val overrides = HashMap<String, EsuvidhaServiceTile>()
    private val tiles = ArrayList<GridTile>()
    private lateinit var adapter: EsuvidhaGridAdminAdapter
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // E-Suvidha grid administration has been discontinued.
        // Close this screen without loading or changing E-Suvidha service data.
        finish()
    }

    private val dragCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {
        override fun isLongPressDragEnabled() = false // drag only from the handle

        override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter.onItemMove(vh.bindingAdapterPosition, target.bindingAdapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        override fun clearView(rv: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(rv, viewHolder)
            persistOrderAll() // save the new order once the drag ends
        }
    }

    override fun onResume() {
        super.onResume()
        loadAll()
    }

    private fun loadAll() {
        binding.progressBar.visible()
        app?.apiRequestHelper?.apiService?.esuvidhaDynamicTypes?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (!json.isNullOrEmpty() && json != "null") {
                        val type: Type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>?>() {}.type
                        typesMap = Gson().fromJson(json, type)
                    }
                }
                loadOverrides()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "dynamicTypes failure", t)
                loadOverrides()
            }
        })
    }

    private fun loadOverrides() {
        database.child(Utils.ESUVIDHA_GRID_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                if (checkIfActivityDestroying()) return
                rebuild()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (checkIfActivityDestroying()) return
                overrides.clear()
                for (child in snapshot.children) {
                    val tile = try { child.getValue(EsuvidhaServiceTile::class.java) } catch (e: Exception) { null }
                    val key = child.key
                    if (tile != null && !key.isNullOrEmpty()) overrides[key] = tile
                }
                rebuild()
            }
        })
    }

    private fun rebuild() {
        binding.progressBar.gone()
        tiles.clear()
        tiles.addAll(EsuvidhaServiceRegistry.buildTiles(typesMap, overrides, includeDisabled = true))
        adapter.notifyDataSetChanged()
    }

    private fun openEditor(tile: GridTile) {
        val subtitle = tile.def.defaultTitle + if (!tile.subtypeKey.isNullOrEmpty()) " • उपप्रकार" else ""
        startActivity(Intent(mContext, EditEsuvidhaTileActivity::class.java)
            .putExtra("itemKey", tile.itemKey)
            .putExtra("serviceKey", tile.def.serviceKey)
            .putExtra("subtypeKey", tile.subtypeKey)
            .putExtra("defaultTitle", tile.defaultTitle)
            .putExtra("defaultIconRes", tile.def.defaultIconRes)
            .putExtra("subtitle", subtitle)
            .putExtra("override", overrides[tile.itemKey]))
    }

    private fun toggleEnabled(tile: GridTile, checked: Boolean) {
        persist(ensureOverride(tile).apply { enabled = checked })
    }

    /**
     * Persist the current visual order by renumbering EVERY tile (order = index) in one batch write.
     * Avoids the collision problem of per-move midpoints (subtype tiles are naturally spaced by 1) and
     * keeps other override fields (title/icon/fee/enabled) intact.
     */
    private fun persistOrderAll() {
        val now = System.currentTimeMillis()
        val dt = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val name = app?.preferences?.loggedInUser?.name ?: "admin"
        val updates = HashMap<String, Any?>()
        tiles.forEachIndexed { index, tile ->
            val ov = ensureOverride(tile).apply {
                order = index
                updatedBy = name; timeStamp = now; updatedDateTime = dt
            }
            val key = tile.itemKey
            overrides[key] = ov
            updates["${Utils.ESUVIDHA_GRID_TABLE}/$key"] = ov
            updates["${Utils.ESUVIDHA_GRID_BACKUP_TABLE}/$key"] = ov
        }
        database.updateChildren(updates) { error: DatabaseError?, _: DatabaseReference? ->
            if (error != null && !checkIfActivityDestroying()) toast("Failed to save order: ${error.message}")
        }
    }

    /** Existing override (copied) or a fresh one carrying just this tile's identity. */
    private fun ensureOverride(tile: GridTile): EsuvidhaServiceTile =
        overrides[tile.itemKey]?.copy()
            ?: EsuvidhaServiceTile(itemKey = tile.itemKey, serviceKey = tile.def.serviceKey, subtypeKey = tile.subtypeKey)

    private fun persist(ov: EsuvidhaServiceTile) {
        val key = ov.itemKey ?: return
        ov.updatedBy = app?.preferences?.loggedInUser?.name ?: "admin"
        ov.timeStamp = System.currentTimeMillis()
        ov.updatedDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        overrides[key] = ov
        rebuild() // optimistic: reflect immediately
        val updates = hashMapOf<String, Any?>(
            "${Utils.ESUVIDHA_GRID_TABLE}/$key" to ov,
            "${Utils.ESUVIDHA_GRID_BACKUP_TABLE}/$key" to ov
        )
        database.updateChildren(updates) { error: DatabaseError?, _: DatabaseReference? ->
            if (error != null && !checkIfActivityDestroying()) toast("Failed to save: ${error.message}")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
