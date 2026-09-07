package com.companion.astrodating.ui.registration.ui

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
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityRegistrationBinding
import com.companion.astrodating.ui.home.ui.HomePageActivity
import com.companion.astrodating.ui.notification.ui.PushNotificationActivity
import com.companion.astrodating.ui.otp.ui.request.RequestOtpActivity
import com.companion.astrodating.ui.registration.viewmodel.AddUpdateMandatoryDetailsViewModel
import com.companion.astrodating.ui.registration.viewmodel.GetMandatoryDetailsViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.UpdateProfileActivity
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.DATE_FORMAT1
import com.companion.astrodating.util.EmojiExcludeFilter
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.REGISTRATION
import com.companion.astrodating.util.RegexConstants
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.TIME_FORMAT
import com.companion.astrodating.util.getAge
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.inputFilter
import com.companion.astrodating.util.invisible
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
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
class RegistrationActivity : BaseActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val binding by lazy {
        ActivityRegistrationBinding.inflate(layoutInflater)
    }
    private val getMandatoryDetailsViewModel: GetMandatoryDetailsViewModel by viewModels()
    private val addUpdateMandatoryDetailsViewModel: AddUpdateMandatoryDetailsViewModel by viewModels()
    private var selectedGender: String = "Male"
    private var dateOfBirth = ""
    private var timeOfBirth = "12:00"
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
    private lateinit var dialog: LoadingDialog
    private var authToken: String = APP_EMPTY_STRING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        dialog = LoadingDialog(this)
        initViewData()
        handleClickEvents()

    }

    private var years = 0
    internal val myCalendar = Calendar.getInstance()
    private fun initViewData() {
        binding.etFullName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(200),
            EmojiExcludeFilter(),inputFilter(RegexConstants.nickNameRegex))
        binding.etFullName.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        if (isInternetConnection()) {
            getMandatoryDetailsViewModel.getMandatoryDetails(authToken)
        }else {
            binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
        }
    }

    private fun handleClickEvents() {
        binding.rbGenderMale.setOnClickListener(this)
        binding.rbGenderFemale.setOnClickListener(this)
        binding.rbGenderOther.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.tvSelectBirthdate.setOnClickListener(this)
        binding.ivBirthdate.setOnClickListener(this)
        binding.ivBirthTime.setOnClickListener(this)
        binding.tvSelectBirthTime.setOnClickListener(this)
        binding.tvCurrentCity.setOnClickListener(this)
        binding.tvCityOfBirth.setOnClickListener(this)
//        binding.tvBirthLocation.setOnClickListener(this)
//        binding.ivBack.setOnClickListener(this)

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

    override fun initObservers() {
        addUpdateMandatoryDetailsViewModel.state.observe(this) { state ->
            when (state) {
                UiState.Loading -> showLoading()
                is UiState.Error -> showError(state.error)
                is UiState.Success -> {
                    val profileUrl = state.data.profileUrl
                    StorePreferences.saveProfileUrl(profileUrl)
                    StorePreferences.saveRegistrationStatus(state.data.isRegistrationCompleted)
                    if (profileUrl!=null && profileUrl.isNotEmpty()) {
                        loadData()
                    }else{
                        launchScreenAndFinish<UpdateProfileActivity>{
                            putExtra(FROM,REGISTRATION)
                        }
                    }
                }
            }
        }
        getMandatoryDetailsViewModel.state.observe(this) { state ->
            when (state) {
                UiState.Loading -> showLoading()
                is UiState.Error -> showError(state.error)
                is UiState.Success -> {
                    dialog.hideDialog()
                    communityAdapter =
                        ArrayAdapter(this, R.layout.item_spinner, state.data.community)
                    binding.tvCommunity.setAdapter(communityAdapter)
                    StorePreferences.saveCommunityList(state.data.community)
                    StorePreferences.saveUserDetails(state.data.user)
                }
            }
        }
    }

    private fun showLoading() {
        dialog.showDialog()
    }

    private fun showError(errorMsg: String) {
        dialog.hideDialog()
        binding.root.showShortDurationSnackBar(errorMsg)
    }

    private fun loadData() {
        Log.e(TAG, "loadData: ")
        dialog.hideDialog()
        goToHomePage()
    }

    private fun goToHomePage() {
        if (NotificationManagerCompat.from(this)
                .areNotificationsEnabled()
        ) {
            launchScreenAndFinish<HomePageActivity>(){
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchScreenAndFinish<PushNotificationActivity>()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.rbGenderMale -> {
                selectedGender = binding.rbGenderMale.text.toString()
                binding.rbGenderMale.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.white
                    )
                )
                binding.rbGenderFemale.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderOther.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.colorTextRegGender
                    )
                )
            }

            binding.rbGenderFemale -> {
                selectedGender = binding.rbGenderFemale.text.toString()
                binding.rbGenderMale.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderFemale.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.white
                    )
                )
                binding.rbGenderOther.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.colorTextRegGender
                    )
                )
            }

            binding.rbGenderOther -> {
                selectedGender = binding.rbGenderOther.text.toString()
                binding.rbGenderMale.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderFemale.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
                        R.color.colorTextRegGender
                    )
                )
                binding.rbGenderOther.setTextColor(
                    ContextCompat.getColor(
                        this@RegistrationActivity,
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
                ).setTypeFilter(TypeFilter.CITIES).build(this)

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
                ).build(this)

                currentCityAutocomplete.launch(intent)
            }

            binding.btnSubmit -> {
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
//                    if (timeOfBirth.isEmpty()) {
//                        binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_birthtime))
//                    } else {
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
                                    if (isInternetConnection()) {
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
                                        binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
//            }

                                    }
                                }
                            }
                        }
//                    }
                }
            }
        }


//            || selectedGender.isEmpty()
//            || dateOfBirth.isEmpty()
//            || dateOfTime.isEmpty()
//            || binding.etBirthLocation.text.toString().isEmpty()
//        ) {
//            binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_fill_reg_details))
//        } else {
//            if (isInternetConnection()) {
////                       registerViewModel.registerUser(
////                           authToken,
////                           binding.etFullName.text.toString(),
////                           selectedGender,
////                           dateOfBirth,
////                           dateOfTime,
////                           binding.etBirthLocation.text.toString(),
////                           latitude,
////                           longitude
////                       )
//            } else {
////                binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
////            }
//        }
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
                        val geocoder = Geocoder(this, Locale.getDefault())
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
                        val geocoder = Geocoder(this, Locale.getDefault())
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

    private fun moveToPreviousScreen() {
        launchScreenAndFinish<RequestOtpActivity>()
    }

    private fun showBirthDateTime() {
        TimePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
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
//        calendar.set(year, month, dayOfMonth)
//        SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).also {
//            dateOfBirth = it.format(calendar.time)
//        }
//        binding.tvSelectBirthdate.text = buildString {
//            append(year)
//            append(" - ")
//            append(month + 1)
//            append(" - ")
//            append(dayOfMonth)
//        }
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

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val time = "$hourOfDay:$minute"
//        val sdfInput = SimpleDateFormat(TIME_FORMAT, Locale.US)
//        val sdfOutput = SimpleDateFormat(TIME_FORMAT, Locale.US)
        timeOfBirth = convertTimeFormat(time)
        binding.tvSelectBirthTime.text = timeOfBirth
    }

    override fun onPause() {
        super.onPause()
        dialog.hideDialog()
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

    fun convertTimeFormat(time:String):String{
        var formattedTime=""
        try {
            val inputFormat: DateFormat = SimpleDateFormat(TIME_FORMAT, Locale.US)
            val timeObj = inputFormat.parse(time)
            Log.d("timeObj",""+timeObj)
            formattedTime=SimpleDateFormat(TIME_FORMAT, Locale.US).format(timeObj!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedTime
    }

}