package com.companion.astrodating.ui.updateProfile.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityUpdateProfileBinding
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.ui.AboutFragment
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.ui.BasicDetailsFragment
import com.companion.astrodating.ui.updateProfile.ui.mandatoryDetails.MandatoryDetailsFragment
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.REGISTRATION
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.annotation.Nullable

import androidx.viewpager.widget.ViewPager
import com.companion.astrodating.util.showShortDurationSnackBar

@AndroidEntryPoint
class UpdateProfileActivity : BaseActivity() {

    lateinit var binding: ActivityUpdateProfileBinding
    lateinit var aboutFragment: AboutFragment
    lateinit var basicDetailsFragment: BasicDetailsFragment
    lateinit var mandatoryDetailsFragment: MandatoryDetailsFragment
    private var viewPagerAdapter: ViewPagerAdapter?=null
    private var from : String = APP_EMPTY_STRING


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
        aboutFragment = AboutFragment()
        basicDetailsFragment = BasicDetailsFragment()
        mandatoryDetailsFragment = MandatoryDetailsFragment()
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,0)
        setupViews()
    }

    override fun initObservers() {

    }

    private fun setupViews() {
        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.tvTitle.text = getString(R.string.text_updateprofile_title)

        binding.layoutToolbar.ivToolbarBack.setOnClickListener {
            finish()
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        if (intent!=null){
            from = intent.getStringExtra(FROM) ?: APP_EMPTY_STRING
            val bundle = Bundle()
            bundle.putString(FROM, from)
            basicDetailsFragment.arguments = bundle
            if (from == REGISTRATION){
                viewPagerAdapter!!.addFragment(aboutFragment,getString(R.string.text_about_title))
                viewPagerAdapter!!.addFragment(basicDetailsFragment,getString(R.string.text_basicdetails_title))
            }else{
                viewPagerAdapter!!.addFragment(aboutFragment,getString(R.string.text_about_title))
                viewPagerAdapter!!.addFragment(basicDetailsFragment,getString(R.string.text_basicdetails_title))
                viewPagerAdapter!!.addFragment(mandatoryDetailsFragment,getString(R.string.text_mandatorydetails_title))
            }
        }

        binding.viewPager.adapter = viewPagerAdapter

        // UpdateProfileActivity.kt

        var lastPosition = 0
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val leavingAbout = (lastPosition == 0 && position != 0)

                val mustStay = try {
                    aboutFragment.isAdded && aboutFragment.view != null && aboutFragment.requiresStayOnAbout()
                } catch (_: Exception) { false }

                if (leavingAbout && mustStay) {
                    binding.viewPager.currentItem = 0
                    binding.root.showShortDurationSnackBar(getString(R.string.error_contains_contact_info_backend))
                } else {
                    lastPosition = position
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })



    }


    private class ViewPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        private val fragments: MutableList<Fragment> = ArrayList<Fragment>()
        private val fragmentTitles: MutableList<String> = ArrayList()

        //add fragment to the viewpager
        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        //to setup title of the tab layout
        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitles[position]
        }
    }

}