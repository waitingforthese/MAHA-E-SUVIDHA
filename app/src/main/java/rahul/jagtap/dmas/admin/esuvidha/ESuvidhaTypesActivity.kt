package rahul.jagtap.dmas.admin.esuvidha

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.google.gson.Gson
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaTypesBinding
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ESuvidhaType
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import kotlin.collections.ArrayList

class ESuvidhaTypesActivity : BaseActivity() {
    var list = ArrayList<User>()
    lateinit var binding: ActivityEsuvidhaTypesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // E-Suvidha service-type screen has been discontinued.
        // Close this screen without displaying service categories.
        finish()
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

    companion object
}
