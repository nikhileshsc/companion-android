package com.companion.astrodating.ui.managePhotos.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.RowItemManagePhotoBinding
import com.companion.astrodating.ui.managePhotos.domain.model.GetMyPhotoGalleryDomainEntity
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.loadImage
import com.companion.astrodating.util.showVisibility

class ManagePhotosAdapter : RecyclerView.Adapter<ManagePhotosAdapter.ManagePhotosViewHolder>() {

    private var listOfGallery: MutableList<GetMyPhotoGalleryDomainEntity> = mutableListOf()
    var onItemClicked: ((GetMyPhotoGalleryDomainEntity) -> Unit)? = null
    var onDeleteItemClicked: ((GetMyPhotoGalleryDomainEntity) -> Unit)? = null
    var onAddPhotoItemClicked: ((GetMyPhotoGalleryDomainEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagePhotosViewHolder {
        val binding =
            RowItemManagePhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManagePhotosViewHolder(binding, {
            onItemClicked?.invoke(it)
        }, {
            onDeleteItemClicked?.invoke(it)
        }, {
            onAddPhotoItemClicked?.invoke(it)
        })
    }

    override fun onBindViewHolder(holder: ManagePhotosViewHolder, position: Int) {
        holder.bind(listOfGallery[position],position)
    }

    override fun getItemCount(): Int {
        return listOfGallery.size
    }

    fun submitData(list: List<GetMyPhotoGalleryDomainEntity>) {
        listOfGallery.clear()
        listOfGallery = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class ManagePhotosViewHolder(
        val binding: RowItemManagePhotoBinding,
        onItemClicked: (GetMyPhotoGalleryDomainEntity) -> Unit,
        onDeleteItemClicked: (GetMyPhotoGalleryDomainEntity) -> Unit,
        onAddPhotoItemClicked: (GetMyPhotoGalleryDomainEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.clProfileItem.setOnClickListener {
                onItemClicked(listOfGallery[adapterPosition])
            }
            binding.ivDeleteImage.setOnClickListener {
                onDeleteItemClicked(listOfGallery[adapterPosition])
            }
            binding.clAddPhoto.setOnClickListener {
                onAddPhotoItemClicked(listOfGallery[adapterPosition])
            }
        }

        fun bind(galleryDomainEntity: GetMyPhotoGalleryDomainEntity, position: Int) {
            if (listOfGallery[position].status == "Add Photo"){
                binding.clAddPhoto.showVisibility()
                binding.clProfileItem.hideVisibility()
            }else {
                binding.clProfileItem.showVisibility()
                binding.clAddPhoto.hideVisibility()
                binding.ivProfile.loadImage(galleryDomainEntity.galleryUrl, sizePx = 400)
                binding.tvProfileStatus.text = galleryDomainEntity.status
                if (galleryDomainEntity.status == binding.tvProfileStatus.context.getString(R.string.text_gallery_status_approved)) {
                    binding.tvProfileStatus.background = ContextCompat.getDrawable(
                        binding.tvProfileStatus.context,
                        R.drawable.bg_profile_status_approved
                    )
                } else if (galleryDomainEntity.status == binding.tvProfileStatus.context.getString(R.string.text_gallery_status_profilephoto)) {
                    binding.tvProfileStatus.background = ContextCompat.getDrawable(
                        binding.tvProfileStatus.context,
                        R.drawable.bg_profile_status_profilephoto
                    )
                } else {
                    binding.tvProfileStatus.background = ContextCompat.getDrawable(
                        binding.tvProfileStatus.context,
                        R.drawable.bg_profile_status_inreview
                    )
                }

            }
        }
    }
}