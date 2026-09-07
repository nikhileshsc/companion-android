package com.companion.astrodating.ui.policy

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityPrivacyPolicyBinding
import com.companion.astrodating.util.BLOG_WEBVIEW_USERAGENT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.showVisibility

class PrivacyPolicyActivity : BaseActivity() {

    private lateinit var policyUrl: String
    private lateinit var binding: ActivityPrivacyPolicyBinding
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(this)
        setContentView(binding.root)
//        enableEdgeToEdge()
        initData()
    }

    override fun initObservers() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initData() {
        binding.layoutToolbar.toolbarTitle.text = intent.getStringExtra(WEBVIEW_TITLE) ?: getString(R.string.text_profile_privacypolicy)
        binding.layoutToolbar.ivBack.setOnClickListener {
            finish()
        }

        loadingDialog?.showDialog()
        intent.getStringExtra(PRIVACY_POLICY_URL)?.let {
            policyUrl = it
        }

        binding.privacyPolicyView.settings.javaScriptEnabled = true
        binding.privacyPolicyView.settings.userAgentString = BLOG_WEBVIEW_USERAGENT
        binding.privacyPolicyView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.privacyPolicyView.showVisibility()
                loadingDialog?.hideDialog()
            }
        }
        binding.privacyPolicyView.loadUrl(policyUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    companion object {
        const val PRIVACY_POLICY_URL = "policyUrl"
        const val KEY_URL_TERMS_OF_USE = "termsOfUse"
        const val WEBVIEW_TITLE = "webviewTitle"
    }
}