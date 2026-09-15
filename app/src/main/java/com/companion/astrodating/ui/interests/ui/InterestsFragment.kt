package com.companion.astrodating.ui.interests.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.base.InAppEventBus
import com.companion.astrodating.databinding.FragmentInterestsBinding
import com.companion.astrodating.ui.home.domain.model.InterestsList
import com.companion.astrodating.ui.interests.adapter.InterestsAdapter
import com.companion.astrodating.util.InterestTypeConstant
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterestsFragment : Fragment() {

    lateinit var binding: FragmentInterestsBinding
    private val interestsAdapter by lazy {
        InterestsAdapter(InterestsList.interestsList)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentInterestsBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewsAndAdapters()
    }

    override fun onResume() {
        super.onResume()
        // Opening the Interests tab counts as reading the received interests -
        // clear the bottom-nav badge.
        InAppEventBus.resetInterestBadge()
    }

    private fun initRecyclerViewsAndAdapters() {
        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.ivTitleLogo.showVisibility()
        binding.layoutToolbar.ivTitleLogo.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_dashboard_interests_selected))
        binding.layoutToolbar.tvTitle.text = ContextCompat.getString(requireContext(),R.string.text_dashboard_interests)
        binding.layoutToolbar.ivToolbarBack.setOnClickListener {
            findNavController().popBackStack()

        }

        binding.rvInterest.apply {
            adapter = interestsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        interestsAdapter.onItemClicked ={
            val bundle = Bundle()
            bundle.putString(InterestTypeConstant.interestData,Gson().toJson(it))
            findNavController().navigate(R.id.action_interestsFragment_to_interestUserFragment,bundle)
        }

    }

}
