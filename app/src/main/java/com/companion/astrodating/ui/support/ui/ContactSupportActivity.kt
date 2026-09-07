package com.companion.astrodating.ui.support.ui

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityContactSupportBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.support.requestData.ContactSupportRequestData
import com.companion.astrodating.ui.support.viewmodel.GetContactSupportViewModel
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ContactSupportActivity : BaseActivity() {

    private val binding by lazy {
        ActivityContactSupportBinding.inflate(layoutInflater)
    }
    private var loadingDialog: LoadingDialog? = null
    private lateinit var problemTypeAdapter: ArrayAdapter<String>
    private val getContactSupportViewModel: GetContactSupportViewModel by viewModels()
    val listOfProblemType = arrayListOf(
        "Need Help",
        "Report A Bug",
        "Feedback",
        "Need Refund",
        "Other"
    )
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        initView()
        handleClickEvents()
    }

    override fun initObservers() {
        getContactSupportViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog?.hideDialog()
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
                    loadingDialog?.showDialog()
                }
                is UiState.Success -> {
                    loadingDialog?.hideDialog()
                    showLoggedOutDialog(getString(R.string.text_success),
                        getString(R.string.dialog_ticket_success_message)) {
                        finish()
                    }
                }
            }
        }

    }

    private fun initView() {
        binding.layoutToolbar.toolbarTitle.text = getString(R.string.text_contactsupport_title)
        problemTypeAdapter =
            ArrayAdapter(this, R.layout.dropdown_layout_spinner, listOfProblemType)
        binding.actvProblemType.setAdapter(problemTypeAdapter)

    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.btnSubmit.setOnClickListener {
            if (binding.actvProblemType.text.toString() == getString(R.string.text_contactsupport_selectproblemtype)) {
                binding.root.showLongDurationSnackBar(getString(R.string.text_contactsupport_selectproblemtype))
            } else {
                getContactSupportViewModel.createNewTicket(
                    ContactSupportRequestData(
                        problemType = binding.actvProblemType.text.toString(),
                        description = binding.etProblemDesc.text.toString(),
                        attachment = "",
                    )
                )
            }
        }
        binding.tvAttachFile.setOnClickListener {
            chooseImageLauncher.launch("image/*")
        }



    }

    private fun uploadContactSupportImage() {
        if (isInternetConnection()) {
//            getContactSupportViewModel.uploadSupportImage(fileType = "support", getMultiPartBody())
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
    }

    private fun getMultiPartBody(): MultipartBody.Part {
        val filesDir = applicationContext.filesDir
        val file = File(filesDir, "contactSupport.png")
        val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)
        inputStream.close()
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    private val chooseImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let {
                imageUri = it
               /* binding.tvAttachment.hideVisibility()
                binding.tvAttachmentLabel.showVisibility()
                binding.ivSupport.showVisibility()
                binding.tvSelectAnother.showVisibility()
                Glide.with(this)
                    .load(result)
                    .into(binding.ivSupport)*/
            }
        }


}