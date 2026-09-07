package com.companion.astrodating.ui.requestToDeleteData.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityRequestToDeleteDataBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.support.requestData.ContactSupportRequestData
import com.companion.astrodating.ui.support.viewmodel.GetContactSupportViewModel
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestToDeleteDataActivity : BaseActivity() {

    private val binding by lazy {
        ActivityRequestToDeleteDataBinding.inflate(layoutInflater)
    }
    private val getContactSupportViewModel: GetContactSupportViewModel by viewModels()
    private lateinit var reasonAdapter: ArrayAdapter<String>
    private var loadingDialog: LoadingDialog? = null

    val listOfReason = arrayListOf(
        "I found someone",
        "Privacy Issue",
        "Deleting My Footprints",
        "Other"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        initData()
        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.btnSubmit.setOnClickListener {
            if (binding.actvProblemType.text.toString() == getString(R.string.text_requesttodeletedata_selectreason)) {
                binding.root.showLongDurationSnackBar(getString(R.string.error_text_requesttodeletedata_selectreason))
            } else {
                getContactSupportViewModel.createNewTicket(
                    ContactSupportRequestData(
                        problemType = "Delete Account & Complete Data",
                        description = binding.etProblemDesc.text.toString(),
                        reason = binding.actvProblemType.text.toString(),
                        attachment = "",
                    )
                )
            }


//            if (binding.actvProblemType.text.toString() == getString(R.string.text_requesttodeletedata_selectreason)) {
//                binding.root.showLongDurationSnackBar(getString(R.string.error_text_requesttodeletedata_selectreason))
//            } else {
//                showLoggedOutDialog(
//                    getString(R.string.dialog_requesttodeletedata_title),
//                    getString(R.string.dialog_requesttodeletedata_desc)
//                )
//            }
        }

    }

    private fun initData() {
        binding.layoutToolbar.toolbarTitle.text = getString(R.string.text_profile_deletedata)
        reasonAdapter =
            ArrayAdapter(this, R.layout.dropdown_layout_spinner, listOfReason)
        binding.actvProblemType.setAdapter(reasonAdapter)
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
                    showLoggedOutDialog(
                        getString(R.string.dialog_requesttodeletedata_title),
                        getString(R.string.dialog_requesttodeletedata_desc)
                    ) {
                        finish()
                    }
                }
            }
        }

    }
}