package com.companion.astrodating.ui.filter.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityFilterBinding
import com.companion.astrodating.ui.filter.adapter.FilterAgeAdapter
import com.companion.astrodating.ui.filter.adapter.FilterCityAdapter
import com.companion.astrodating.ui.filter.adapter.FilterCommunityAdapter
import com.companion.astrodating.ui.filter.adapter.FilterCountryAdapter
import com.companion.astrodating.ui.filter.adapter.FilterEducationAdapter
import com.companion.astrodating.ui.filter.adapter.FilterHeightAdapter
import com.companion.astrodating.ui.filter.adapter.FilterMaritalStatusAdapter
import com.companion.astrodating.ui.filter.adapter.FilterProfessionAdapter
import com.companion.astrodating.ui.filter.adapter.FilterReligionAdapter
import com.companion.astrodating.ui.filter.model.FilterDomainEntity
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.otp.domain.model.CountryDomainEntity
import com.companion.astrodating.ui.otp.viewmodel.CountryViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.BasicDetailList
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.viewmodel.GetMasterDataDetailsViewModel
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.COMMON_AUTH
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.FILTER_REQUEST_CODE
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.FilterConstants
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.setFullscreenWithNavigation
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterActivity : BaseActivity() {
    private val binding by lazy {
        ActivityFilterBinding.inflate(layoutInflater)
    }
    private lateinit var loadingDialog: LoadingDialog
    private val communityAdapter by lazy {
        FilterCommunityAdapter()
    }
    private val countryAdapter by lazy {
        FilterCountryAdapter()
    }
    private val educationAdapter by lazy {
        FilterEducationAdapter()
    }
    private val heightAdapter by lazy {
        FilterHeightAdapter()
    }
    private val ageAdapter by lazy {
        FilterAgeAdapter()
    }
    private val maritalStatusAdapter by lazy {
        FilterMaritalStatusAdapter()
    }
    private val professionAdapter by lazy {
        FilterProfessionAdapter()
    }
    private val religionAdapter by lazy {
        FilterReligionAdapter()
    }
    private val cityAdapter by lazy {
        FilterCityAdapter()
    }
    private val countryViewModel: CountryViewModel by viewModels()
    private val getMasterDataDetailsViewModel: GetMasterDataDetailsViewModel by viewModels()
    private var communityArr: ArrayList<FilterDomainEntity>? = null
    private var countryArr: ArrayList<FilterDomainEntity>? = null
    private var heightArr: ArrayList<FilterDomainEntity>? = null
    private var educationArr: ArrayList<FilterDomainEntity>? = null
    private var professionArr: ArrayList<FilterDomainEntity>? = null
    private var maritalStatusArr: ArrayList<FilterDomainEntity>? = null
    private var ageArr: ArrayList<FilterDomainEntity>? = null
    private var religionArr: ArrayList<FilterDomainEntity>? = null
    private var cityArr: ArrayList<FilterDomainEntity>? = null
    private lateinit var countryDomainList: ArrayList<CountryDomainEntity>
    private var authToken: String = APP_EMPTY_STRING
    private var selectedFilter: String = APP_EMPTY_STRING
    private var isSelectedAllCommunity = false
    private var isSelectedAllEducation = false
    private var isSelectedAllCountry = false
    private var isSelectedAllHeight = false
    private var isSelectedAllAge = false
    private var isSelectedAllStatus = false
    private var isSelectedAllProfession = false
    private var isSelectedAllReligion = false
    private var isSelectedAllCity = false
    var resetMinHeight :String = APP_EMPTY_STRING
    var resetMaxHeight :String = APP_EMPTY_STRING
    var resetMinAge :String = APP_EMPTY_STRING
    var resetMaxAge :String = APP_EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullscreenWithNavigation(this, window)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        initBottomSheetBehavior()
        initData()
        getIntentData()
        handleClickEvents()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun getIntentData() {
        if (intent!=null){

            /*if (intent.hasExtra(FilterConstants.community)) {
                community = intent.getStringArrayListExtra(FilterConstants.community)!!
                Log.e(TAG, "=========community - $community")
            }
            if (intent.hasExtra(FilterConstants.education)) {
                education = intent.getStringArrayListExtra(FilterConstants.education)!!
                Log.e(TAG, "education - $education")
            }
            if (intent.hasExtra(FilterConstants.status)) {
                status = intent.getStringArrayListExtra(FilterConstants.status)!!
                Log.e(TAG, "status - $status")
            }
            if (intent.hasExtra(FilterConstants.profession)) {
                profession = intent.getStringArrayListExtra(FilterConstants.profession)!!
                Log.e(TAG, "profession - $profession")
            }
            if (intent.hasExtra(FilterConstants.religion)) {
                religion = intent.getStringArrayListExtra(FilterConstants.religion)!!
                Log.e(TAG, "religion - $religion")
            }
            if (intent.hasExtra(FilterConstants.minAge)) {
                val minAgeStr = intent.getStringExtra(FilterConstants.minAge)!!
                minAge = if (minAgeStr.isNotEmpty()) {
                    minAgeStr.trim().toInt()
                }else{
                    18
                }
                Log.e(TAG, "minAge - $minAge")
            }*/
        }
    }

    private fun handleClickEvents() {
        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.tvCommunity.setOnClickListener {
            selectedFilter = FilterConstants.community
            with(binding){
                tvCommunity.isSelected = true
                tvEduction.isSelected = false
                tvHeight.isSelected = false
                tvAge.isSelected = false
                tvMaritalStatus.isSelected = false
                tvProfession.isSelected = false
                tvReligion.isSelected = false
                tvCountry.isSelected = false
                tvCity.isSelected = false

                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvCommunity.showVisibility()
                showResetFilterSelection(isSelectedAllCommunity)
                rvEducation.hideVisibility()
                rvHeight.hideVisibility()
                rvAge.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvProfession.hideVisibility()
                rvReligion.hideVisibility()
                rvCountry.hideVisibility()
                rvCity.hideVisibility()
            }
        }

        binding.tvEduction.setOnClickListener {
            selectedFilter = FilterConstants.education
            with(binding){
                tvEduction.isSelected = true
                tvCommunity.isSelected = false
                tvHeight.isSelected = false
                tvAge.isSelected = false
                tvMaritalStatus.isSelected = false
                tvProfession.isSelected = false
                tvReligion.isSelected = false
                tvCountry.isSelected = false
                tvCity.isSelected = false

                showResetFilterSelection(isSelectedAllEducation)
                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvEducation.showVisibility()
                rvCommunity.hideVisibility()
                rvHeight.hideVisibility()
                rvAge.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvProfession.hideVisibility()
                rvReligion.hideVisibility()
                rvCountry.hideVisibility()
                rvCity.hideVisibility()
            }
        }
        binding.tvCountry.setOnClickListener {
            selectedFilter = FilterConstants.country
            with(binding){
                tvCountry.isSelected = true
                tvCommunity.isSelected = false
                tvHeight.isSelected = false
                tvAge.isSelected = false
                tvMaritalStatus.isSelected = false
                tvProfession.isSelected = false
                tvReligion.isSelected = false
                tvEduction.isSelected = false
                tvCity.isSelected = false

                showResetFilterSelection(isSelectedAllCountry)
                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvCountry.showVisibility()
                rvCommunity.hideVisibility()
                rvHeight.hideVisibility()
                rvAge.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvProfession.hideVisibility()
                rvReligion.hideVisibility()
                rvEducation.hideVisibility()
                rvCity.hideVisibility()
            }
        }



        binding.tvHeight.setOnClickListener {
            selectedFilter = FilterConstants.height
            with(binding){
                tvHeight.isSelected = true
                tvCommunity.isSelected = false
                tvEduction.isSelected = false
                tvAge.isSelected = false
                tvMaritalStatus.isSelected = false
                tvProfession.isSelected = false
                tvReligion.isSelected = false
                tvCountry.isSelected = false
                tvCity.isSelected = false
                showResetFilterSelection(isSelectedAllHeight)

                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvHeight.showVisibility()
                rvCommunity.hideVisibility()
                rvEducation.hideVisibility()
                rvAge.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvProfession.hideVisibility()
                rvReligion.hideVisibility()
                rvCountry.hideVisibility()
                rvCity.hideVisibility()
            }
        }
        binding.tvAge.setOnClickListener {
            selectedFilter = FilterConstants.age
            with(binding){
                tvAge.isSelected = true
                tvCommunity.isSelected = false
                tvHeight.isSelected = false
                tvEduction.isSelected = false
                tvMaritalStatus.isSelected = false
                tvProfession.isSelected = false
                tvReligion.isSelected = false
                tvCountry.isSelected = false
                tvCity.isSelected = false
                showResetFilterSelection(isSelectedAllAge)

                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvAge.showVisibility()
                rvCommunity.hideVisibility()
                rvHeight.hideVisibility()
                rvEducation.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvProfession.hideVisibility()
                rvReligion.hideVisibility()
                rvCountry.hideVisibility()
                rvCity.hideVisibility()
            }
        }
        binding.tvMaritalStatus.setOnClickListener {
            selectedFilter = FilterConstants.status
            with(binding){
                tvMaritalStatus.isSelected = true
                tvCommunity.isSelected = false
                tvHeight.isSelected = false
                tvAge.isSelected = false
                tvEduction.isSelected = false
                tvProfession.isSelected = false
                tvReligion.isSelected = false
                tvCountry.isSelected = false
                tvCity.isSelected = false

                showResetFilterSelection(isSelectedAllStatus)
                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvMaritalStatus.showVisibility()
                rvCommunity.hideVisibility()
                rvHeight.hideVisibility()
                rvAge.hideVisibility()
                rvEducation.hideVisibility()
                rvProfession.hideVisibility()
                rvReligion.hideVisibility()
                rvCountry.hideVisibility()
                rvCity.hideVisibility()
            }
        }
        binding.tvProfession.setOnClickListener {
            selectedFilter = FilterConstants.profession
            with(binding){
                tvProfession.isSelected = true
                tvCommunity.isSelected = false
                tvHeight.isSelected = false
                tvAge.isSelected = false
                tvMaritalStatus.isSelected = false
                tvEduction.isSelected = false
                tvReligion.isSelected = false
                tvCountry.isSelected = false
                tvCity.isSelected = false

                showResetFilterSelection(isSelectedAllProfession)
                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvProfession.showVisibility()
                rvCommunity.hideVisibility()
                rvHeight.hideVisibility()
                rvAge.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvEducation.hideVisibility()
                rvReligion.hideVisibility()
                rvCountry.hideVisibility()
                rvCity.hideVisibility()
            }
        }

        binding.tvReligion.setOnClickListener {
            selectedFilter = FilterConstants.religion
            with(binding){
                tvReligion.isSelected = true
                tvCommunity.isSelected = false
                tvHeight.isSelected = false
                tvAge.isSelected = false
                tvMaritalStatus.isSelected = false
                tvProfession.isSelected = false
                tvEduction.isSelected = false
                tvCountry.isSelected = false
                tvCity.isSelected = false

                showResetFilterSelection(isSelectedAllReligion)
                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvReligion.showVisibility()
                rvCommunity.hideVisibility()
                rvHeight.hideVisibility()
                rvAge.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvProfession.hideVisibility()
                rvEducation.hideVisibility()
                rvCountry.hideVisibility()
                rvCity.hideVisibility()
            }
        }

        binding.tvCity.setOnClickListener {
            selectedFilter = FilterConstants.religion
            with(binding){
                tvCity.isSelected = true
                tvCommunity.isSelected = false
                tvHeight.isSelected = false
                tvAge.isSelected = false
                tvMaritalStatus.isSelected = false
                tvProfession.isSelected = false
                tvEduction.isSelected = false
                tvCountry.isSelected = false
                tvReligion.isSelected = false

                showResetFilterSelection(isSelectedAllCity)
                ivFilterItem.showVisibility()
                tvSelectAll.showVisibility()
                rvCity.showVisibility()
                rvCommunity.hideVisibility()
                rvHeight.hideVisibility()
                rvAge.hideVisibility()
                rvMaritalStatus.hideVisibility()
                rvProfession.hideVisibility()
                rvEducation.hideVisibility()
                rvCountry.hideVisibility()
                rvReligion.hideVisibility()
            }
        }

        binding.tvReset.setOnClickListener {
            resetFilter()
        }

        binding.tvSelectAll.setOnClickListener {
            selectAllFilterOption()
        }
        binding.ivFilterItem.setOnClickListener {
            selectAllFilterOption()
        }
        binding.btnApplyFilter.setOnClickListener {
            val community = communityAdapter.getListOfSelectedCommunity()
            val country = countryAdapter.getListOfSelectedCountry()
            val education = educationAdapter.getListOfSelectedEducation()
            val height = heightAdapter.getListOfSelectedHeight()
            val age = ageAdapter.getListOfSelectedAge()
            val status = maritalStatusAdapter.getListOfSelectedMaritalStatus()
            val profession = professionAdapter.getListOfSelectedProfession()
            val religion = religionAdapter.getListOfSelectedReligion()
            val city = cityAdapter.getListOfSelectedCity()
            var minHeight :String = APP_EMPTY_STRING
            var maxHeight :String = APP_EMPTY_STRING
            var minAge :String = APP_EMPTY_STRING
            var maxAge :String = APP_EMPTY_STRING

            if (height!=null && height.size>0){
                 minHeight = height[0].substringBefore("-")
                 maxHeight = height[height.size-1].substringAfter("-").substringBefore("(")
                Log.e(TAG,"min height - $minHeight, max - $maxHeight")

            }
            if (age!=null && age.size>0){
              minAge = age[0].substringBefore("to")
              maxAge = age[age.size-1].substringAfter("to")
                Log.e(TAG,"age - $minAge, max - $maxAge")

            }

            Log.e(TAG,"community - $community")
            Log.e(TAG,"education - $education")
            Log.e(TAG,"height - $height")
            Log.e(TAG,"age - $age")
            Log.e(TAG,"status - $status")
            Log.e(TAG,"profession - $profession")
            Log.e(TAG,"religion - $religion")

            val intent = Intent()
            intent.putStringArrayListExtra(FilterConstants.community, community)
            intent.putStringArrayListExtra(FilterConstants.country, country)
            intent.putStringArrayListExtra(FilterConstants.education, education)
            intent.putExtra(FilterConstants.minHeight, minHeight)
            intent.putExtra(FilterConstants.maxHeight, maxHeight)
            intent.putExtra(FilterConstants.minAge, minAge)
            intent.putExtra(FilterConstants.maxAge, maxAge)
            intent.putStringArrayListExtra(FilterConstants.status, status)
            intent.putStringArrayListExtra(FilterConstants.profession, profession)
            intent.putStringArrayListExtra(FilterConstants.religion, religion)
            intent.putStringArrayListExtra(FilterConstants.city, city)
            intent.putExtra(FROM, "filter")
            setResult(FILTER_REQUEST_CODE, intent)
//            Utility.slideDownScreenNavigation(this)
            finish()
        }


    }

    private fun resetFilter() {
        communityAdapter.clearAll()
        countryAdapter.clearAll()
        educationAdapter.clearAll()
        heightAdapter.clearAll()
        ageAdapter.clearAll()
        maritalStatusAdapter.clearAll()
        professionAdapter.clearAll()
        religionAdapter.clearAll()
        cityAdapter.clearAll()
        val intent = Intent()
        intent.putStringArrayListExtra(FilterConstants.community, ArrayList())
        intent.putStringArrayListExtra(FilterConstants.country, ArrayList())
        intent.putStringArrayListExtra(FilterConstants.education, ArrayList())
        intent.putStringArrayListExtra(FilterConstants.status, ArrayList())
        intent.putStringArrayListExtra(FilterConstants.profession, ArrayList())
        intent.putStringArrayListExtra(FilterConstants.religion, ArrayList())
        intent.putStringArrayListExtra(FilterConstants.city, ArrayList())
        intent.putExtra(FilterConstants.minHeight, resetMinHeight)
        intent.putExtra(FilterConstants.maxHeight, resetMaxHeight)
        intent.putExtra(FilterConstants.minAge, resetMinAge)
        intent.putExtra(FilterConstants.maxAge, resetMaxAge)
        intent.putExtra(FROM, "reset")

        setResult(FILTER_REQUEST_CODE, intent)
//            Utility.slideDownScreenNavigation(this)
        finish()
    }

    private fun selectAllFilterOption() {
        if (selectedFilter == FilterConstants.community) {
            val allSelected = communityArr!!.all { it.isSelected }
            isSelectedAllCommunity = !allSelected
            showFilterSelection(allSelected)
            /*if (!allSelected) {
                binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_selected)
            } else {
                binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_unselected)
            }*/
            communityAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.education) {
            val allSelected = educationArr!!.all { it.isSelected }
            isSelectedAllEducation = !allSelected
            showFilterSelection(allSelected)

            educationAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.country) {
            val allSelected = countryArr!!.all { it.isSelected }
            isSelectedAllCountry = !allSelected
            showFilterSelection(allSelected)

            countryAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.height) {
            val allSelected = heightArr!!.all { it.isSelected }
            showFilterSelection(allSelected)
            heightAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.age) {
            val allSelected = ageArr!!.all { it.isSelected }
            showFilterSelection(allSelected)
            ageAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.status) {
            val allSelected = maritalStatusArr!!.all { it.isSelected }
            showFilterSelection(allSelected)
            maritalStatusAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.profession) {
            val allSelected = professionArr!!.all { it.isSelected }
            showFilterSelection(allSelected)
            professionAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.religion) {
            val allSelected = religionArr!!.all { it.isSelected }
           showFilterSelection(allSelected)
            religionAdapter.selectAll(!allSelected)
        }else if (selectedFilter == FilterConstants.city) {
            val allSelected = cityArr!!.all { it.isSelected }
           showFilterSelection(allSelected)
            cityAdapter.selectAll(!allSelected)
        }


    }
    fun showFilterSelection(allSelected:Boolean){
        if (!allSelected) {
            binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_selected)
        } else {
            binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_unselected)
        }
    }

    fun showResetFilterSelection(allSelected:Boolean){
        if (allSelected) {
            binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_selected)
        } else {
            binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_unselected)
        }
    }

    private fun initData() {
        communityArr = ArrayList()
        heightArr = ArrayList()
        educationArr = ArrayList()
        professionArr = ArrayList()
        maritalStatusArr = ArrayList()
        ageArr = ArrayList()
        countryArr = ArrayList()
        religionArr = ArrayList()
        cityArr = ArrayList()
        countryDomainList = ArrayList()
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }

        var searchStr = ""
        with(binding) {
            rvCommunity.adapter = communityAdapter
            rvEducation.adapter = educationAdapter
            rvCountry.adapter = countryAdapter
            rvHeight.adapter = heightAdapter
            rvAge.adapter = ageAdapter
            rvMaritalStatus.adapter = maritalStatusAdapter
            rvProfession.adapter = professionAdapter
            rvReligion.adapter = religionAdapter
            rvCity.adapter = cityAdapter
        }
        if (isInternetConnection()) {
            countryViewModel.getCountryList(COMMON_AUTH)
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }

        val searchEditText =
            binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        searchEditText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        searchEditText.typeface = ResourcesCompat.getFont(this, R.font.poppins_regular)
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        searchEditText.setHintTextColor(
            ContextCompat.getColor(
                this,
                R.color.colorTextReg
            )
        )

        val searchCloseImageView =
            binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchCloseImageView.setOnClickListener {
            searchEditText.setText("")
            searchStr = ""
//            searchListWithInputValues(searchStr!!)
        }

    }

    override fun initObservers() {
        countryViewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> loadingDialog.showDialog()
                is UiState.Success -> {
                    countryDomainList.clear()
                    countryDomainList.addAll(state.data.list)


                    val selectedCountries = intent.getStringArrayListExtra(FilterConstants.country) ?: arrayListOf()

                    countryArr!!.clear()
                    for (item in countryDomainList){
                        val isSelected = selectedCountries.contains(item.country)
                        countryArr!!.add(FilterDomainEntity(item.country,isSelected))
                    }

                    countryAdapter.notifyDataSetChanged()
//                    getMasterDataDetailsViewModel.getMasterDataDetails(authToken)

                    StorePreferences.saveFilterCountryList(countryArr!!)
                    getMasterDataDetailsViewModel.getMasterDataDetails(authToken)
                }

                is UiState.Error -> loadingDialog.hideDialog()
            }
        }


        getMasterDataDetailsViewModel.getMasterDataDetailsState.observe(this) {
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
//                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    val selectedReligions = intent.getStringArrayListExtra(FilterConstants.religion) ?: arrayListOf()
                    val selectedCommunities = intent.getStringArrayListExtra(FilterConstants.community) ?: arrayListOf()
                    val selectedEducations = intent.getStringArrayListExtra(FilterConstants.education) ?: arrayListOf()
                    val selectedProfessions = intent.getStringArrayListExtra(FilterConstants.profession) ?: arrayListOf()
                    val selectedStatus = intent.getStringArrayListExtra(FilterConstants.status) ?: arrayListOf()
                    val selectedCities = intent.getStringArrayListExtra(FilterConstants.city) ?: arrayListOf()


                    communityArr!!.clear()
                    educationArr!!.clear()
                    heightArr!!.clear()
                    ageArr!!.clear()
                    maritalStatusArr!!.clear()
                    professionArr!!.clear()
                    religionArr!!.clear()
                    cityArr!!.clear()

                    if (BasicDetailList.filterReligionArr !=null && BasicDetailList.filterReligionArr.size>0  ) {
                        for (item in BasicDetailList.filterReligionArr) {
                            val isSelected = selectedReligions.contains(item)
                            religionArr!!.add(FilterDomainEntity(item, isSelected))
                        }
                    }

                    if (BasicDetailList.ageArr !=null && BasicDetailList.ageArr.size>0  ) {
                        for (item in BasicDetailList.ageArr) {
                            ageArr!!.add(FilterDomainEntity(item, false))
                        }
                    }

                    if (BasicDetailList.filterHeightArr !=null && BasicDetailList.filterHeightArr.size>0  ) {
                        for (item in BasicDetailList.filterHeightArr) {
                            heightArr!!.add(FilterDomainEntity(item, false))
                        }
                    }
                    for (item in it.data.masterData) {
                        if (item.type == FilterConstants.community) {
                            val list = item.values
                            for (listItem in list) {
                                    communityArr!!.add(FilterDomainEntity(listItem, selectedCommunities.contains(listItem)))
                                }
                        } else if (item.type == FilterConstants.education) {
                            val list = item.values
                            for (listItem in list) {
                                educationArr!!.add(FilterDomainEntity(listItem, selectedEducations.contains(listItem)))
                            }
                        } else if (item.type == FilterConstants.profession) {
                            val list = item.values
                            for (listItem in list) {
                                professionArr!!.add(FilterDomainEntity(listItem, selectedProfessions.contains(listItem)))
                            }
                        } else if (item.type == FilterConstants.status) {
                            val list = item.values
                            for (listItem in list) {
                                maritalStatusArr!!.add(FilterDomainEntity(listItem, selectedStatus.contains(listItem)))
                            }

                        } else if (item.type == FilterConstants.height) {
                            resetMaxHeight = item.max.toString()
                            resetMinHeight = item.min.toString()
                            StorePreferences.saveFilterMaxHeight(item.max)
                            StorePreferences.saveFilterMinHeight(item.min)
                        }else if (item.type == FilterConstants.age) {
                            resetMaxAge = item.max.toString()
                            resetMinAge = item.min.toString()
                            StorePreferences.saveFilterMaxAge(item.max)
                            StorePreferences.saveFilterMinAge(item.min)
                        } else if (item.type == FilterConstants.city) {
                            val list = item.values
                            for (listItem in list) {
                                cityArr!!.add(FilterDomainEntity(listItem, selectedCities.contains(listItem)))
                            }
                        }

                        /* else if (item.type == "height") {
                            val startHeight = item.min// Starting height
                            val endHeight = item.max
                            val increment = 0.1    // Increment value
                            val numberOfHeights = 10 // Number of heights to generate
                            heightArr!!.add(
                                FilterDomainEntity(
                                    ContextCompat.getString(
                                        this,
                                        R.string.text_filter_selectheight
                                    ), false
                                )
                            )
                            for (j in startHeight until endHeight) {
                                for (i in 0 until numberOfHeights) {
                                    heightArr!!.add(
                                        FilterDomainEntity(
                                            "${j + i * increment}",
                                            false
                                        )
                                    )
                                }
                            }
                        } else if (item.type == "age") {
                            val startAge = item.min// Starting height
                            val endAge = item.max
                            ageArr!!.add(
                                FilterDomainEntity(
                                    ContextCompat.getString(
                                        this,
                                        R.string.text_filter_selectage
                                    ), false
                                )
                            )
                            for (j in startAge until endAge) {
                                ageArr!!.add(FilterDomainEntity("$j", false))
                            }
                        }*/
                    }
                    loadingDialog.hideDialog()
                    initFilterAdapter()
                    religionAdapter.notifyDataSetChanged()
                    countryAdapter.notifyDataSetChanged()
                    educationAdapter.notifyDataSetChanged()
                    professionAdapter.notifyDataSetChanged()
                    maritalStatusAdapter.notifyDataSetChanged()
                    cityAdapter.notifyDataSetChanged()
                }
            }
        }

    }
    private fun initFilterAdapter() {
        if (communityArr!= null && communityArr!!.size>0) {
            communityAdapter.submitData(communityArr!!)
        }
        Log.e(TAG,"countryArr-$countryArr")
        if (countryArr!= null && countryArr!!.size>0) {
            countryAdapter.submitData(countryArr!!)
        }
        if (educationArr!= null && educationArr!!.size>0) {
            educationAdapter.submitData(educationArr!!)
        }
        if (heightArr!= null && heightArr!!.size>0) {
            heightAdapter.submitData(heightArr!!)
        }
        if (ageArr!= null && ageArr!!.size>0) {
            ageAdapter.submitData(ageArr!!)
        }
        if (maritalStatusArr!= null && maritalStatusArr!!.size>0) {
            maritalStatusAdapter.submitData(maritalStatusArr!!)
        }
        if (professionArr!= null && professionArr!!.size>0) {
            professionAdapter.submitData(professionArr!!)
        }
        if (religionArr!= null && religionArr!!.size>0) {
            religionAdapter.submitData(religionArr!!)
        }
        if (cityArr!= null && cityArr!!.size>0) {
            cityAdapter.submitData(cityArr!!)
        }
    }

    private fun initBottomSheetBehavior() {
        val bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.detail_container))
        // Expanded by default
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
//                    Utility.slideDownScreenNavigation(this@AddMoneyBottomDialogFragment)
                    // overridePendingTransition(0, 0)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
//                Utility.slideDownScreenNavigation(this@AddMoneyBottomDialogFragment)
            }
        }

}