package com.companion.astrodating.ui.completeanalysis.matchMakingReport.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainEntity
import com.companion.astrodating.databinding.ActivityMatchMakingReportBinding
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ASHTAGUNA_MILAN_DATA
import com.companion.astrodating.util.DASHAGUNA_MILAN_DATA
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.MANGLIK_DOSHA_DATA
import com.companion.astrodating.util.formatNumber
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showDialog
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson

class MatchMakingReportActivity : BaseActivity() {
    private val binding by lazy {
        ActivityMatchMakingReportBinding.inflate(layoutInflater)
    }
    private lateinit var loadingDialog: LoadingDialog
    private var ashtakootMillanDomainData: AshtakootMilanDomainDetails.AshtakootMilanDomain? = null
    private var dashakootMilanDomainData: DashakootMilanDomainDetails.DashakootMilanDataDomain? =
        null
    private var manglikDoshaDomainEntity: ManglikDoshaDomainEntity? = null
    private var ashtagunaScore:Double?=null
    private var dashagunaScore:Double?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        initViewData()
        getIntentData()
        handleClickEvents()
    }

    override fun initObservers() {

    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivToolbarBack.setOnClickListener {
            finish()
        }
        binding.ivAshtakootInfo.setOnClickListener {
            showDialog(getString(R.string.text_matchmakingreport_ashtakoot_title),getString(R.string.text_matchmakingreport_ashtakoot_desc))
        }
        binding.ivDashkootInfo.setOnClickListener {
            showDialog(getString(R.string.text_matchmakingreport_dashakoot_title),getString(R.string.text_matchmakingreport_dashakoot_desc))
        }
        binding.ivManglikInfo.setOnClickListener {
            showDialog(getString(R.string.text_matchmakingreport_manglik_title),getString(R.string.text_matchmakingreport_manglik_desc))
        }
    }

    private fun initViewData() {

        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.tvTitle.text =
            getString(R.string.text_purchaseplans_matchmakingreport)

    }

    private fun getIntentData() {
        if (intent != null) {
            dashakootMilanDomainData = Gson().fromJson(
                intent.getStringExtra(DASHAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                DashakootMilanDomainDetails.DashakootMilanDataDomain::class.java
            )
            ashtakootMillanDomainData = Gson().fromJson(
                intent.getStringExtra(ASHTAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                AshtakootMilanDomainDetails.AshtakootMilanDomain::class.java
            )
            manglikDoshaDomainEntity = Gson().fromJson(
                intent.getStringExtra(MANGLIK_DOSHA_DATA) ?: APP_EMPTY_STRING,
                ManglikDoshaDomainEntity::class.java
            )

        }
        if (ashtakootMillanDomainData != null) {
            if (ashtakootMillanDomainData!!.ashtakoot_milan_result != null) {
                if (ashtakootMillanDomainData!!.ashtakoot_milan_result.content != null) {
                    binding.tvAshtakootDesc.text = ashtakootMillanDomainData!!.ashtakoot_milan_result.content
                }
                ashtagunaScore = ashtakootMillanDomainData!!.ashtakoot_milan_result.points_obtained
                val totalPoints = "${formatNumber(ashtagunaScore!!)}/${ashtakootMillanDomainData!!.ashtakoot_milan_result.max_ponits}"
                binding.tvAshtakootCount.text = totalPoints

                val averageAshtagunaPoint = ashtakootMillanDomainData!!.ashtakoot_milan_result.max_ponits / 2.0
                if (ashtagunaScore!! >= averageAshtagunaPoint){
                    binding.ivAshtakoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                }else{
                    binding.ivAshtakoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                }
            }

        }

        if (dashakootMilanDomainData != null) {
            if (dashakootMilanDomainData!!.dashakoot_milan_result != null) {
                if (dashakootMilanDomainData!!.dashakoot_milan_result.content != null) {
                    binding.tvDashkootDesc.text = dashakootMilanDomainData!!.dashakoot_milan_result.content
                }
                dashagunaScore = dashakootMilanDomainData!!.dashakoot_milan_result.points_obtained
                val totalPoints = "${formatNumber(dashagunaScore!!)}/${dashakootMilanDomainData!!.dashakoot_milan_result.max_ponits}"
                binding.tvDashkootCount.text = totalPoints

                val averageDashagunaPoint = dashakootMilanDomainData!!.dashakoot_milan_result.max_ponits / 2.0
                if (dashagunaScore!! >= averageDashagunaPoint){
                    binding.ivDashkoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                }else{
                    binding.ivDashkoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                }

                val overallScore = (ashtagunaScore!! + dashagunaScore!!) / 2
                binding.tvProgress.text = "${overallScore}/${dashakootMilanDomainData!!.dashakoot_milan_result.max_ponits}"
                binding.progressIndicator.progress = overallScore.toInt()
            }

        }

        if (manglikDoshaDomainEntity != null) {
            if (manglikDoshaDomainEntity!!.content.isNotEmpty()) {
                binding.tvManglikDesc.text = manglikDoshaDomainEntity!!.content
            }
        }
        if (manglikDoshaDomainEntity!!.p1.manglik_dosha && manglikDoshaDomainEntity!!.p2.manglik_dosha){
            binding.ivManglik.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
        }else if (!manglikDoshaDomainEntity!!.p1.manglik_dosha && !manglikDoshaDomainEntity!!.p2.manglik_dosha){
            binding.ivManglik.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
        }else{
            binding.ivManglik.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))

        }

    }

}