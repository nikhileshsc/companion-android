package com.companion.astrodating.ui.updateProfile.ui.mandatoryDetails

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.companion.astrodating.R
import com.companion.astrodating.databinding.FragmentMandatoryDetailsBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.registration.viewmodel.AddUpdateMandatoryDetailsViewModel
import com.companion.astrodating.ui.registration.viewmodel.GetMandatoryDetailsViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.DATE_FORMAT1
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.EmojiExcludeFilter
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.RegexConstants
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.TIME_FORMAT
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.formatDateForBirthDate
import com.companion.astrodating.util.getAge
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.inputFilter
import com.companion.astrodating.util.invisible
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showDialog
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale


@AndroidEntryPoint
class MandatoryDetailsFragment : Fragment(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentMandatoryDetailsBinding
    private val getMandatoryDetailsViewModel: GetMandatoryDetailsViewModel by viewModels()
    private val addUpdateMandatoryDetailsViewModel: AddUpdateMandatoryDetailsViewModel by viewModels()
    private var selectedGender: String = "Male"
    private var dateOfBirth = ""
    private var timeOfBirth = ""
    private var age = 0
    private var cityOfBirth: String? = null
    private var currentCity: String? = null
    private lateinit var latitudeOfCityOfBirth: String
    private lateinit var longitudeOfCityOfBirth: String
    private lateinit var latitudeOfCurrentCity: String
    private lateinit var longitudeOfCurrentCity: String
    private val calendar by lazy {
        Calendar.getInstance()
    }

    private lateinit var communityAdapter: ArrayAdapter<String>
    private lateinit var loadingDialog: LoadingDialog
    private var authToken: String = APP_EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMandatoryDetailsBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewData()
        initObserver()
        handleClickEvents()
    }

    private fun initObserver() {
        addUpdateMandatoryDetailsViewModel.state.observe(requireActivity()) { it ->
            when (it) {
                UiState.Loading -> loadingDialog.showDialog()
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

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    requireActivity().showDialog(ContextCompat.getString(requireContext(),R.string.text_success),ContextCompat.getString(requireContext(),R.string.dialog_edit_profile_success_message))
                }
            }
        }
        getMandatoryDetailsViewModel.state.observe(requireActivity()) { it ->
            when (it) {
                UiState.Loading -> loadingDialog.showDialog()
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

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    communityAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_spinner, it.data.community)
                    binding.tvCommunity.setAdapter(communityAdapter)

                    StorePreferences.saveCommunityList(it.data.community)
                    StorePreferences.saveUserDetails(it.data.user)
                    binding.etFullName.setText(it.data.user.fullName)
                    if (it.data.user.gender == "Male") {
                        selectedGender = binding.rbGenderMale.text.toString()
                        binding.rbGenderMale.isChecked = true
                        binding.rbGenderMale.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                        binding.rbGenderFemale.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorTextRegGender
                            )
                        )
                        binding.rbGenderOther.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorTextRegGender
                            )
                        )
                    } else if (it.data.user.gender == "Female") {
                        selectedGender = binding.rbGenderFemale.text.toString()
                        binding.rbGenderFemale.isChecked = true
                        binding.rbGenderMale.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorTextRegGender
                            )
                        )
                        binding.rbGenderFemale.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                        binding.rbGenderOther.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorTextRegGender
                            )
                        )
                    } else {
                        selectedGender = binding.rbGenderOther.text.toString()
                        binding.rbGenderOther.isChecked = true
                        binding.rbGenderMale.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorTextRegGender
                            )
                        )
                        binding.rbGenderFemale.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorTextRegGender
                            )
                        )
                        binding.rbGenderOther.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }

                    dateOfBirth = formatDateForBirthDate(it.data.user.birthDate)
                    binding.tvSelectBirthdate.text = dateOfBirth
                    age = it.data.user.age
                    timeOfBirth = it.data.user.timeOfBirth
                    binding.tvSelectBirthTime.text = timeOfBirth
                    cityOfBirth = it.data.user.cityOfBirth
                    binding.tvCityOfBirth.setText(cityOfBirth)
                    binding.tvCommunity.setText(it.data.user.community)
                    currentCity = it.data.user.currentCity
                    binding.tvCurrentCity.setText(currentCity)
                    latitudeOfCityOfBirth = it.data.user.latitudeOfCityOfBirth
                    longitudeOfCityOfBirth = it.data.user.longitudeOfCityOfBirth
                    latitudeOfCurrentCity = it.data.user.latitudeOfCurrentCity
                    longitudeOfCurrentCity = it.data.user.longitudeOfCurrentCity

                    communityAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_spinner, it.data.community)
                    binding.tvCommunity.setAdapter(communityAdapter)


                }
            }
        }
    }

    private var years = 0
    internal val myCalendar = Calendar.getInstance()
    private fun initViewData() {
        binding.etFullName.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(200),
            EmojiExcludeFilter(), inputFilter(RegexConstants.nickNameRegex)
        )
        binding.etFullName.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        getMandatoryDetailsViewModel.getMandatoryDetails(authToken)
    }

    private fun handleClickEvents() {
        binding.rbGenderMale.setOnClickListener(this)
        binding.rbGenderFemale.setOnClickListener(this)
        binding.rbGenderOther.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.tvSelectBirthdate.setOnClickListener(this)
        binding.ivBirthdate.setOnClickListener(this)
        binding.ivBirthTime.setOnClickListener(this)
        binding.tvSelectBirthTime.setOnClickListener(this)
        binding.tvCurrentCity.setOnClickListener(this)
        binding.tvCityOfBirth.setOnClickListener(this)

        binding.etFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val data = p0.toString()
                if (data.length > 1) {
                    binding.tvFullNameWarning.hideVisibility()
                } else {
                    binding.tvFullNameWarning.showVisibility()
                }
            }

        })
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.rbGenderMale -> {
                selectedGender = binding.rbGenderMale.text.toString()
                binding.rbGenderMale.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.rbGenderFemale.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderOther.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextRegGender
                    )
                )
            }

            binding.rbGenderFemale -> {
                selectedGender = binding.rbGenderFemale.text.toString()
                binding.rbGenderMale.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderFemale.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.rbGenderOther.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextRegGender
                    )
                )
            }

            binding.rbGenderOther -> {
                selectedGender = binding.rbGenderOther.text.toString()
                binding.rbGenderMale.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderFemale.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderOther.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }

            binding.tvSelectBirthdate -> {
                showDatePicker()
            }

            binding.ivBirthdate -> {
                showDatePicker()
            }

            binding.tvSelectBirthTime -> {
                showBirthDateTime()
            }

            binding.ivBirthTime -> {
                showBirthDateTime()
            }

            binding.tvCityOfBirth -> {
                val placeFields = listOf(
                    Place.Field.ID, Place.Field.NAME,
                    Place.Field.ADDRESS, Place.Field.LAT_LNG
                )
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    placeFields
                ).setTypeFilter(TypeFilter.CITIES).build(requireContext())

                cityOfBirthAutocomplete.launch(intent)
            }


            binding.tvCurrentCity -> {
                val placeFields = listOf(
                    Place.Field.ID, Place.Field.NAME,
                    Place.Field.ADDRESS, Place.Field.LAT_LNG
                )
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    placeFields
                ).setTypeFilter(TypeFilter.CITIES)
                    .build(requireContext())

                currentCityAutocomplete.launch(intent)
            }

            binding.btnSave -> {
                validateForm()
//               launchScreenAndFinish<HomePageActivity>()
            }

        }

    }

    private fun validateForm() {
        if (binding.etFullName.text.toString().isEmpty()) {
            binding.tvFullNameWarning.showVisibility()
        } else {
            binding.tvFullNameWarning.invisible()
            if (selectedGender.isEmpty()) {
                binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_gender))
            } else {
                if (dateOfBirth.isEmpty()) {
                    binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_birthdate))
                } else {
                    if (timeOfBirth.isEmpty()) {
                        binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_birthtime))
                    } else {
                        if (binding.tvCityOfBirth.text.toString()
                                .isEmpty() || binding.tvCityOfBirth.text.toString() == "City of Birth*"
                        ) {
                            binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_birthcity))
                        } else {
                            if (binding.tvCommunity.text.toString()
                                    .isEmpty() || binding.tvCommunity.text.toString() == "Community*"
                            ) {
                                binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_community))
                            } else {
                                if (binding.tvCurrentCity.text.toString()
                                        .isEmpty() || binding.tvCurrentCity.text.toString() == "Current City*"
                                ) {
                                    binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_current_city))
                                } else {
                                    try {
                                        if (requireActivity().isInternetConnection()) {
                                            addUpdateMandatoryDetailsViewModel.addUpdateMandatoryDetails(
                                                authToken,
                                                binding.etFullName.text.toString(),
                                                selectedGender,
                                                dateOfBirth,
                                                age,
                                                timeOfBirth,
                                                cityOfBirth,
                                                latitudeOfCityOfBirth,
                                                longitudeOfCityOfBirth,
                                                binding.tvCommunity.text.toString(),
                                                currentCity,
                                                latitudeOfCurrentCity,
                                                longitudeOfCurrentCity
                                            )
                                        } else {
                                            binding.root.showLongDurationSnackBar(
                                                resources.getString(
                                                    R.string.text_no_internet_connection
                                                )
                                            )

                                        }
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private val currentCityAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    place.latLng?.let {
                        latitudeOfCurrentCity = it.latitude.toString()
                        longitudeOfCurrentCity = it.longitude.toString()
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            val addresses: List<Address>? =
                                geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            val cityName: String = addresses!![0].locality
                            currentCity = cityName
                            binding.tvCurrentCity.setText(currentCity)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    Log.e(
                        TAG,
                        "Place Lat Long: ${place.latLng?.latitude},${place.latLng?.longitude} "
                    )
                }
            } else {
                binding.root.showShortDurationSnackBar(getString(R.string.text_something_went_wrong))
            }
        }

    private val cityOfBirthAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    place.latLng?.let {
                        latitudeOfCityOfBirth = it.latitude.toString()
                        longitudeOfCityOfBirth = it.longitude.toString()
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            val addresses: List<Address>? =
                                geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            val cityName: String = addresses!![0].locality
                            cityOfBirth = cityName
                            binding.tvCityOfBirth.setText(cityOfBirth)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    Log.e(
                        TAG,
                        "Place Lat Long: ${place.latLng?.latitude},${place.latLng?.longitude} "
                    )
                }
            } else {
                binding.root.showShortDurationSnackBar(getString(R.string.text_something_went_wrong))
            }
        }

    private fun showBirthDateTime() {
        TimePickerDialog(
            requireContext(),
            this,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = GregorianCalendar(year, month, dayOfMonth).time
        if (ageVerification(date)) {
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.error_text_birthdate))
        }
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        val time = "$hourOfDay:$minute"
        timeOfBirth = convertTimeFormat(time)
        binding.tvSelectBirthTime.text = timeOfBirth
    }

    private fun updateLabel() {
        val sdf = SimpleDateFormat(DATE_FORMAT1, Locale.US)
        dateOfBirth = sdf.format(myCalendar.time)
        binding.tvSelectBirthdate.text = dateOfBirth
        age = getAge(dateOfBirth)
    }

    private fun ageVerification(birthDate: Date): Boolean {

        val birthDay = Calendar.getInstance()
        birthDay.timeInMillis = birthDate.time

        //create calendar object for current day
        val currentTime = System.currentTimeMillis()
        val now = Calendar.getInstance()
        now.timeInMillis = currentTime

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)
        return if (years >= 18) {
            true
        } else {
            false
        }
    }

    fun convertTimeFormat(time: String): String {
        var formattedTime = ""
        try {
            val inputFormat: DateFormat = SimpleDateFormat(TIME_FORMAT, Locale.US)
            val timeObj = inputFormat.parse(time)
            Log.d("timeObj", "" + timeObj)
            formattedTime = SimpleDateFormat(TIME_FORMAT, Locale.US).format(timeObj!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedTime
    }

}