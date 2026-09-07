package com.companion.astrodating.ui.updateProfile.ui.basicDetails.ui

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.companion.astrodating.R
import com.companion.astrodating.databinding.FragmentBasicDetailsBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.managePhotos.ui.ManagePhotosActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.BasicDetailList
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.viewmodel.GetBasicDetailsViewModel
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.viewmodel.GetMasterDataDetailsViewModel
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.REGISTRATION
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showDialog
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class BasicDetailsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentBasicDetailsBinding
    private val getBasicDetailsViewModel: GetBasicDetailsViewModel by viewModels()
    private val getMasterDataDetailsViewModel: GetMasterDataDetailsViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private var authToken: String = APP_EMPTY_STRING
    private lateinit var maritalStatusAdapter: ArrayAdapter<String>
    private lateinit var lookingForAdapter: ArrayAdapter<String>
    private lateinit var heightAdapter: ArrayAdapter<String>
    private lateinit var religionAdapter: ArrayAdapter<String>
    private lateinit var communityAdapter: ArrayAdapter<String>
    private lateinit var professionAdapter: ArrayAdapter<String>
    private lateinit var educationAdapter: ArrayAdapter<String>
    private var currentCity: String? = null
    private var communityArr: ArrayList<String>? = null
    private var heightArr: ArrayList<String>? = null
    private var educationArr: ArrayList<String>? = null
    private var professionArr: ArrayList<String>? = null
    private var from : String = APP_EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBasicDetailsBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communityArr = ArrayList()
        heightArr = ArrayList()
        educationArr = ArrayList()
        professionArr = ArrayList()
        initData()
        initAdapter()
        initObserver()
        handleClickEvents()
    }

    private val currentCityAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    place.latLng?.let {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            val addresses: List<Address>? =
                                geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            val cityName: String = addresses!![0].locality
                            currentCity = cityName
                            binding.tvCity.setText(currentCity)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }
            } else {
                binding.root.showShortDurationSnackBar(getString(R.string.text_something_went_wrong))
            }
        }

    private fun initAdapter() {
        maritalStatusAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner, BasicDetailList.maritalStatusArr)
        binding.tvMaritalStatus.setAdapter(maritalStatusAdapter)

        lookingForAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner, BasicDetailList.lookingForArr)
        binding.tvLookingFor.setAdapter(lookingForAdapter)

        heightAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner, heightArr!!)
        binding.tvHeight.setAdapter(heightAdapter)

        religionAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner, BasicDetailList.religionArr)
        binding.tvReligion.setAdapter(religionAdapter)

        communityAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner, communityArr!!)
        binding.tvCommunity.setAdapter(communityAdapter)

        professionAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner, professionArr!!)
        binding.tvProfession.setAdapter(professionAdapter)

        educationAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner, educationArr!!)
        binding.tvEducation.setAdapter(educationAdapter)

    }

    private fun handleClickEvents() {
        binding.tvMaritalStatus.setOnClickListener(this)
        binding.tvLookingFor.setOnClickListener(this)
        binding.tvHeight.setOnClickListener(this)
        binding.tvReligion.setOnClickListener(this)
        binding.tvCommunity.setOnClickListener(this)
        binding.tvCity.setOnClickListener(this)
        binding.tvProfession.setOnClickListener(this)
        binding.tvEducation.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    private fun initObserver() {
        getMasterDataDetailsViewModel.getMasterDataDetailsState.observe(requireActivity()) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    communityArr!!.clear()
                    for (item in it.data.masterData) {
                        if (item.type == "community") {
                            val list = item.values
                            if (list.isEmpty()) {
                                communityArr!!.add("Select your Community")
                            } else {
                                communityArr!!.add("Select your Community")
                                communityArr!!.addAll(list)
                            }
                        } else if (item.type == "education") {
                            val list = item.values
                            if (list.isEmpty()) {
                                educationArr!!.add("Select your Education")
                            } else {
                                educationArr!!.add("Select your Education")
                                educationArr!!.addAll(list)
                            }
                        } else if (item.type == "profession") {
                            val list = item.values
                            if (list.isEmpty()) {
                                professionArr!!.add("Select your Profession")
                            } else {
                                professionArr!!.add("Select your Profession")
                                professionArr!!.addAll(list)
                            }
                        } else if (item.type == "height") {
                            val startHeight = item.min// Starting height
                            val endHeight = item.max
                            val increment = 0.1    // Increment value
                            val numberOfHeights = 10 // Number of heights to generate
                            heightArr!!.add("Select your Height")
                            for (j in startHeight until endHeight) {
                                for (i in 0 until numberOfHeights) {
                                    heightArr!!.add("${j + i * increment}")
                                }
                            }
                        }
                    }
                    initAdapter()
                    if (requireActivity().isInternetConnection()) {
                        getBasicDetailsViewModel.getBasicDetails(authToken)
                    } else {
                        binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                    }
                }
            }
        }

        getBasicDetailsViewModel.getBasicDetailsState.observe(requireActivity()) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    binding.tvMaritalStatus.setText(it.data.status.ifEmpty { BasicDetailList.maritalStatusArr[0] })
                    binding.tvLookingFor.setText(it.data.lookingFor.ifEmpty { BasicDetailList.lookingForArr[0] })
                    binding.tvHeight.setText(it.data.height.ifEmpty { heightArr!![0] })
                    binding.tvReligion.setText(it.data.religion.ifEmpty { BasicDetailList.religionArr[0] })
                    binding.tvCommunity.setText(it.data.community.ifEmpty { communityArr!![0] })
                    binding.tvCity.setText(it.data.currentCity.ifEmpty { "Select your City" })
                    binding.tvProfession.setText(it.data.profession.ifEmpty { professionArr!![0] })
                    binding.tvEducation.setText(it.data.education.ifEmpty { educationArr!![0] })
                    if (from == REGISTRATION){
                        binding.tilCommunity.hideVisibility()
                        binding.tilCity.hideVisibility()
                    }else{
                        binding.tilCommunity.showVisibility()
                        binding.tilCity.showVisibility()
                    }
                    initAdapter()

                }
            }
        }

        getBasicDetailsViewModel.updateBasicDetailsState.observe(requireActivity()) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    binding.tvMaritalStatus.setText(it.data.status)
                    binding.tvLookingFor.setText(it.data.lookingFor)
                    binding.tvHeight.setText(it.data.height)
                    binding.tvReligion.setText(it.data.religion)
                    binding.tvCommunity.setText(it.data.community)
                    binding.tvCity.setText(it.data.currentCity)
                    binding.tvProfession.setText(it.data.profession)
                    binding.tvEducation.setText(it.data.education)
                    requireActivity().showDialog(
                        ContextCompat.getString(requireContext(),R.string.text_success),
                        ContextCompat.getString(requireContext(),R.string.dialog_edit_profile_success_message)
                    ) {
                        if (from == REGISTRATION){
                            StorePreferences.saveUpdateProfileStatus(true)
                            requireActivity().launchScreenAndFinish<ManagePhotosActivity>{
                                putExtra(FROM,REGISTRATION)
                            }
                        }else {
                            val viewPager =
                                requireActivity().findViewById<ViewPager>(R.id.viewPager)
                            viewPager.currentItem = 2
                        }
                    }

                }
            }
        }

    }

    private fun initData() {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }

        if (arguments!=null){
            from = requireArguments().getString(FROM) ?: APP_EMPTY_STRING
            Log.e(TAG,"From - $from")

        }
        if (requireActivity().isInternetConnection()) {
            getMasterDataDetailsViewModel.getMasterDataDetails(authToken)
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
//        val list = StorePreferences.getCommunityList() ?: emptyList()
//        communityArr!!.clear()
//        if (list.isEmpty()) {
//            communityArr!!.add("Select your Community")
//        } else {
//            communityArr!!.add("Select your Community")
//            communityArr!!.addAll(list)
//        }

    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnSave -> {
                val maritalStatus = binding.tvMaritalStatus.text.toString()
                val lookingFor = binding.tvLookingFor.text.toString()
                val height = binding.tvHeight.text.toString()
                val religion = binding.tvReligion.text.toString()
                val community = binding.tvCommunity.text.toString()
                val city = binding.tvCity.text.toString()
                val profession = binding.tvProfession.text.toString()
                val education = binding.tvEducation.text.toString()

                if (maritalStatus == getString(R.string.text_basicdetails_selectyourstatus)) {
                    binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_basicdetails_selectyourstatus))
                } else {
                    if (lookingFor == getString(R.string.text_basicdetails_lookingfor)) {
                        binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_basicdetails_lookingfor))
                    } else {
                        if (height == getString(R.string.text_basicdetails_height)) {
                            binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_basicdetails_height))
                        } else {
                            if (religion == getString(R.string.text_basicdetails_religion)) {
                                binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_basicdetails_religion))
                            } else {
                                if (community == getString(R.string.text_basicdetails_community)) {
                                    binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_basicdetails_community))
                                } else {
                                    if (city == getString(R.string.text_basicdetails_city)) {
                                        binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_basicdetails_city))
                                    } else {
                                        if (profession == getString(R.string.text_basicdetails_profession)) {
                                            binding.root.showShortDurationSnackBar(
                                                resources.getString(
                                                    R.string.error_text_basicdetails_profession
                                                )
                                            )
                                        } else {
                                            if (education == getString(R.string.text_basicdetails_education)) {
                                                binding.root.showShortDurationSnackBar(
                                                    resources.getString(
                                                        R.string.error_text_basicdetails_education
                                                    )
                                                )
                                            } else {
                                                if (requireActivity().isInternetConnection()) {
                                                    getBasicDetailsViewModel.updateBasicDetails(
                                                        authToken,
                                                        maritalStatus,
                                                        lookingFor,
                                                        height.toDouble(),
                                                        religion,
                                                        community,
                                                        city,
                                                        profession,
                                                        education
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

                                    }

                                }

                            }

                        }

                    }

                }
            }

            binding.tvCity -> {
                val placeFields = listOf(
                    Place.Field.ID, Place.Field.NAME,
                    Place.Field.ADDRESS, Place.Field.LAT_LNG
                )
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    placeFields
                ).build(requireContext())

                currentCityAutocomplete.launch(intent)
            }

        }

    }

}