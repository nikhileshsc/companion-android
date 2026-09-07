package com.companion.astrodating.ui.interests.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.RowItemTopMatchesBinding
import com.companion.astrodating.ui.interests.domain.model.GetInterestUserDomainEntity
import com.companion.astrodating.util.formatNumber
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showVisibility

class InterestUsersAdapter() : RecyclerView.Adapter<InterestUsersAdapter.InterestUserViewHolder>() {

    private var interestUserList: MutableList<GetInterestUserDomainEntity> = mutableListOf()
    var onItemClick: ((GetInterestUserDomainEntity) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestUserViewHolder {
        val binding =
            RowItemTopMatchesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InterestUserViewHolder(binding) { onItemClick?.invoke(it) }
    }

    override fun onBindViewHolder(holder: InterestUserViewHolder, position: Int) {
        holder.bind(interestUserList[position])
    }

    override fun getItemCount(): Int {
        return interestUserList.size
    }

    fun submitData(list: List<GetInterestUserDomainEntity>) {
        interestUserList.clear()
        interestUserList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class InterestUserViewHolder(
        val binding: RowItemTopMatchesBinding,
        onItemClicked: (GetInterestUserDomainEntity) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cvItem.setOnClickListener {
                onItemClicked(interestUserList[adapterPosition])
            }
        }

        fun bind(userDomainEntity: GetInterestUserDomainEntity) {
            Glide.with(binding.ivUser.context).load(userDomainEntity.profileUrl)
                .error(R.drawable.ic_default_profile).into(binding.ivUser)
            Glide.with(binding.ivHoroscope.context).load(userDomainEntity.zodiacPngUrl)
                .error(R.drawable.ic_default_profile).into(binding.ivHoroscope)
            if (userDomainEntity.isVerifiedAccount) {
                binding.ivStatus.showVisibility()
            } else {
                binding.ivStatus.hideVisibility()
            }
            binding.tvUser.text = userDomainEntity.fullName
            binding.tvLocationInfo.text =
                "${formatNumber(userDomainEntity.height.toDouble())}, ${userDomainEntity.currentCity}, ${userDomainEntity.community}"
            binding.tvProfession.text = userDomainEntity.profession
//            binding.tvQualification.text = userDomainEntity.education
            binding.tvMaritalStatus.text = userDomainEntity.status
            binding.tvHoroscopeTitle.text = userDomainEntity.zodiacSignInEng
            binding.tvHoroscope.text = "(${userDomainEntity.zodiacSignInMarathi})"
        }
    }
}