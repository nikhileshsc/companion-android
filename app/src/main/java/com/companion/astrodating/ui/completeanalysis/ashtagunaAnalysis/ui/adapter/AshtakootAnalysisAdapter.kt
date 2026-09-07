package com.companion.astrodating.ui.completeanalysis.ashtagunaAnalysis.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.databinding.RowItemAshtakootBinding
import com.companion.astrodating.ui.completeanalysis.ashtagunaAnalysis.ui.model.AshtakootData
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showVisibility

class AshtakootAnalysisAdapter :
    RecyclerView.Adapter<AshtakootAnalysisAdapter.AshtakootAnalysisViewHolder>() {

    private var ashtakootDataList: MutableList<AshtakootData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AshtakootAnalysisViewHolder {
        val binding =
            RowItemAshtakootBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AshtakootAnalysisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AshtakootAnalysisViewHolder, position: Int) {
        holder.bind(ashtakootDataList[position],position)
    }

    override fun getItemCount(): Int {
        return ashtakootDataList.size
    }

    fun submitData(list: List<AshtakootData>) {
        ashtakootDataList.clear()
        ashtakootDataList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class AshtakootAnalysisViewHolder(
        val binding: RowItemAshtakootBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AshtakootData, position: Int) {
            if (position%2 == 0){
                binding.clLayout1.hideVisibility()
                binding.clLayout2.showVisibility()

                binding.tvAttributeTitle2.text = data.title
                binding.tvDescriptionTitle2.text = data.area_of_life
                binding.tvUser1Title2.text = data.p1
                binding.tvUser2Title2.text = data.p2
                binding.tvMarksObtained2.text = data.points_obtained.toString()
                binding.tvTotalTitle2.text = data.max_ponits.toString()

            }else{

                binding.clLayout2.hideVisibility()
                binding.clLayout1.showVisibility()

                binding.tvAttributeTitle1.text = data.title
                binding.tvDescriptionTitle1.text = data.area_of_life
                binding.tvUser1Title1.text = data.p1
                binding.tvUser2Title1.text = data.p2
                binding.tvMarksObtained1.text = data.points_obtained.toString()
                binding.tvTotalTitle1.text = data.max_ponits.toString()
            }
        }
    }
}