package com.companion.astrodating.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.companion.astrodating.R
import com.companion.astrodating.databinding.ViewInAppAlertBinding

/**
 * Shows a card at the top of the screen when a new message or interest
 * arrives while the app is in the foreground - e.g. "You received an
 * interest!" / "You have a new message!". The card auto-dismisses after
 * [AUTO_DISMISS_MS] if the user doesn't tap it - previously it only
 * dismissed on tap, which meant any other way of moving on (back press,
 * switching bottom-nav tabs, backgrounding the app) left the alert queue
 * permanently stuck and no further pop-ups were ever shown again for the
 * rest of the app session. [onDismissed] now fires exactly once per alert
 * - whether it was tapped or timed out - so the caller can reliably advance
 * to the next queued alert either way.
 */
object InAppAlertManager {

    private const val AUTO_DISMISS_MS = 6000L

    private var currentAlertView: View? = null
    private var onDismissedCallback: (() -> Unit)? = null
    private val autoDismissHandler = Handler(Looper.getMainLooper())
    private var autoDismissRunnable: Runnable? = null

    fun show(
        container: ViewGroup,
        title: String,
        body: String,
        iconRes: Int,
        avatarUrl: String? = null,
        onClick: (() -> Unit)? = null,
        onDismissed: (() -> Unit)? = null
    ) {
        dismiss(container)

        onDismissedCallback = onDismissed

        val binding = ViewInAppAlertBinding.inflate(LayoutInflater.from(container.context), container, false)
        binding.tvAlertTitle.text = title
        binding.tvAlertBody.text = body

        val density = container.context.resources.displayMetrics.density

        if (!avatarUrl.isNullOrBlank()) {
            // Show the sender's real photo, filling the whole 48dp circle -
            // no tint, no colored backing circle behind it.
            binding.iconContainer.background = null
            binding.ivAlertIcon.imageTintList = null
            val fullSize = (48 * density).toInt()
            binding.ivAlertIcon.layoutParams = FrameLayout.LayoutParams(fullSize, fullSize)
            Glide.with(container.context)
                .load(avatarUrl)
                .transform(CircleCrop())
                .placeholder(iconRes)
                .error(iconRes)
                .into(binding.ivAlertIcon)
        } else {
            // No avatar to show (interest alerts, or a message alert whose
            // sender has no photo) - fall back to a small tinted icon
            // centered on the colored circle background.
            binding.iconContainer.setBackgroundResource(R.drawable.bg_circle_primary)
            binding.ivAlertIcon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(container.context, R.color.white)
            )
            val iconSize = (24 * density).toInt()
            binding.ivAlertIcon.layoutParams = FrameLayout.LayoutParams(
                iconSize, iconSize, Gravity.CENTER
            )
            binding.ivAlertIcon.setImageResource(iconRes)
        }

        binding.root.alpha = 0f
        binding.root.translationY = -40f

        binding.root.setOnClickListener {
            onClick?.invoke()
            dismiss(container)
        }

        container.addView(binding.root)
        currentAlertView = binding.root

        binding.root.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .start()

        val runnable = Runnable { dismiss(container) }
        autoDismissRunnable = runnable
        autoDismissHandler.postDelayed(runnable, AUTO_DISMISS_MS)
    }

    fun dismiss(container: ViewGroup) {
        autoDismissRunnable?.let { autoDismissHandler.removeCallbacks(it) }
        autoDismissRunnable = null

        val view = currentAlertView ?: return
        currentAlertView = null

        val callback = onDismissedCallback
        onDismissedCallback = null

        view.animate()
            .alpha(0f)
            .translationY(-40f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    container.removeView(view)
                    callback?.invoke()
                }
            })
            .start()
    }
}
