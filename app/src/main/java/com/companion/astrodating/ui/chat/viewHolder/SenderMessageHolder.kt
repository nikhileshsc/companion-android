package com.companion.astrodating.ui.chat.viewHolder

import android.view.View
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.ItemLayoutMsgSenderBinding
import com.companion.astrodating.util.convertLongToTime
import com.xwray.groupie.viewbinding.BindableItem

class SenderMessageHolder(
    private val chatMessage: String,
    private val chatMessageTime: Long,
    private val chatProfileUrl: String
) : BindableItem<ItemLayoutMsgSenderBinding>() {

    override fun initializeViewBinding(view: View): ItemLayoutMsgSenderBinding {
        return ItemLayoutMsgSenderBinding.bind(view)
    }

    override fun bind(binding: ItemLayoutMsgSenderBinding, position: Int) {
            binding.tvMessageFrom.text = chatMessage
            binding.tvMessageFromTimestamp.text = convertLongToTime(chatMessageTime)
            Glide.with(binding.ivUserRight.context).load(chatProfileUrl)
            .error(R.drawable.ic_default_profile).into(binding.ivUserRight)
    }

    override fun getLayout(): Int = R.layout.item_layout_msg_sender
}