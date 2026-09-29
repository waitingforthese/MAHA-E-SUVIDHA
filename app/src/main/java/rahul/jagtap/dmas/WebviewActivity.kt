package rahul.jagtap.dmas

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import rahul.jagtap.dmas.databinding.ActivityWebviewBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.Utils

class WebviewActivity : BaseActivity() {
    lateinit var binding: ActivityWebviewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        try {
            setSupportActionBar(binding.toolbarLayout.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            binding.toolbarLayout.toolbar?.title = intent.getStringExtra("title")
            binding.toolbarLayout.toolbar?.setTitleTextColor(Color.WHITE)
            binding.progressBar?.visible()
            //        webview.setWebViewClient(new MyBrowser());
            val url: String? = intent.getStringExtra("url")
//            webview?.settings?.loadsImagesAutomatically = true
            binding.webview?.settings?.javaScriptEnabled = true
            binding.webview?.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            if (url != null) {
                binding.webview?.loadUrl(url)
            }
            binding.webview?.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar?.visible()
                    binding.webview?.gone()
                }

                override fun onPageCommitVisible(view: WebView, url: String) {
                    super.onPageCommitVisible(view, url)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    binding.progressBar?.gone()
                    binding.webview?.visible()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Utils.hideSoftKeyboard(this@WebviewActivity)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}