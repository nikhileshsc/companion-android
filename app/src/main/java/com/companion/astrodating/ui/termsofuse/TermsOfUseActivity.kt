package com.companion.astrodating.ui.termsofuse

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityTermsOfUseBinding
import com.companion.astrodating.ui.policy.PrivacyPolicyActivity.Companion.KEY_URL_TERMS_OF_USE
import com.companion.astrodating.util.BLOG_WEBVIEW_USERAGENT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.showVisibility

class TermsOfUseActivity : BaseActivity() {

    private lateinit var termsUrl: String
    private lateinit var binding: ActivityTermsOfUseBinding
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityTermsOfUseBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(this)
        setContentView(binding.root)
//        enableEdgeToEdge()
        initData()
    }

    override fun initObservers() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initData() {

        binding.layoutToolbar.toolbarTitle.text = getString(R.string.text_profile_termsofuse)
        binding.layoutToolbar.ivBack.setOnClickListener {
            finish()
        }

        loadingDialog?.showDialog()
        intent.getStringExtra(KEY_URL_TERMS_OF_USE)?.let {
            termsUrl = it
        }

        binding.termsOfUseView.settings.javaScriptEnabled = true
        binding.termsOfUseView.settings.userAgentString = BLOG_WEBVIEW_USERAGENT
        binding.termsOfUseView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.termsOfUseView.showVisibility()
                loadingDialog?.hideDialog()
            }
        }
        binding.termsOfUseView.loadUrl(termsUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    companion object {
        const val TERMS_OF_USE_URL = "termsUrl"
    }
}