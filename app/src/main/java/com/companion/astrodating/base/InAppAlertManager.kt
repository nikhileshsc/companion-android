package com.companion.astrodating.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.companion.astrodating.databinding.ViewInAppAlertBinding

/**
 * Shows a small, auto-dismissing card at the top of the screen when a new
 * message or interest arrives while the app is in the foreground - e.g.
 * "You received an interest!" / "You have a new message!".
 */
object InAppAlertManager {

    private const val AUTO_DISMISS_MS = 4500L

    private var currentAlertView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private var dismissRunnable: Runnable? = null

    fun show(
        container: ViewGroup,
        title: String,
        body: String,
        iconRes: Int,
        onClick: (() -> Unit)? = null
    ) {
        dismiss(container)

        val binding = ViewInAppAlertBinding.inflate(LayoutInflater.from(container.context), container, false)
        binding.ivAlertIcon.setImageResource(iconRes)
        binding.tvAlertTitle.text = title
        binding.tvAlertBody.text = body

        binding.root.alpha = 0f
        binding.root.translationY = -40f

        binding.root.setOnClickListener {
            onClick?.invoke()
            dismiss(container)
        }
        binding.tvAlertClose.setOnClickListener {
            dismiss(container)
        }

        container.addView(binding.root)
        currentAlertView = binding.root

        binding.root.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .start()

        dismissRunnable = Runnable { dismiss(container) }
        handler.postDelayed(dismissRunnable!!, AUTO_DISMISS_MS)
    }

    fun dismiss(container: ViewGroup) {
        dismissRunnable?.let { handler.removeCallbacks(it) }
        dismissRunnable = null

        val view = currentAlertView ?: return
        currentAlertView = null

        view.animate()
            .alpha(0f)
            .translationY(-40f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    container.removeView(view)
                }
            })
            .start()
    }
}
