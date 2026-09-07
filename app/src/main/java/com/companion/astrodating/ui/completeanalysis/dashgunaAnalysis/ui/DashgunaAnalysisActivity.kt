package com.companion.astrodating.ui.completeanalysis.dashgunaAnalysis.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.viewmodel.GetDashakootMilanDetailsViewModel
import com.companion.astrodating.databinding.ActivityDashgunaAnalysisBinding
import com.companion.astrodating.ui.completeanalysis.dashgunaAnalysis.ui.adapter.DashakootAnalysisAdapter
import com.companion.astrodating.ui.completeanalysis.dashgunaAnalysis.ui.model.DashakootData
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.DASHAGUNA_MILAN_DATA
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.USER_1
import com.companion.astrodating.util.USER_2
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.formatNumber
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showDialog
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashgunaAnalysisActivity : BaseActivity() {

    private val binding by lazy {
        ActivityDashgunaAnalysisBinding.inflate(layoutInflater)
    }
    private val dashakootAnalysisAdapter by lazy {
        DashakootAnalysisAdapter()
    }
    private var user1: String = APP_EMPTY_STRING
    private var user2: String = APP_EMPTY_STRING
    private val getDashakootMilanDetailsViewModel: GetDashakootMilanDetailsViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private var divineApiAuthToken: String = APP_EMPTY_STRING
    var layoutManager : LinearLayoutManager? = null
    private var dashakootMilanDomainData : DashakootMilanDomainDetails.DashakootMilanDataDomain? =null
    private var dashakootDataList: ArrayList<DashakootData>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        initViewData()
        getIntentData()
        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivToolbarBack.setOnClickListener{
            finish()
        }
        binding.ivDashakootInfo.setOnClickListener {
            showDialog(getString(R.string.text_matchmakingreport_dashakoot_title),getString(R.string.text_matchmakingreport_dashakoot_desc))
        }
    }
    private fun initViewData() {
        dashakootDataList = ArrayList()

        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.tvTitle.text = getString(R.string.text_completeanalysis_dashguna)


        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvTable.layoutManager = layoutManager
        binding.rvTable.adapter = dashakootAnalysisAdapter
    }

    private fun getIntentData() {
        StorePreferences.getDivineApiAccessToken()?.let {
            divineApiAuthToken = it
        }

        if (intent!=null) {
            user1 = intent.getStringExtra(USER_1) ?: APP_EMPTY_STRING
            user2 = intent.getStringExtra(USER_2) ?: APP_EMPTY_STRING
            dashakootMilanDomainData = Gson().fromJson(
                intent.getStringExtra(DASHAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                DashakootMilanDomainDetails.DashakootMilanDataDomain::class.java
            )
        }
        if (dashakootMilanDomainData!=null){
            setData()
        }else {
            val ashtakootMilanRequestData = StorePreferences.getAshtakootRequestData()
            if (isInternetConnection()) {
//            StorePreferences.saveAshtakootRequestData(ashtakootMilanRequestData)
                getDashakootMilanDetailsViewModel.getDashakootMilanDetails(
                    divineApiAuthToken, ashtakootMilanRequestData
                )
            } else {
                binding.root.showLongDurationSnackBar(
                    resources.getString(
                        R.string.text_no_internet_connection
                    )
                )

            }
        }

    }

    override fun initObservers() {
        getDashakootMilanDetailsViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    if (it.data.success ==1){
                        dashakootMilanDomainData = it.data.data
                        setData()

                    }else if (it.data.success ==2){
                        showErrorDialog("Invalid Gender")

                    }else if (it.data.success ==3){
                        showErrorDialog("Invalid Authorization Token")
                    }

                }
            }
        }

    }

    private fun setData() {
        if (user1.isNotEmpty()){
            binding.tvUser1Title.text = user1
        }
        if (user2.isNotEmpty()){
            binding.tvUser2Title.text = user2
        }
        if (dashakootMilanDomainData!!.dashakoot_milan_result!=null){
            val totalPoints = "${formatNumber(dashakootMilanDomainData!!.dashakoot_milan_result.points_obtained)}/${dashakootMilanDomainData!!.dashakoot_milan_result.max_ponits}"
            binding.tvDashakootCount.text = totalPoints
            binding.tvDashakootDesc.text = dashakootMilanDomainData!!.dashakoot_milan_result.content
            binding.tvMarksObtained.text = dashakootMilanDomainData!!.dashakoot_milan_result.points_obtained.toString()
            binding.tvTotalCount.text = dashakootMilanDomainData!!.dashakoot_milan_result.max_ponits.toString()

            val averageDashagunaPoint = dashakootMilanDomainData!!.dashakoot_milan_result.max_ponits / 2.0
            if (dashakootMilanDomainData!!.dashakoot_milan_result.points_obtained >= averageDashagunaPoint){
                binding.ivDashakoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
            }else{
                binding.ivDashakoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
            }
        }
        if (dashakootMilanDomainData!!.dashakoot_milan != null) {
            val dina = DashakootData(
                getString(R.string.text_dashakoot_attr_dina),
                dashakootMilanDomainData!!.dashakoot_milan.dina.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.dina.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.dina.p1,
                dashakootMilanDomainData!!.dashakoot_milan.dina.p2,
                dashakootMilanDomainData!!.dashakoot_milan.dina.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.dina.result
            )
            val gana = DashakootData(
                getString(R.string.text_dashakoot_attr_gana),
                dashakootMilanDomainData!!.dashakoot_milan.gana.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.gana.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.gana.p1,
                dashakootMilanDomainData!!.dashakoot_milan.gana.p2,
                dashakootMilanDomainData!!.dashakoot_milan.gana.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.gana.result
            )
            val yoni = DashakootData(
                getString(R.string.text_dashakoot_attr_yoni),
                dashakootMilanDomainData!!.dashakoot_milan.yoni.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.yoni.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.yoni.p1,
                dashakootMilanDomainData!!.dashakoot_milan.yoni.p2,
                dashakootMilanDomainData!!.dashakoot_milan.yoni.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.yoni.result
            )
            val rashi = DashakootData(
                getString(R.string.text_dashakoot_attr_rashi),
                dashakootMilanDomainData!!.dashakoot_milan.rashi.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.rashi.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.rashi.p1,
                dashakootMilanDomainData!!.dashakoot_milan.rashi.p2,
                dashakootMilanDomainData!!.dashakoot_milan.rashi.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.rashi.result
            )
            val rajju = DashakootData(
                getString(R.string.text_dashakoot_attr_rajju),
                dashakootMilanDomainData!!.dashakoot_milan.rajju.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.rajju.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.rajju.p1,
                dashakootMilanDomainData!!.dashakoot_milan.rajju.p2,
                dashakootMilanDomainData!!.dashakoot_milan.rajju.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.rajju.result
            )
            val rasyadhipati = DashakootData(
                getString(R.string.text_dashakoot_attr_rasyadhipati),
                dashakootMilanDomainData!!.dashakoot_milan.rasyadhipati.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.rasyadhipati.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.rasyadhipati.p1,
                dashakootMilanDomainData!!.dashakoot_milan.rasyadhipati.p2,
                dashakootMilanDomainData!!.dashakoot_milan.rasyadhipati.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.rasyadhipati.result
            )

            val vedha = DashakootData(
                getString(R.string.text_dashakoot_attr_vedha),
                dashakootMilanDomainData!!.dashakoot_milan.vedha.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.vedha.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.vedha.p1,
                dashakootMilanDomainData!!.dashakoot_milan.vedha.p2,
                dashakootMilanDomainData!!.dashakoot_milan.vedha.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.vedha.result
            )
            val vashya = DashakootData(
                getString(R.string.text_dashakoot_attr_vashya),
                dashakootMilanDomainData!!.dashakoot_milan.vashya.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.vashya.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.vashya.p1,
                dashakootMilanDomainData!!.dashakoot_milan.vashya.p2,
                dashakootMilanDomainData!!.dashakoot_milan.vashya.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.vashya.result
            )
            val mahendra = DashakootData(
                getString(R.string.text_dashakoot_attr_mahendra),
                dashakootMilanDomainData!!.dashakoot_milan.mahendra.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.mahendra.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.mahendra.p1,
                dashakootMilanDomainData!!.dashakoot_milan.mahendra.p2,
                dashakootMilanDomainData!!.dashakoot_milan.mahendra.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.mahendra.result
            )
            val streedargha = DashakootData(
                getString(R.string.text_dashakoot_attr_streedargha),
                dashakootMilanDomainData!!.dashakoot_milan.streedargha.area_of_life,
                dashakootMilanDomainData!!.dashakoot_milan.streedargha.max_ponits,
                dashakootMilanDomainData!!.dashakoot_milan.streedargha.p1,
                dashakootMilanDomainData!!.dashakoot_milan.streedargha.p2,
                dashakootMilanDomainData!!.dashakoot_milan.streedargha.points_obtained,
                dashakootMilanDomainData!!.dashakoot_milan.streedargha.result
            )

            dashakootDataList!!.add(dina)
            dashakootDataList!!.add(gana)
            dashakootDataList!!.add(yoni)
            dashakootDataList!!.add(rashi)
            dashakootDataList!!.add(rajju)
            dashakootDataList!!.add(rasyadhipati)
            dashakootDataList!!.add(vedha)
            dashakootDataList!!.add(vashya)
            dashakootDataList!!.add(mahendra)
            dashakootDataList!!.add(streedargha)
            dashakootAnalysisAdapter.submitData(dashakootDataList!!)

        }

    }
}