package rahul.jagtap.dmas.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaOptionsDynamicTypesBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class ESuvidhaOptionsDynamicTypesActivity : BaseActivity() {
    private lateinit var binding: ActivityEsuvidhaOptionsDynamicTypesBinding
    var typesMap: HashMap<String, HashMap<String, HashMap<String, String>>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // E-Suvidha dynamic service-type management has been discontinued.
        // Close this screen without loading service types or opening edit screens.
        finish()
    }

    private fun redirectToNextScreen(map: java.util.HashMap<String, HashMap<String, String>>?, typeTitle: String, nodeName: String) {
        val intent = Intent(mContext, EditESuvidhaDynamicTypesActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("hashMap", map)
        bundle.putString("typeTitle", typeTitle)
        bundle.putString("nodeName", nodeName)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    private fun fetchDynamicTypes() {
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.esuvidhaDynamicTypes?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar?.gone()
                binding.llButtons.visible()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        return
                    }
                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>?>() {}.type
                    typesMap = Gson().fromJson(json, type)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                Log.e("in", "failure")
            }
        })
    }

//    private fun isAdminOrEmployee(): Boolean = app?.preferences?.loggedInUser?.isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2"

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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            fetchDynamicTypes()
        }
    }

    companion object {

    }
}
