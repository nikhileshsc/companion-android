package com.companion.astrodating.ui.topMatches.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.RowItemTopMatchesBinding
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomainEntity
import com.companion.astrodating.util.formatNumber
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showVisibility

class TopMatchesAdapter() : RecyclerView.Adapter<TopMatchesAdapter.TopMatchesViewHolder>() {

    private var topUserList: MutableList<GetHomeUserDomainEntity> = mutableListOf()
    var onItemClick: ((GetHomeUserDomainEntity) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopMatchesViewHolder {
        val binding =
            RowItemTopMatchesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopMatchesViewHolder(binding) { onItemClick?.invoke(it) }
    }

    override fun onBindViewHolder(holder: TopMatchesViewHolder, position: Int) {
        holder.bind(topUserList[position])
    }

    override fun getItemCount(): Int {
        return topUserList.size
    }

    fun submitData(list: List<GetHomeUserDomainEntity>) {
        topUserList.clear()
        topUserList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class TopMatchesViewHolder(
        val binding: RowItemTopMatchesBinding,
        onItemClicked: (GetHomeUserDomainEntity) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cvItem.setOnClickListener {
                onItemClicked(topUserList[adapterPosition])
            }
        }

        fun bind(userDomainEntity: GetHomeUserDomainEntity) {
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
            if(userDomainEntity.isOnline){
                binding.viewOnlineDot.visibility = View.VISIBLE
                binding.tvOnline.visibility = View.VISIBLE
            }else{
                binding.viewOnlineDot.visibility = View.GONE
                binding.tvOnline.visibility = View.GONE
            }
        }
    }
}