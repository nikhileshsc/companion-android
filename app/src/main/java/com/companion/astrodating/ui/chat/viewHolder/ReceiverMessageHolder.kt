package com.companion.astrodating.ui.chat.viewHolder

import android.view.View
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.ItemLayoutMsgReceiverBinding
import com.companion.astrodating.ui.chat.OnUserPhotoClickListener
import com.companion.astrodating.util.convertLongToTime
import com.companion.astrodating.util.loadImage
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
        binding.ivUser.loadImage(chatProfileUrl, sizePx = 150)

        binding.ivUser.setOnClickListener{
            onPhotoClick?.invoke(chatProfileUrl)
        }
    }

    override fun getLayout(): Int = R.layout.item_layout_msg_receiver
}