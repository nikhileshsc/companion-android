package com.companion.astrodating.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.databinding.RowItemNotificationBinding
import com.companion.astrodating.ui.notification.domain.model.NotificationDomainEntity

class NotificationAdapter() : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private var listOfNotifications: MutableList<NotificationDomainEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = RowItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listOfNotifications[position])
    }

    override fun getItemCount(): Int {
        return listOfNotifications.size
    }

    fun submitData(list: List<NotificationDomainEntity>) {
        listOfNotifications.clear()
        listOfNotifications = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class NotificationViewHolder(
        val binding: RowItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(notification: NotificationDomainEntity) {
            binding.ivItem.setImageDrawable(ContextCompat.getDrawable(binding.ivItem.context,notification.imageDrawable))
            binding.tvTitle.text = notification.title
        }
    }
}