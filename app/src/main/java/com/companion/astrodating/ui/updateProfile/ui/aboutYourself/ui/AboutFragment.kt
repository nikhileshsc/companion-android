package com.companion.astrodating.ui.updateProfile.ui.aboutYourself.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.companion.astrodating.R
import com.companion.astrodating.databinding.FragmentAboutBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.viewmodel.GetAboutDetailsViewModel
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showDialog
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment(),View.OnClickListener {

    private lateinit var binding: FragmentAboutBinding
    private val getAboutDetailsViewModel : GetAboutDetailsViewModel by viewModels()
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
        binding =  FragmentAboutBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initObserver()
        handleClickEvents()
    }
    private fun handleClickEvents() {
        binding.btnSave.setOnClickListener(this)

    }

    private fun initObserver() {
        getAboutDetailsViewModel.getAboutDetailsState.observe(requireActivity()) {
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
                    binding.etAboutYourself.setText(it.data.aboutYourself)
                    binding.etExpectations.setText(it.data.expectations)
                    binding.etInterests.setText(it.data.interest)
                    afterPopulateFromBackend()
                }
            }
        }

        getAboutDetailsViewModel.updateAboutDetailsState.observe(requireActivity()) {
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
                    binding.etAboutYourself.setText(it.data.aboutYourself)
                    binding.etExpectations.setText(it.data.expectations)
                    binding.etInterests.setText(it.data.interest)
//                    aboutFragment.
                    onSavedSuccessfully()

                    requireActivity().showDialog(ContextCompat.getString(requireContext(),R.string.text_success),ContextCompat.getString(requireContext(),R.string.dialog_edit_profile_success_message)) {
                        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewPager)
                        viewPager.currentItem = 1
                    }

                }
            }
        }


    }

    private fun initData() {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        if (requireActivity().isInternetConnection()) {
            getAboutDetailsViewModel.getAboutDetails(authToken)
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            binding.btnSave ->{
                val aboutYourSelf = binding.etAboutYourself.text.toString()
                val interest = binding.etInterests.text.toString()
                val expectation = binding.etExpectations.text.toString()
                if (aboutYourSelf.isEmpty()){
                    binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_aboutyourself))
                } else if (containsContactInfo(aboutYourSelf)) {
                    binding.root.showShortDurationSnackBar(getString(R.string.error_contains_contact_info))
                } else if (interest.isEmpty()){
                    binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_interest))
                } else if (containsContactInfo(interest)) {
                    binding.root.showShortDurationSnackBar(getString(R.string.error_contains_contact_info))
                } else if (expectation.isEmpty()){
                    binding.root.showShortDurationSnackBar(resources.getString(R.string.error_text_expectation))
                } else if (containsContactInfo(expectation)) {
                    binding.root.showShortDurationSnackBar(getString(R.string.error_contains_contact_info))
                } else {
                    if (requireActivity().isInternetConnection()) {
                        getAboutDetailsViewModel.updateAboutDetails(
                            authToken,
                            aboutYourSelf,
                            interest,
                            expectation,
                            false
                        )
                    } else {
                        binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
                    }
                }
            }
        }
    }

    private fun containsContactInfo(aboutYourSelf: String): Boolean {
        // Regex pattern to detect phone numbers (various formats)
        val phonePattern = Regex("""\b\+?\d{1,3}?[-.\s]?\(?\d{1,4}?\)?[-.\s]?\d{1,4}[-.\s]?\d{1,9}\b""")

        // Regex pattern to detect emails
        val emailPattern = Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b""")

        // List of common social media platform keywords
        val socialPlatforms = listOf(
            "instagram", "facebook", "twitter", "linkedin",
            "snapchat", "tiktok", "whatsapp", "telegram",
            "wechat", "discord", "reddit"
        )

        // Regex pattern for social media handles (e.g., @username)
        val socialIdPattern = Regex("""@[A-Za-z0-9_]+""")

        // Check for phone numbers
        if (phonePattern.containsMatchIn(aboutYourSelf)) return true

        // Check for emails
        if (emailPattern.containsMatchIn(aboutYourSelf)) return true

        // Check for mentions of social platforms (case insensitive)
        if (socialPlatforms.any { it in aboutYourSelf.lowercase() }) return true

        // Check for social media handles
        if (socialIdPattern.containsMatchIn(aboutYourSelf)) return true

        return false
    }

// AboutFragment.kt

    private var initialHadContacts: Boolean = false   // set after loading backend data
    private var isClearedAndSaved: Boolean = false    // set after successful save

    // Call this right after you populate EditTexts from backend:
    private fun afterPopulateFromBackend() {
        initialHadContacts = hasBlockedContactInfo()
        isClearedAndSaved = false
    }

    // Existing checker for current text
    fun hasBlockedContactInfo(): Boolean {
        val about = binding.etAboutYourself.text?.toString().orEmpty()
        val interest = binding.etInterests.text?.toString().orEmpty()
        val expectation = binding.etExpectations.text?.toString().orEmpty()
        return containsContactInfo(about) ||
                containsContactInfo(interest) ||
                containsContactInfo(expectation)
    }

    /**
     * Hard gate used by Activity:
     * - If backend had contacts initially, user MUST save a clean version before leaving.
     * - If backend was clean, just block when current text contains contacts.
     */
    fun requiresStayOnAbout(): Boolean {
        return if (initialHadContacts) {
            !isClearedAndSaved
        } else {
            hasBlockedContactInfo()
        }
    }

    /** Call this from Activity *only on* successful save of About */
    fun onSavedSuccessfully() {
        // Only release the lock if the *saved* text is clean
        if (!hasBlockedContactInfo()) {
            isClearedAndSaved = true
            initialHadContacts = false  // future checks behave like normal
        } else {
            isClearedAndSaved = false
        }
    }


}