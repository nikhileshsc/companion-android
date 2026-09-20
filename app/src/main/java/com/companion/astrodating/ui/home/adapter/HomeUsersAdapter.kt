package com.companion.astrodating.ui.home.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.RowItemHomeBinding
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomainEntity
import com.companion.astrodating.util.formatNumber
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.invisible
import com.companion.astrodating.util.loadImage
import com.companion.astrodating.util.showVisibility
import com.google.android.material.shape.AbsoluteCornerSize
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.CornerSize

class HomeUsersAdapter() : RecyclerView.Adapter<HomeUsersAdapter.HomeUsersViewHolder>() {

    private var listOfUsers: MutableList<GetHomeUserDomainEntity> = mutableListOf()
    var onItemClicked: ((GetHomeUserDomainEntity) -> Unit)? = null
    var onShortlistButtonClicked: ((GetHomeUserDomainEntity) -> Unit)? = null
    var onSentInterestClicked: ((GetHomeUserDomainEntity) -> Unit)? = null
    var onChatButtonClicked: ((GetHomeUserDomainEntity) -> Unit)? = null
    var onCompatibilityReportClicked: ((GetHomeUserDomainEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeUsersViewHolder {
        val binding = RowItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeUsersViewHolder(binding, {
            onItemClicked?.invoke(it)
        },{
            onShortlistButtonClicked?.invoke(it)
        }, {
            onSentInterestClicked?.invoke(it)
        }, {
            onChatButtonClicked?.invoke(it)
        }, {
            onCompatibilityReportClicked?.invoke(it)
        })
    }

    override fun onBindViewHolder(holder: HomeUsersViewHolder, position: Int) {
        holder.bind(listOfUsers[position])
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    fun submitData(list: List<GetHomeUserDomainEntity>) {
        listOfUsers.clear()
        listOfUsers = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class HomeUsersViewHolder(
        val binding: RowItemHomeBinding,
        onItemClicked: (GetHomeUserDomainEntity) -> Unit,
        onShortlistButtonClicked: (GetHomeUserDomainEntity) -> Unit,
        onSentInterestClicked: (GetHomeUserDomainEntity) -> Unit,
        onChatButtonClicked: (GetHomeUserDomainEntity) -> Unit,
        onCompatibilityReportClicked: (GetHomeUserDomainEntity) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivShortlist.setOnClickListener {
                onShortlistButtonClicked(listOfUsers[adapterPosition])
            }
            binding.ivSendInterest.setOnClickListener {
                onSentInterestClicked(listOfUsers[adapterPosition])
            }
            binding.ivChat.setOnClickListener {
                onChatButtonClicked(listOfUsers[adapterPosition])
            }
            binding.clItem.setOnClickListener {
                onItemClicked(listOfUsers[adapterPosition])
            }
            binding.btnCompatibilityReport.setOnClickListener {
                onCompatibilityReportClicked(listOfUsers[adapterPosition])
            }
        }

        private fun Float.dpToPx(): Float {
            return this * binding.root.context.resources.displayMetrics.density
        }

        private fun isOlderDevice(): Boolean {
            // Android 11 and below, or Redmi devices
            return Build.VERSION.SDK_INT <= Build.VERSION_CODES.R ||
                    Build.DEVICE.contains("redmi", ignoreCase = true)
        }

        private fun applyCardCorners(card: com.google.android.material.card.MaterialCardView) {
            if (isOlderDevice()) {
                // Older devices: round all corners + add bottom padding
                card.shapeAppearanceModel = card.shapeAppearanceModel.toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 46f)//AbsoluteCornerSize(16F.dpToPx()))
                    .build()
                card.setPadding(0, 0, 0, 44F.dpToPx().toInt())

                // In your bind() function
                binding.llUserDetailsCards.apply {
                    val params = layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = 4F.dpToPx().toInt() // Change from -12dp to your desired value
                    layoutParams = params
                }
            }
//            else {
//                // Newer devices: only top corners, no bottom padding
//                card.shapeAppearanceModel = card.shapeAppearanceModel.toBuilder()
//                    .setTopLeftCorner(CornerFamily.ROUNDED, 16f.dpToPx())
//                    .setTopRightCorner(CornerFamily.ROUNDED, 24f.dpToPx())
//                    .setBottomLeftCorner(CornerFamily.ROUNDED, AbsoluteCornerSize(0F.dpToPx()))
//                    .setBottomRightCorner(CornerFamily.ROUNDED, AbsoluteCornerSize(0F.dpToPx()))
//                    .build()
//                card.setPadding(0, 0, 0, 0)
//            }
        }

        fun bind(userDomainEntity: GetHomeUserDomainEntity) {
            binding.ivProfile.loadImage(userDomainEntity.profileUrl, sizePx = 600)

            if (userDomainEntity.isVerifiedAccount) {
                binding.ivProfileStatus.showVisibility()
            } else {
                binding.ivProfileStatus.hideVisibility()
            }

            "${userDomainEntity.fullName},${userDomainEntity.age}".also {
                binding.tvUserName.text = it
            }
            binding.tvUserId.text = userDomainEntity.userId

            // Marital Status Card
            if (userDomainEntity.status.isNotEmpty()) {
                binding.clUserMaritalStatus.showVisibility()
                applyCardCorners(binding.clUserMaritalStatus)
                binding.tvUserMaritalStatus.text = userDomainEntity.status
            } else {
                binding.clUserMaritalStatus.invisible()
            }

            // Education Card
            if (userDomainEntity.education.isNotEmpty()) {
                binding.clUserEducation.showVisibility()
                applyCardCorners(binding.clUserEducation)
                binding.tvUserEducation.text = userDomainEntity.education
            } else {
                binding.clUserEducation.invisible()
            }

            // Profession Card
            if (userDomainEntity.profession.isNotEmpty()) {
                binding.clUserProfession.showVisibility()
                applyCardCorners(binding.clUserProfession)
                binding.tvUserProfession.text = userDomainEntity.profession
            } else {
                binding.clUserProfession.invisible()
            }

            "${formatNumber(userDomainEntity.height.toDouble())}, ${userDomainEntity.currentCity}, ${userDomainEntity.community}".also {
                binding.tvUserInfo.text = it
            }
        }
    }
}