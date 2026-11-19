package id.my.notmemc.tools

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvHeaderTitle: TextView

    private lateinit var tabTop4Top: Button
    private lateinit var tabTikSave: Button
    private lateinit var tabYTSave: Button

    private val prefs by lazy { getSharedPreferences("notme_tools_prefs", Context.MODE_PRIVATE) }

    private fun getTop4TopUrl(): String =
        prefs.getString("url_top4top", "https://t4tup.notmemc.my.id") ?: "https://t4tup.notmemc.my.id"

    private fun getTikSaveUrl(): String =
        prefs.getString("url_tiksave", "https://tiksave.notmemc.my.id") ?: "https://tiksave.notmemc.my.id"

    private fun getYTSaveUrl(): String =
        prefs.getString("url_ytsave", "https://ytsave.notmemc.my.id") ?: "https://ytsave.notmemc.my.id"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)

        tabTop4Top = findViewById(R.id.tabTop4Top)
        tabTikSave = findViewById(R.id.tabTikSave)
        tabYTSave = findViewById(R.id.tabYTSave)

        val btnSettings: ImageButton = findViewById(R.id.btnSettings)

        setupWebView()

        swipeRefresh.setColorSchemeColors(Color.parseColor("#10B981"))
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        tabTop4Top.setOnClickListener { switchTab(0) }
        tabTikSave.setOnClickListener { switchTab(1) }
        tabYTSave.setOnClickListener { switchTab(2) }
        btnSettings.setOnClickListener { showUrlSettingsDialog() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })

        // Default tab: Top4Top Auto-Uploader
        switchTab(0)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                swipeRefresh.isRefreshing = false
                progressBar.visibility = View.GONE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }

        // Tangani unduhan langsung MP4 / MP3 lewat DownloadManager Android
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            try {
                val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
                val request = DownloadManager.Request(Uri.parse(url)).apply {
                    setMimeType(mimeType)
                    addRequestHeader("User-Agent", userAgent)
                    setDescription("Mengunduh file via NotMe Tools...")
                    setTitle(fileName)
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                }
                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(this, "Mengunduh: $fileName", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal memulai unduhan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchTab(index: Int) {
        val activeColor = Color.parseColor("#34D399")
        val inactiveColor = Color.parseColor("#A1A1AA")

        tabTop4Top.setTextColor(if (index == 0) activeColor else inactiveColor)
        tabTikSave.setTextColor(if (index == 1) activeColor else inactiveColor)
        tabYTSave.setTextColor(if (index == 2) activeColor else inactiveColor)

        when (index) {
            0 -> {
                tvHeaderTitle.text = "NotMe Tools • Top4Top MP3"
                webView.loadUrl(getTop4TopUrl())
            }
            1 -> {
                tvHeaderTitle.text = "NotMe Tools • TikSave"
                webView.loadUrl(getTikSaveUrl())
            }
            2 -> {
                tvHeaderTitle.text = "NotMe Tools • YTSave"
                webView.loadUrl(getYTSaveUrl())
            }
        }
    }

    private fun showUrlSettingsDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val inputTop4Top = EditText(this).apply {
            hint = "URL Top4Top Auto-Uploader"
            setText(getTop4TopUrl())
        }
        val inputTikSave = EditText(this).apply {
            hint = "URL TikSave Downloader"
            setText(getTikSaveUrl())
        }
        val inputYTSave = EditText(this).apply {
            hint = "URL YTSave Downloader"
            setText(getYTSaveUrl())
        }

        layout.addView(TextView(this).apply { text = "URL Server Top4Top MP3:" })
        layout.addView(inputTop4Top)
        layout.addView(TextView(this).apply { text = "
URL Server TikSave:" })
        layout.addView(inputTikSave)
        layout.addView(TextView(this).apply { text = "
URL Server YTSave:" })
        layout.addView(inputYTSave)

        AlertDialog.Builder(this)
            .setTitle("Konfigurasi Endpoint Server")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                prefs.edit()
                    .putString("url_top4top", inputTop4Top.text.toString().trim())
                    .putString("url_tiksave", inputTikSave.text.toString().trim())
                    .putString("url_ytsave", inputYTSave.text.toString().trim())
                    .apply()
                Toast.makeText(this, "URL diperbarui!", Toast.LENGTH_SHORT).show()
                webView.reload()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
