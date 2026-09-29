package rahul.jagtap.dmas.admin.esuvidha

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.*
import rahul.jagtap.dmas.admin.TextMsgActivity
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.*
import rahul.jagtap.dmas.utils.Utils
import java.lang.reflect.Type
import java.util.*

class RailwayTicketBookingListActivity : BaseActivity() {
    var list = ArrayList<RailwayTicketBooking>()
    private var creator: User? = null
    var adapter: RailwayTicketBookingListAdapter? = null
    var paymentStatusChanged = false

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_railway_ticket_booking)

        val json = intent.getStringExtra("data")
        val type: Type = object : TypeToken<MutableList<RailwayTicketBooking>>() {}.type
        val dataList: MutableList<RailwayTicketBooking> = Gson().fromJson(json, type)
        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        creator = Gson().fromJson<User>(creatorJsonString, User::class.java)
        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = RailwayTicketBookingListAdapter(mContext, list)
        adapter?.itemClickListener = object : RailwayTicketBookingListAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                val selPanCard = list[position]

                val closingUpdateList = Utils.closingUpdateList
                MaterialDialog.Builder(mContext!!).items(closingUpdateList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        dialog?.dismiss()
                        performDelete(selPanCard, closingUpdateList[position])
                    }
                }.show()
            }

            override fun onDownloadAll(position: Int, isCreateFolderClicked: Boolean) {

            }

            override fun onMarkAsUnPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "2"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("railway_ticket_booking").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("railway_ticket_booking").child(item.pushKey!!).setValue(item)
                }
            }

            override fun onMarkAsPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "1"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("railway_ticket_booking").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("railway_ticket_booking").child(item.pushKey!!).setValue(item)
                }
            }

            override fun onCallOrSms(phoneNo: String) {
                callOrSms(phoneNo)
            }

            override fun onCallOrSmsCreator() {
                creator?.contactNo?.let { callOrSms(it) }
            }
        }
        binding.recyclerView?.adapter = adapter

        if (dataList != null && dataList.size > 0) {
            list.addAll(dataList)
            notifyAdapter()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
    }

    private fun performDelete(selPanCard: RailwayTicketBooking, closing_update: String) {
        // Delete Aadhar file
        storageRef.child("esuvidha/${selPanCard.email}/railway_ticket_booking/${selPanCard.aadharPhotoFileName}").delete()

        selPanCard.closingUpdate = closing_update
        // Update record in Backup DB
        Utils.dmyHmsTodmy(selPanCard.createdDateTime)?.let { formattedDate ->
            database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(selPanCard.uid!!).child("railway_ticket_booking").child(selPanCard.pushKey!!).setValue(selPanCard)
        }

        // Delete DB Record
        Utils.dmyHmsTodmy(selPanCard.createdDateTime)?.let { formattedDate ->
            database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(selPanCard.uid!!).child("railway_ticket_booking").child(selPanCard.pushKey!!).removeValue()
        }

        toast("Record deleted successfully")
        startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    private fun notifyAdapter() {
        binding.progressBar?.gone()
        adapter?.notifyDataSetChanged()
        if (list.size == 0) {
            binding.tvError?.visible()
        } else {
            binding.tvError?.gone()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.e("in", "onsoftback")
                Utils.hideSoftKeyboard(this)
                if (paymentStatusChanged) {
                    startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
                finish()
                return true
            }
            R.id.action_delete_all -> {
                deleteAll()
                return true
            }
            R.id.action_text_msg -> {
                startActivity(Intent(mContext, TextMsgActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isAdmin(): Boolean = app?.preferences?.loggedInUser?.isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2"

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_all, menu)
        val item = menu?.findItem(R.id.action_delete_all)
        item?.isVisible = isAdmin()
        val itemTextMsg = menu?.findItem(R.id.action_text_msg)
        itemTextMsg?.isVisible = isAdmin()
        return true
    }

    private fun deleteAll() {
        MaterialDialog.Builder(mContext!!).items(Utils.closingUpdateList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
            run {
                dialog?.dismiss()
                list.forEach { item ->
                    // Delete Aadhar file
                    storageRef.child("esuvidha/${item.email}/railway_ticket_booking/${item.aadharPhotoFileName}").delete()

                    item.closingUpdate = text.toString()
                    // Update record in Backup DB
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("railway_ticket_booking").child(item.pushKey!!).setValue(item)
                    }

                    // Delete DB Record
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("railway_ticket_booking").child(item.pushKey!!).removeValue()
                    }
                }
                toast("Records deleted successfully")
                startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }.show()
    }

    override fun onBackPressed() {
        Log.e("in", "onBackPressed")
        if (paymentStatusChanged) {
            startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
            return
        }
        super.onBackPressed()
    }

    companion object {

    }
}
