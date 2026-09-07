package com.companion.astrodating.ui.onlineusers.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.companion.astrodating.R
import com.companion.astrodating.databinding.RowItemOnlineUserBinding
import com.companion.astrodating.ui.onlineusers.domain.model.GetOnlineUsersDomainEntity
import kotlin.math.roundToInt

class OnlineUsersAdapter(
    private val onItemClick: ((GetOnlineUsersDomainEntity) -> Unit)? = null
) : RecyclerView.Adapter<OnlineUsersAdapter.OnlineUsersViewHolder>() {

    private var userList: List<GetOnlineUsersDomainEntity> = emptyList()

    inner class OnlineUsersViewHolder(private val binding: RowItemOnlineUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: GetOnlineUsersDomainEntity) {
            Log.d("ViewHolder", "Binding user: ${user.fullName}, age: ${user.age}, image: ${user.profileUrl}, distance: ${user.distance}")

            val distanceMeters = user.distance.toDoubleOrNull() ?: 0.0


            val distanceDisplay = when {
                distanceMeters < 500 -> "0.5 km"
                distanceMeters < 1000 -> "${distanceMeters.toInt()} m"
                distanceMeters < 10000 -> String.format("%.1f km", distanceMeters / 1000) // e.g., 2.3 km
                distanceMeters < 100000 -> "${(distanceMeters / 1000).roundToInt()} km"  // e.g., 23 km
                distanceMeters < 1000000 -> "${(distanceMeters / 1000).roundToInt() / 10}k km"  // e.g., 120k km
                else -> "${(distanceMeters / 100000).roundToInt()}k km" // 1,000,000+ => 1M km as 1000k km
            }



            val firstName = user.fullName.split(" ").firstOrNull() ?: user.fullName


//            binding.ivProfileImage.setImageResource(R.drawable.ic_default_profile)
            binding.tvName.text = "${firstName}, ${user.age}"
            binding.tvDistance.text = "${distanceDisplay} away"


            val imageUrl = user.profileUrl.takeIf { it.isNotBlank() }

            Glide.with(binding.ivProfileImage.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_profile)
                .error(R.drawable.ic_default_profile)
                .transform(CenterCrop(), RoundedCorners(32))
                .into(binding.ivProfileImage)

//            Glide.with(binding.ivProfileImage.context)
//                .load(R.drawable.ic_default_profile)
//                .into(binding.ivProfileImage)


            binding.onlineIndicator.setBackgroundResource(
                R.drawable.ic_message_online
            )

            binding.root.setOnClickListener {
                onItemClick?.invoke(user)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineUsersViewHolder {
        val binding = RowItemOnlineUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnlineUsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnlineUsersViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun setList(newList: List<GetOnlineUsersDomainEntity>) {
        Log.d("OnlineUsersAdapter", "setList called with ${newList.size} users")
        userList = newList
        notifyDataSetChanged()
    }
}
