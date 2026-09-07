package com.companion.astrodating.ui.completeanalysis.ashtagunaAnalysis.ui

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.databinding.ActivityAshtakootAnalysisBinding
import com.companion.astrodating.ui.completeanalysis.ashtagunaAnalysis.ui.adapter.AshtakootAnalysisAdapter
import com.companion.astrodating.ui.completeanalysis.ashtagunaAnalysis.ui.model.AshtakootData
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ASHTAGUNA_MILAN_DATA
import com.companion.astrodating.util.USER_1
import com.companion.astrodating.util.USER_2
import com.companion.astrodating.util.formatNumber
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showDialog
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AshtakootAnalysisActivity : BaseActivity(), View.OnClickListener {

    private val binding by lazy {
        ActivityAshtakootAnalysisBinding.inflate(layoutInflater)
    }
    private val ashtakootAnalysisAdapter by lazy {
        AshtakootAnalysisAdapter()
    }
    private var user1: String = APP_EMPTY_STRING
    private var user2: String = APP_EMPTY_STRING
    var layoutManager : LinearLayoutManager? = null
    private var ashtakootDataList: ArrayList<AshtakootData>? = null
    private var ashtakootMilanDomainData: AshtakootMilanDomainDetails.AshtakootMilanDomain? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewData()
        getIntentData()
        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivToolbarBack.setOnClickListener(this)
        binding.ivAshtakootInfo.setOnClickListener(this)

    }

    private fun initViewData() {
        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.tvTitle.text = getString(R.string.text_ashtakootanalysis_title)

    }

    private fun getIntentData() {
        ashtakootDataList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvTable.layoutManager = layoutManager
        binding.rvTable.adapter = ashtakootAnalysisAdapter
        if (intent != null) {
            user1 = intent.getStringExtra(USER_1) ?: APP_EMPTY_STRING
            user2 = intent.getStringExtra(USER_2) ?: APP_EMPTY_STRING
            ashtakootMilanDomainData = Gson().fromJson(
                intent.getStringExtra(ASHTAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                AshtakootMilanDomainDetails.AshtakootMilanDomain::class.java
            )
            if (ashtakootMilanDomainData != null) {
                if (user1.isNotEmpty()){
                    binding.tvUser1Title.text = user1
                }
                if (user2.isNotEmpty()){
                    binding.tvUser2Title.text = user2
                }
                if (ashtakootMilanDomainData!!.ashtakoot_milan_result!=null){
                    val totalPoints = "${formatNumber(ashtakootMilanDomainData!!.ashtakoot_milan_result.points_obtained)}/${ashtakootMilanDomainData!!.ashtakoot_milan_result.max_ponits}"
                    binding.tvAshtakootCount.text = totalPoints
                    binding.tvAshtakootDesc.text = ashtakootMilanDomainData!!.ashtakoot_milan_result.content
                    binding.tvMarksObtained.text = ashtakootMilanDomainData!!.ashtakoot_milan_result.points_obtained.toString()
                    binding.tvTotalCount.text = ashtakootMilanDomainData!!.ashtakoot_milan_result.max_ponits.toString()
                    val averageAshtagunaPoint = ashtakootMilanDomainData!!.ashtakoot_milan_result.max_ponits / 2.0
                    if (ashtakootMilanDomainData!!.ashtakoot_milan_result.points_obtained >= averageAshtagunaPoint){
                        binding.ivAshtakoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivAshtakoot.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }
                }
                if (ashtakootMilanDomainData!!.ashtakoot_milan != null) {
                    val varna = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_varna),
                        ashtakootMilanDomainData!!.ashtakoot_milan.varna.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.varna.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.varna.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.varna.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.varna.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.varna.points_obtained
                    )
                    val vashya = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_vashya),
                        ashtakootMilanDomainData!!.ashtakoot_milan.vashya.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.vashya.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.vashya.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.vashya.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.vashya.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.vashya.points_obtained
                    )
                    val tara = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_tara),
                        ashtakootMilanDomainData!!.ashtakoot_milan.tara.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.tara.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.tara.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.tara.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.tara.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.tara.points_obtained
                    )
                    val yoni = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_yoni),
                        ashtakootMilanDomainData!!.ashtakoot_milan.yoni.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.yoni.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.yoni.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.yoni.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.yoni.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.yoni.points_obtained
                    )
                    val grahaMaitri = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_grahamaitri),
                        ashtakootMilanDomainData!!.ashtakoot_milan.graha_maitri.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.graha_maitri.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.graha_maitri.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.graha_maitri.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.graha_maitri.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.graha_maitri.points_obtained
                    )
                    val gana = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_gana),
                        ashtakootMilanDomainData!!.ashtakoot_milan.gana.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.gana.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.gana.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.gana.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.gana.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.gana.points_obtained
                    )

                    val bhakoota = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_bhakoota),
                        ashtakootMilanDomainData!!.ashtakoot_milan.bhakoota.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.bhakoota.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.bhakoota.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.bhakoota.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.bhakoota.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.bhakoota.points_obtained
                    )

                    val nadi = AshtakootData(
                        getString(R.string.text_ashtakoot_attr_nadi),
                        ashtakootMilanDomainData!!.ashtakoot_milan.nadi.area_of_life,
                        ashtakootMilanDomainData!!.ashtakoot_milan.nadi.description,
                        ashtakootMilanDomainData!!.ashtakoot_milan.nadi.max_ponits,
                        ashtakootMilanDomainData!!.ashtakoot_milan.nadi.p1,
                        ashtakootMilanDomainData!!.ashtakoot_milan.nadi.p2,
                        ashtakootMilanDomainData!!.ashtakoot_milan.nadi.points_obtained
                    )
                    ashtakootDataList!!.add(varna)
                    ashtakootDataList!!.add(vashya)
                    ashtakootDataList!!.add(tara)
                    ashtakootDataList!!.add(yoni)
                    ashtakootDataList!!.add(grahaMaitri)
                    ashtakootDataList!!.add(gana)
                    ashtakootDataList!!.add(bhakoota)
                    ashtakootDataList!!.add(nadi)
                    ashtakootAnalysisAdapter.submitData(ashtakootDataList!!)

                    binding.tvVarnaTitle.text = varna.area_of_life
                    binding.tvVarnaDesc.text = varna.description
                    binding.tvVarnaCount.text = "${varna.title} - ${formatNumber(varna.points_obtained)}/${varna.max_ponits}"
                    val averageVarnaPoint = varna.max_ponits / 2.0
                    if (varna.points_obtained >= averageVarnaPoint){
                        binding.ivVarna.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivVarna.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                    binding.tvVashyaTitle.text = vashya.area_of_life
                    binding.tvVashyaDesc.text = vashya.description
                    binding.tvVashyaCount.text = "${vashya.title} - ${formatNumber(vashya.points_obtained)}/${vashya.max_ponits}"
                    val averageVashyaPoint = vashya.max_ponits / 2
                    if (vashya.points_obtained >= averageVashyaPoint){
                        binding.ivVashya.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivVashya.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                    binding.tvTaraTitle.text = tara.area_of_life
                    binding.tvTaraDesc.text = tara.description
                    binding.tvTaraCount.text = "${tara.title} - ${formatNumber(tara.points_obtained)}/${tara.max_ponits}"
                    val averageTaraPoint = tara.max_ponits / 2
                    if (tara.points_obtained >= averageTaraPoint){
                        binding.ivTara.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivTara.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                    binding.tvYoniTitle.text = yoni.area_of_life
                    binding.tvYoniDesc.text = yoni.description
                    binding.tvYoniCount.text = "${yoni.title} - ${formatNumber(yoni.points_obtained)}/${yoni.max_ponits}"
                    val averageYoniPoint = yoni.max_ponits / 2
                    if (yoni.points_obtained >= averageYoniPoint){
                        binding.ivYoni.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivYoni.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                    binding.tvGrahaMaitriTitle.text = grahaMaitri.area_of_life
                    binding.tvGrahaMaitriDesc.text = grahaMaitri.description
                    binding.tvGrahaMaitriCount.text = "${grahaMaitri.title} - ${formatNumber(grahaMaitri.points_obtained)}/${grahaMaitri.max_ponits}"
                    val averageGrahaMaitriPoint = grahaMaitri.max_ponits / 2
                    if (grahaMaitri.points_obtained >= averageGrahaMaitriPoint){
                        binding.ivGrahaMaitri.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivGrahaMaitri.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                    binding.tvGanaTitle.text = gana.area_of_life
                    binding.tvGanaDesc.text = gana.description
                    binding.tvGanaCount.text = "${gana.title} - ${formatNumber(gana.points_obtained)}/${gana.max_ponits}"
                    val averageGanaPoint = gana.max_ponits / 2
                    if (gana.points_obtained >= averageGanaPoint){
                        binding.ivGana.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivGana.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                    binding.tvBhakootaTitle.text = bhakoota.area_of_life
                    binding.tvBhakootaDesc.text = bhakoota.description
                    binding.tvBhakootaCount.text = "${bhakoota.title} - ${formatNumber(bhakoota.points_obtained)}/${bhakoota.max_ponits}"
                    val averageBhakootaPoint = bhakoota.max_ponits / 2
                    if (bhakoota.points_obtained >= averageBhakootaPoint){
                        binding.ivBhakoota.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivBhakoota.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                    binding.tvNadiTitle.text = nadi.area_of_life
                    binding.tvNadiDesc.text = nadi.description
                    binding.tvNadiCount.text = "${nadi.title} - ${formatNumber(nadi.points_obtained)}/${nadi.max_ponits}"
                    val averageNadiPoint = nadi.max_ponits / 2
                    if (nadi.points_obtained >= averageNadiPoint){
                        binding.ivNadi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_green_thumb))
                    }else{
                        binding.ivNadi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_complete_analysis_red_thumb))
                    }

                }
            }
        }

    }

    override fun initObservers() {

    }

    override fun onClick(p0: View?) {
        when(p0){
            binding.layoutToolbar.ivToolbarBack ->{
                finish()
            }
            binding.ivAshtakootInfo -> {
                showDialog(getString(R.string.text_matchmakingreport_ashtakoot_title),getString(R.string.text_matchmakingreport_ashtakoot_desc))
            }
        }
    }
}