package com.companion.astrodating.ui.topMatches.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.R
import com.companion.astrodating.databinding.FragmentTopMatchesBinding
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomainEntity
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.profileDetails.ui.ProfileDetailsActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.topMatches.adapter.TopMatchesAdapter
import com.companion.astrodating.ui.topMatches.viewModel.TopUserViewModel
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopMatchesFragment : Fragment() {

    lateinit var binding: FragmentTopMatchesBinding
    private val topUserViewModel : TopUserViewModel by viewModels()
    private val topMatchesAdapter by lazy {
        TopMatchesAdapter()
    }
    private lateinit var loadingDialog: LoadingDialog
    private var topUserList: ArrayList<GetHomeUserDomainEntity>? = null
    var firstVisibleItem = 0
    var visibleItemCount:Int = 0
    var totalItemCount:Int = 0
    private var loading = true
    private var previousTotal = 0
    private var visibleThreshold = 5
    var page = 1
    var layoutManager : LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTopMatchesBinding.inflate(inflater, container, false)
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
        topMatchesAdapter.onItemClick = {
            requireActivity().launchScreen<ProfileDetailsActivity> {
                putExtra(USER_ID, it.id)
//                putExtra(SEC_USER_DATA, Gson().toJson(it))
            }
        }

    }

    private fun initObserver() {
        topUserViewModel.topUserList.observe(requireActivity()) {
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
                    topUserList!!.clear()
                    topUserList!!.addAll(it.data.users)
                    if (topUserList!!.isEmpty()) {
                        binding.rvTopMatches.hideVisibility()
                        binding.tvNoData.showVisibility()
                    } else {
                        binding.rvTopMatches.showVisibility()
                        binding.tvNoData.hideVisibility()
                        topMatchesAdapter.submitData(topUserList!!)
                    }
                }
            }
        }

        topUserViewModel.topMoreUserList.observe(requireActivity()) {
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
                    topUserList!!.addAll(it.data.users)
                    if (topUserList!!.isEmpty()) {
//                        binding.rvNotifications.hideVisibility()
//                        binding.tvNoData.showVisibility()
                    } else {
                        binding.rvTopMatches.showVisibility()
                        binding.tvNoData.hideVisibility()
                        topMatchesAdapter.submitData(topUserList!!)
                    }
                }
            }
        }

    }

    private fun initData() {
        topUserList = ArrayList()
        layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvTopMatches.layoutManager = layoutManager
        binding.rvTopMatches.adapter = topMatchesAdapter
        if (requireActivity().isInternetConnection()) {
            resetValue()
            topUserViewModel.getTopUserDetails(page)
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }

        binding.rvTopMatches.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = binding.rvTopMatches.childCount
                totalItemCount = layoutManager!!.itemCount
                firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    // End has been reached
                    requireActivity().runOnUiThread(Runnable {
                        if (requireActivity().isInternetConnection()) {
                            topUserViewModel.getMoreTopUserDetails(pageNumber = ++page)
                        }
                    })
                    // Do something
                    loading = true
                }
            }
        })
    }
    /** reset all values  */
    fun resetValue() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        loading = true
        previousTotal = 0
        visibleThreshold = 5
        page = 1
        topUserList!!.clear()
    }


}