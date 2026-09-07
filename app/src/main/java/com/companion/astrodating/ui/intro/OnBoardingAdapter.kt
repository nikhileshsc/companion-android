package com.companion.astrodating.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.companion.astrodating.databinding.ItemOnboardingBinding
import com.companion.astrodating.domain.model.Intro

class OnBoardingAdapter(private val list: List<Intro>) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding =
            ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class OnBoardingViewHolder(private val binding: ItemOnboardingBinding) :
        ViewHolder(binding.root) {

        fun bindItem(onboarding: Intro) {
//            binding.tvOnboardingTitle.text = onboarding.name
//            binding.tvOnboardingDesc.text = onboarding.description
            binding.ivIntro.setImageResource(onboarding.image)
        }
    }
}