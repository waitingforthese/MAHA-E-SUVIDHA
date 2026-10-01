package rahul.jagtap.dmas

import android.content.Intent
import android.os.Bundle
import rahul.jagtap.dmas.admin.bills.BillDatesActivity

/**
 * Compatibility entry point used by the Scan Bills screen.
 * Bills are stored as bills/date/uid/pushKey, so use the shared date-based browser.
 */
class ViewShopBillsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, BillDatesActivity::class.java))
        finish()
    }
}
