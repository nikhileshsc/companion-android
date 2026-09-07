package com.companion.astrodating.ui.chat.viewHolder

import android.view.View
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.ItemLayoutMsgReceiverBinding
import com.companion.astrodating.ui.chat.OnUserPhotoClickListener
import com.companion.astrodating.util.convertLongToTime
import com.xwray.groupie.viewbinding.BindableItem

class ReceiverMessageHolder (
    private val chatMessage: String,
    private val chatMessageTime: Long,
    private val chatProfileUrl: String?,
    private val onPhotoClick: ((String?) -> Unit)? = null
) : BindableItem<ItemLayoutMsgReceiverBinding>() {

    override fun initializeViewBinding(view: View): ItemLayoutMsgReceiverBinding {
        return ItemLayoutMsgReceiverBinding.bind(view)
    }

    override fun bind(binding: ItemLayoutMsgReceiverBinding, position: Int) {
        binding.tvMessageReceiver.text = chatMessage
        binding.tvMessageFromTimestamp.text = convertLongToTime(chatMessageTime)
        Glide.with(binding.ivUser.context).load(chatProfileUrl)
            .error(R.drawable.ic_default_profile).into(binding.ivUser)

        binding.ivUser.setOnClickListener{
            onPhotoClick?.invoke(chatProfileUrl)
        }
    }

    override fun getLayout(): Int = R.layout.item_layout_msg_receiver
}