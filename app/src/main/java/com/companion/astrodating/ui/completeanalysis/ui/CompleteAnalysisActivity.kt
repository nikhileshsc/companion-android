package com.companion.astrodating.ui.completeanalysis.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainEntity
import com.companion.astrodating.data.divineapi.domain.model.P1DomainEntity
import com.companion.astrodating.databinding.ActivityCompleteAnalysisBinding
import com.companion.astrodating.ui.completeanalysis.ashtagunaAnalysis.ui.AshtakootAnalysisActivity
import com.companion.astrodating.ui.completeanalysis.astrologyDetails.ui.AstrologyDetailsActivity
import com.companion.astrodating.ui.completeanalysis.dashgunaAnalysis.ui.DashgunaAnalysisActivity
import com.companion.astrodating.ui.completeanalysis.matchMakingReport.ui.MatchMakingReportActivity
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ASHTAGUNA_MILAN_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P1_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P2_DATA
import com.companion.astrodating.util.DASHAGUNA_MILAN_DATA
import com.companion.astrodating.util.MANGLIK_DOSHA_DATA
import com.companion.astrodating.util.USER_1
import com.companion.astrodating.util.USER_2
import com.companion.astrodating.util.launchScreen
import com.google.gson.Gson

class CompleteAnalysisActivity : BaseActivity(), View.OnClickListener {
    private val binding by lazy {
        ActivityCompleteAnalysisBinding.inflate(layoutInflater)
    }
    private var user1: String = APP_EMPTY_STRING
    private var user2: String = APP_EMPTY_STRING
    private var ashtakootMillanDomainData: AshtakootMilanDomainDetails.AshtakootMilanDomain? = null
    private var dashakootMilanDomainData : DashakootMilanDomainDetails.DashakootMilanDataDomain? =null
    private var p1DomainEntity: P1DomainEntity?=null
    private var p2DomainEntity: P1DomainEntity?=null
    private var manglikDoshaDomainEntity: ManglikDoshaDomainEntity?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        getIntentData()
        initViewData()
        handleClickEvents()
    }

    override fun initObservers() {

    }

    private fun handleClickEvents() {
        binding.cardAshtagunaAnalysis.setOnClickListener(this)
        binding.cardDashgunaAnalysis.setOnClickListener(this)
        binding.cardAstrologyDetails.setOnClickListener(this)
        binding.cardMatchMakingReport.setOnClickListener(this)
        binding.layoutToolbar.ivBack.setOnClickListener(this)
    }

    private fun initViewData() {

    }

    private fun getIntentData() {
        if (intent != null) {
            ashtakootMillanDomainData = Gson().fromJson(
                intent.getStringExtra(ASHTAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                AshtakootMilanDomainDetails.AshtakootMilanDomain::class.java
            )

            dashakootMilanDomainData = Gson().fromJson(
                intent.getStringExtra(DASHAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                DashakootMilanDomainDetails.DashakootMilanDataDomain::class.java
            )

            p1DomainEntity = Gson().fromJson(
                intent.getStringExtra(ASTROLOGY_DETAILS_P1_DATA) ?: APP_EMPTY_STRING,
                P1DomainEntity::class.java
            )

            p2DomainEntity = Gson().fromJson(
                intent.getStringExtra(ASTROLOGY_DETAILS_P2_DATA) ?: APP_EMPTY_STRING,
                P1DomainEntity::class.java
            )

            manglikDoshaDomainEntity = Gson().fromJson(
                intent.getStringExtra(MANGLIK_DOSHA_DATA) ?: APP_EMPTY_STRING,
                ManglikDoshaDomainEntity::class.java
            )

            user1 = intent.getStringExtra(USER_1) ?: APP_EMPTY_STRING
            user2 = intent.getStringExtra(USER_2) ?: APP_EMPTY_STRING
        }

    }

    override fun onClick(p0: View?) {
        when(p0){
            binding.cardMatchMakingReport ->{
                binding.cardMatchMakingReport.strokeColor = ContextCompat.getColor(this, R.color.colorPrimary)
                binding.cardDashgunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardAshtagunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardAstrologyDetails.strokeColor = ContextCompat.getColor(this, R.color.white)

                if (manglikDoshaDomainEntity != null) {
                    launchScreen<MatchMakingReportActivity> {
                        putExtra(ASHTAGUNA_MILAN_DATA, Gson().toJson(ashtakootMillanDomainData))
                        putExtra(DASHAGUNA_MILAN_DATA, Gson().toJson(dashakootMilanDomainData))
                        putExtra(MANGLIK_DOSHA_DATA, Gson().toJson(manglikDoshaDomainEntity))
                    }
                }

            }

            binding.cardAshtagunaAnalysis ->{
                binding.cardAshtagunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.colorPrimary)
                binding.cardDashgunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardMatchMakingReport.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardAstrologyDetails.strokeColor = ContextCompat.getColor(this, R.color.white)
                if (ashtakootMillanDomainData != null) {
                    launchScreen<AshtakootAnalysisActivity> {
                        putExtra(ASHTAGUNA_MILAN_DATA, Gson().toJson(ashtakootMillanDomainData))
                        putExtra(USER_1, user1)
                        putExtra(USER_2, user2)
                    }
                }

            }

            binding.cardDashgunaAnalysis ->{
                binding.cardDashgunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.colorPrimary)
                binding.cardAshtagunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardMatchMakingReport.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardAstrologyDetails.strokeColor = ContextCompat.getColor(this, R.color.white)
                if (dashakootMilanDomainData != null) {
                    launchScreen<DashgunaAnalysisActivity> {
                        putExtra(DASHAGUNA_MILAN_DATA, Gson().toJson(dashakootMilanDomainData))
                        putExtra(USER_1, user1)
                        putExtra(USER_2, user2)
                    }
                }

            }

            binding.cardAstrologyDetails ->{
                binding.cardAstrologyDetails.strokeColor = ContextCompat.getColor(this, R.color.colorPrimary)
                binding.cardAshtagunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardMatchMakingReport.strokeColor = ContextCompat.getColor(this, R.color.white)
                binding.cardDashgunaAnalysis.strokeColor = ContextCompat.getColor(this, R.color.white)
                if (p1DomainEntity != null && p2DomainEntity !=null) {
                    launchScreen<AstrologyDetailsActivity> {
                        putExtra(ASTROLOGY_DETAILS_P1_DATA, Gson().toJson(p1DomainEntity))
                        putExtra(ASTROLOGY_DETAILS_P2_DATA, Gson().toJson(p2DomainEntity))
                        putExtra(MANGLIK_DOSHA_DATA, Gson().toJson(manglikDoshaDomainEntity))
                        putExtra(ASHTAGUNA_MILAN_DATA, Gson().toJson(ashtakootMillanDomainData))
                        putExtra(USER_1, user1)
                        putExtra(USER_2, user2)
                    }
                }

            }
            binding.layoutToolbar.ivBack ->{
                finish()
            }



        }
    }
}