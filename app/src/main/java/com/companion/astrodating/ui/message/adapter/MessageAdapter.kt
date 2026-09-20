package com.companion.astrodating.ui.message.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.RowItemMessageBinding
import com.companion.astrodating.ui.message.domain.model.MessageInfo
import com.companion.astrodating.util.convertMessageTimeFromLongToTime
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.loadImage
import com.companion.astrodating.util.showVisibility
import java.util.Locale


class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(), Filterable {

    private var messageInfoList: MutableList<MessageInfo> = mutableListOf()
    private var messageInfoListFull: MutableList<MessageInfo> = mutableListOf()
    var onItemClick: ((MessageInfo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = RowItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding) { onItemClick?.invoke(it) }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageInfoList[position])
    }

    override fun getItemCount(): Int {
        return messageInfoList.size
    }

    fun submitData(list: ArrayList<MessageInfo>) {
        // Remove duplicates by userId before updating
        val uniqueList = list.distinctBy { it.userId }.toMutableList()

        messageInfoList.clear()
        messageInfoListFull.clear()
        messageInfoList = uniqueList
        messageInfoListFull.addAll(messageInfoList)
        notifyDataSetChanged()

        Log.d("MessageAdapter", "✅ Submitted ${uniqueList.size} unique conversations (filtered from ${list.size})")
    }

    inner class MessageViewHolder(
        val binding: RowItemMessageBinding,
        onItemClicked: (MessageInfo) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.clItem.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked(messageInfoList[adapterPosition])
                }
            }
        }

        fun bind(messageInfo: MessageInfo) {
            binding.ivUser.loadImage(messageInfo.profileUrl, sizePx = 200)

            binding.tvUser.text = messageInfo.userName
            binding.tvUserMessage.text = messageInfo.message
            binding.tvMessageTime.text = convertMessageTimeFromLongToTime(messageInfo.msgTime)

            if (messageInfo.unreadMsgCount > 0.toString()) {
                binding.tvMessageUnreadCount.showVisibility()
                binding.tvMessageUnreadCount.text = messageInfo.unreadMsgCount
            } else {
                binding.tvMessageUnreadCount.hideVisibility()
            }
        }
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<MessageInfo> = ArrayList()

            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(messageInfoListFull)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                for (item in messageInfoListFull) {
                    if (item.userName.lowercase().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            messageInfoList.clear()
            @Suppress("UNCHECKED_CAST")
            messageInfoList.addAll(results.values as List<MessageInfo>)
            notifyDataSetChanged()
        }
    }
}