package com.companion.astrodating.ui.intro

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.companion.astrodating.databinding.ActivityOnboardingBinding
import com.companion.astrodating.domain.model.Intro
import com.companion.astrodating.domain.model.IntroList
import com.companion.astrodating.ui.otp.ui.request.RequestOtpActivity
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.setFullscreenWithNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnBoardingAdapter
    private var currentPageCount = 0
    private var arrayList = arrayListOf<Intro>()

    var MIN_POSTION = 0
    var MAX_POSTION = 0

    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            MIN_POSTION = position
            currentPageCount = position
            if (arrayList.isNotEmpty()) {
//                dotAdapter.changeDotPosition(arrayList[position].position)
            }
            countDownTimer.cancel()
            countDownTimer.start()
            if (position == arrayList.size - 2)
                binding.viewPagerOnboarding.post(runnable)
//            if (position == IntroList.introList.size - 1) {
////                binding.btnGetStarted.text = getString(R.string.text_get_started)
////                binding.ivAction.setImageResource(R.drawable.ic_done_white)
//            } else {
////                binding.btnGetStarted.text = getString(R.string.text_next_title)
////                binding.ivAction.setImageResource(R.drawable.ic_arrow_forward_white)
//            }
        }
    }

    val countDownTimer = object : CountDownTimer(3000, 1000) {
        override fun onTick(p0: Long) {}

        override fun onFinish() {
            if (MIN_POSTION < MAX_POSTION) {
                MIN_POSTION += 1
                //Log.e("onFinish: ", "" + MIN_POSTION)
                binding.viewPagerOnboarding.currentItem = MIN_POSTION
            } else {
                MIN_POSTION = 0
                binding.viewPagerOnboarding.setCurrentItem(MIN_POSTION, true)
            }
        }
    }

    var count = 0
    val runnable = Runnable {
        if (count < 5) {
//            Log.e("Runable: ", "$count")
            count++
            arrayList.addAll(arrayList)
            MAX_POSTION = arrayList.size - 1
            onboardingAdapter.notifyDataSetChanged()
        } else {
            count = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullscreenWithNavigation(this, window)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        handleClickEvents()
        Log.e(
            TAG,
            "CurrentPageCount = $currentPageCount \n ItemCount = ${onboardingAdapter.itemCount - 1}"
        )
    }

    private fun setupViews() {
        arrayList.addAll(IntroList.introList)
        MAX_POSTION = arrayList.size - 1
        onboardingAdapter = OnBoardingAdapter(arrayList)
        binding.viewPagerOnboarding.adapter = onboardingAdapter
        binding.viewPagerOnboarding.clipChildren = false
        binding.viewPagerOnboarding.clipToPadding = false
        binding.viewPagerOnboarding.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

//        binding.dotsIndicator.attachTo(binding.viewPagerOnboarding)
        binding.viewPagerOnboarding.registerOnPageChangeCallback(pageCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPagerOnboarding.unregisterOnPageChangeCallback(pageCallback)
    }

    private fun handleClickEvents() {

        binding.btnGetStarted.setOnClickListener {

            navigateToNextScreen()

        }
    }

    private fun skipOnboardingScreen() {
        StorePreferences.saveOnboardingStatus(true)
        navigateToNextScreen()
    }

    private fun navigateToNextScreen() {
        StorePreferences.saveOnboardingStatus(true)
        launchScreenAndFinish<RequestOtpActivity>()
    }
}