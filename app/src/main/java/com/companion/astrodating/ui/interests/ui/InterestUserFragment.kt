package com.companion.astrodating.ui.interests.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.databinding.FragmentInterestUserBinding
import com.companion.astrodating.ui.home.domain.model.InterestDomain
import com.companion.astrodating.ui.interests.adapter.InterestUsersAdapter
import com.companion.astrodating.ui.interests.domain.model.GetInterestUserDomainEntity
import com.companion.astrodating.ui.interests.viewmodel.InterestsViewModel
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.profileDetails.ui.ProfileDetailsActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.InterestTypeConstant
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.USER_ID
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterestUserFragment : Fragment() {

    lateinit var binding: FragmentInterestUserBinding
    private lateinit var loadingDialog: LoadingDialog
    private val interestViewModel: InterestsViewModel by viewModels()
    private var interestUserList: ArrayList<GetInterestUserDomainEntity>? = null
    private val interestUsersAdapter by lazy {
        InterestUsersAdapter()
    }
    var layoutManager: LinearLayoutManager? = null
    private var interestData : InterestDomain?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInterestUserBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getIntentData()
        initData()
        initObserver()
        handleClickEvents()
    }

    private fun getIntentData() {
        if (requireArguments()!=null){
            interestData = Gson().fromJson(requireArguments().getString(InterestTypeConstant.interestData),InterestDomain::class.java)
            if (interestData!=null){
                if (requireActivity().isInternetConnection()) {
                    interestViewModel.getInterestByType(interestData!!.interestType)
                } else {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                }
            }
        }

    }

    private fun handleClickEvents() {
        interestUsersAdapter.onItemClick = {
            requireActivity().launchScreen<ProfileDetailsActivity> {
                putExtra(USER_ID, it.id)
            }
        }
        binding.layoutToolbar.ivToolbarBack.setOnClickListener{
            findNavController().popBackStack()
        }

    }

    private fun initObserver() {
        interestViewModel.getInterestState.observe(requireActivity()) {
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
                    interestUserList!!.clear()
                    interestUserList!!.addAll(it.data.users)
                    if (interestUserList!!.isEmpty()) {
                        binding.rvInterestUsers.hideVisibility()
                        binding.tvNoData.showVisibility()
                    } else {
                        binding.rvInterestUsers.showVisibility()
                        binding.tvNoData.hideVisibility()
                        interestUsersAdapter.submitData(interestUserList!!)
                    }
                }
            }
        }

    }

    private fun initData() {
        interestUserList = ArrayList()
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.rvInterestUsers.layoutManager = layoutManager
        binding.rvInterestUsers.adapter = interestUsersAdapter

        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.tvTitle.text ="${requireContext().getString(interestData!!.title)} User"

    }




}