package br.com.codiub.cemiterio

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var errorLayout: LinearLayout

    // URL oficial do sistema de consulta de sepultados de Uberaba (Codiub)
    private val siteUrl = "https://app3.codiub.com.br/consulta_sepultados/#/sepultados"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        errorLayout = findViewById(R.id.errorLayout)

        val retryButton: Button = findViewById(R.id.retryButton)
        retryButton.setOnClickListener { loadSite() }

        setupWebView()

        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        if (savedInstanceState == null) {
            loadSite()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true          // necessário: o site é uma aplicação Angular
            domStorageEnabled = true          // necessário: Angular usa localStorage/sessionStorage
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress in 1..99) ProgressBar.VISIBLE else ProgressBar.GONE
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                // Mantém a navegação dentro do app enquanto for o domínio do Codiub
                return if (url.contains("codiub.com.br")) {
                    false
                } else {
                    // Links externos (ex: telefone, e-mail, outros sites) abrem fora do app
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (_: Exception) { }
                    true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipeRefresh.isRefreshing = false
                progressBar.visibility = ProgressBar.GONE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: android.webkit.WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    showError()
                }
            }
        }
    }

    private fun loadSite() {
        if (isNetworkAvailable()) {
            hideError()
            webView.loadUrl(siteUrl)
        } else {
            showError()
        }
    }

    private fun showError() {
        errorLayout.visibility = android.view.View.VISIBLE
        swipeRefresh.visibility = android.view.View.GONE
        swipeRefresh.isRefreshing = false
    }

    private fun hideError() {
        errorLayout.visibility = android.view.View.GONE
        swipeRefresh.visibility = android.view.View.VISIBLE
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Permite que o botão "voltar" do Android navegue pelo histórico do site
    // (ex: voltar da tela de resultado/popup para a busca) antes de fechar o app
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
