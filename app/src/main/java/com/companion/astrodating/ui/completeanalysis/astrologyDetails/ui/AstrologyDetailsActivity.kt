package com.companion.astrodating.ui.completeanalysis.astrologyDetails.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainEntity
import com.companion.astrodating.data.divineapi.domain.model.P1DomainEntity
import com.companion.astrodating.data.divineapi.domain.model.P1MatchingPlanetaryDomainEntity
import com.companion.astrodating.data.divineapi.viewmodel.GetAstrologyDetailsViewModel
import com.companion.astrodating.data.divineapi.viewmodel.GetMatchingPlanetaryDetailsViewModel
import com.companion.astrodating.databinding.ActivityAstrologyDetailsBinding
import com.companion.astrodating.ui.completeanalysis.astrologyDetails.ui.adapter.AstrologyDetailsAdapter
import com.companion.astrodating.ui.completeanalysis.astrologyDetails.ui.model.AstrologyDetailsData
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ASHTAGUNA_MILAN_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P1_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P2_DATA
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.MANGLIK_DOSHA_DATA
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AstrologyDetailsActivity : BaseActivity() {

    private val binding by lazy {
        ActivityAstrologyDetailsBinding.inflate(layoutInflater)
    }
    private val astrologyDetailsAdapter by lazy {
        AstrologyDetailsAdapter()
    }
    private val getAstrologyDetailsViewModel : GetAstrologyDetailsViewModel by viewModels()
    private val getMatchingPlanetaryDetailsViewModel : GetMatchingPlanetaryDetailsViewModel by viewModels()
    var layoutManager : LinearLayoutManager? = null
    private var divineApiAuthToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2FzdHJvYXBpLTEuZGl2aW5lYXBpLmNvbS9hcGkvYXV0aC1hcGktdXNlciIsImlhdCI6MTcyNTMzODE0NCwibmJmIjoxNzI1MzM4MTQ0LCJqdGkiOiJ4bDYxV044YWJNdW8wd0k1Iiwic3ViIjoiMjQxNSIsInBydiI6ImU2ZTY0YmIwYjYxMjZkNzNjNmI5N2FmYzNiNDY0ZDk4NWY0NmM5ZDcifQ.MJKjWRpsGcodk7snvwzirdF5FwmQAlHdcRQF6uWBIAI"
    private var divineApiKey: String = "e3ca0449fa2ea7701a7ac53fb719c51a"
    private lateinit var loadingDialog: LoadingDialog
    private var astrologyDetailsDataList : ArrayList<AstrologyDetailsData>? = null
    private var p1DomainEntity: P1DomainEntity?=null
    private var p2DomainEntity: P1DomainEntity?=null
    private var p1MatchingPlanetaryDomainEntity: P1MatchingPlanetaryDomainEntity?=null
    private var p2MatchingPlanetaryDomainEntity: P1MatchingPlanetaryDomainEntity?=null
    private var manglikDoshaDomainEntity: ManglikDoshaDomainEntity?=null
    private var ashtakootMilanDomainData: AshtakootMilanDomainDetails.AshtakootMilanDomain? = null
    var p1SignLord :String = APP_EMPTY_STRING
    var p2SignLord :String = APP_EMPTY_STRING
    var p1NakshtraLord :String = APP_EMPTY_STRING
    var p2NakshtraLord :String = APP_EMPTY_STRING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)
        initViewData()
        getIntentData()
        handleClickEvents()
    }
    private fun getIntentData() {
//        StorePreferences.getDivineApiAccessToken()?.let {
//            divineApiAuthToken = it
//        }

//        user1 = intent.getStringExtra(USER_1) ?: APP_EMPTY_STRING
//        user2 = intent.getStringExtra(USER_2) ?: APP_EMPTY_STRING

        if (intent!=null){
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
            if (manglikDoshaDomainEntity!=null){
                if (manglikDoshaDomainEntity!!.content.isNotEmpty()){
                    binding.tvManglikDesc.text = manglikDoshaDomainEntity!!.content
                }
            }
            ashtakootMilanDomainData = Gson().fromJson(
                intent.getStringExtra(ASHTAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                AshtakootMilanDomainDetails.AshtakootMilanDomain::class.java
            )
            if (ashtakootMilanDomainData!!.ashtakoot_milan != null) {

                binding.tvBhakootaDesc.text = ashtakootMilanDomainData!!.ashtakoot_milan.bhakoota.description

                binding.tvNadiDesc.text = ashtakootMilanDomainData!!.ashtakoot_milan.nadi.description
            }
        }
        if (isInternetConnection()) {
            val ashtakootMilanRequestData = StorePreferences.getAshtakootRequestData()
            getMatchingPlanetaryDetailsViewModel.getMatchingPlanetaryDetails(
                divineApiAuthToken, ashtakootMilanRequestData
            )
        } else {
            binding.root.showLongDurationSnackBar(
                resources.getString(
                    R.string.text_no_internet_connection
                )
            )

        }
//        if (p1DomainEntity!=null && p2DomainEntity!=null){
//            setData()
//        }else {
//            val ashtakootMilanRequestData = StorePreferences.getAshtakootRequestData()
//            if (isInternetConnection()) {
//                StorePreferences.saveAshtakootRequestData(ashtakootMilanRequestData)
//                getAstrologyDetailsViewModel.getAstrologyDetails(
//                    divineApiAuthToken, ashtakootMilanRequestData
//                )
//            } else {
//                binding.root.showLongDurationSnackBar(
//                    resources.getString(
//                        R.string.text_no_internet_connection
//                    )
//                )
//
//            }
//        }

    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivToolbarBack.setOnClickListener{
            finish()
        }
    }
    private fun initViewData() {
        astrologyDetailsDataList = ArrayList()

        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.tvTitle.text = getString(R.string.text_completeanalysis_astrology)


        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvTable.layoutManager = layoutManager
        binding.rvTable.adapter = astrologyDetailsAdapter
    }
    override fun initObservers() {
        getAstrologyDetailsViewModel.state.observe(this) {
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
                        p1DomainEntity = it.data.p1
                        p2DomainEntity = it.data.p2
                       setData(p1SignLord, p2SignLord, p1NakshtraLord, p2NakshtraLord)

                    }else if (it.data.success ==2){
                        showErrorDialog("Invalid Gender")

                    }else if (it.data.success ==3){
                        showErrorDialog("Invalid Authorization Token")
                    }

                }
            }
        }

        getMatchingPlanetaryDetailsViewModel.state.observe(this) {
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
                        binding.tvUser1Title1.text = p1DomainEntity!!.full_name
                        binding.tvUser2Title1.text = p2DomainEntity!!.full_name
                        p1MatchingPlanetaryDomainEntity = it.data.p1
                        p2MatchingPlanetaryDomainEntity = it.data.p2
                        if (p1MatchingPlanetaryDomainEntity!=null){
                            for (item in p1MatchingPlanetaryDomainEntity!!.planets){
                                if (item.name == "Moon"){
                                    p1NakshtraLord = item.nakshatra_lord
                                    p1SignLord = item.rashi_lord
                                }
                            }
                            if (p2MatchingPlanetaryDomainEntity!=null){
                                for (item in p2MatchingPlanetaryDomainEntity!!.planets){
                                    if (item.name == "Moon"){
                                        p2NakshtraLord = item.nakshatra_lord
                                        p2SignLord = item.rashi_lord
                                    }
                                }
                                setData(p1SignLord,p2SignLord,p1NakshtraLord,p2NakshtraLord)
                            }

                        }



                    }else if (it.data.success ==2){
                        showErrorDialog("Invalid Gender")

                    }else if (it.data.success ==3){
                        showErrorDialog("Invalid Authorization Token")
                    }

                }
            }
        }

    }

    private fun setData(
        p1SignLord: String,
        p2SignLord: String,
        p1NakshtraLord: String,
        p2NakshtraLord: String
    ) {


        val varna = AstrologyDetailsData(p1DomainEntity!!.varna, getString(R.string.text_astro_attr_col2_varna),p2DomainEntity!!.varna)
        val vashya = AstrologyDetailsData(p1DomainEntity!!.vashya, getString(R.string.text_astro_attr_col2_vashya),p2DomainEntity!!.vashya)
        val yoni = AstrologyDetailsData(p1DomainEntity!!.yoni, getString(R.string.text_astro_attr_col2_yoni),p2DomainEntity!!.yoni)
        val gan = AstrologyDetailsData(p1DomainEntity!!.gana, getString(R.string.text_astro_attr_col2_gan),p2DomainEntity!!.gana)
        val nadi = AstrologyDetailsData(p1DomainEntity!!.nadi, getString(R.string.text_astro_attr_col2_nadi),p2DomainEntity!!.nadi)
        val signLord = AstrologyDetailsData(p1SignLord, getString(R.string.text_astro_attr_col2_signlord),p2SignLord)
        val nakshatra = AstrologyDetailsData(p1DomainEntity!!.nakshatra, getString(R.string.text_astro_attr_col2_nakshatra),p2DomainEntity!!.nakshatra)
        val nakshatraLord = AstrologyDetailsData(p1NakshtraLord, getString(R.string.text_astro_attr_col2_nakshatralord),p2NakshtraLord)
        val charan = AstrologyDetailsData(p1DomainEntity!!.prahar.toString(), getString(R.string.text_astro_attr_col2_charan),p2DomainEntity!!.prahar.toString())
        val tatva = AstrologyDetailsData(p1DomainEntity!!.tatva, getString(R.string.text_astro_attr_col2_tatva),p2DomainEntity!!.tatva)
        val nameAlphabet = AstrologyDetailsData(p1DomainEntity!!.nameAlphabet, getString(R.string.text_astro_attr_col2_namealphabet),p2DomainEntity!!.nameAlphabet)
        val paya = AstrologyDetailsData(p1DomainEntity!!.paya, getString(R.string.text_astro_attr_col2_paya),p2DomainEntity!!.paya)

        astrologyDetailsDataList!!.add(varna)
        astrologyDetailsDataList!!.add(vashya)
        astrologyDetailsDataList!!.add(yoni)
        astrologyDetailsDataList!!.add(gan)
        astrologyDetailsDataList!!.add(nadi)
        astrologyDetailsDataList!!.add(signLord)
        astrologyDetailsDataList!!.add(nakshatra)
        astrologyDetailsDataList!!.add(nakshatraLord)
        astrologyDetailsDataList!!.add(charan)
        astrologyDetailsDataList!!.add(tatva)
        astrologyDetailsDataList!!.add(nameAlphabet)
        astrologyDetailsDataList!!.add(paya)
        astrologyDetailsAdapter.submitData(astrologyDetailsDataList!!)


    }
}