package com.companion.astrodating.ui.profileDetails.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.LayoutImageItemBinding
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsGalleryDomain

class ProfileImageAdapter() : RecyclerView.Adapter<ProfileImageAdapter.ProfileImageViewHolder>() {

    private var galleryList: MutableList<GetUserDetailsGalleryDomain> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileImageViewHolder {
        val binding =
            LayoutImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileImageViewHolder, position: Int) {
        holder.bind(galleryList[position])
    }

    override fun getItemCount(): Int {
        return galleryList.size
    }

    fun submitData(list: List<GetUserDetailsGalleryDomain>) {
        galleryList.clear()
        galleryList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class ProfileImageViewHolder(
        val binding: LayoutImageItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(galleryDomain: GetUserDetailsGalleryDomain) {
            Glide.with(binding.ivProfile.context).load(galleryDomain.galleryUrl)
                .error(R.drawable.ic_default_profile).into(binding.ivProfile)

        }
    }
}