package com.companion.astrodating.ui.completeanalysis.dashgunaAnalysis.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.databinding.RowItemDashakootBinding
import com.companion.astrodating.ui.completeanalysis.dashgunaAnalysis.ui.model.DashakootData
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showVisibility

class DashakootAnalysisAdapter :
    RecyclerView.Adapter<DashakootAnalysisAdapter.DashakootAnalysisViewHolder>() {

    private var dashakootDataList: MutableList<DashakootData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashakootAnalysisViewHolder {
        val binding =
            RowItemDashakootBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashakootAnalysisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashakootAnalysisViewHolder, position: Int) {
        holder.bind(dashakootDataList[position],position)
    }

    override fun getItemCount(): Int {
        return dashakootDataList.size
    }

    fun submitData(list: List<DashakootData>) {
        dashakootDataList.clear()
        dashakootDataList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class DashakootAnalysisViewHolder(
        val binding: RowItemDashakootBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DashakootData, position: Int) {
            if (position%2 == 0){
                binding.clLayout1.hideVisibility()
                binding.clLayout2.showVisibility()

                binding.tvAttributeTitle2.text = data.title
                binding.tvUser1Title2.text = data.p1
                binding.tvUser2Title2.text = data.p2
                binding.tvPointsObtained2.text = data.points_obtained.toString()
                binding.tvReceivedCount2.text = data.max_ponits.toString()

            }else{

                binding.clLayout2.hideVisibility()
                binding.clLayout1.showVisibility()

                binding.tvAttributeTitle1.text = data.title
                binding.tvUser1Title1.text = data.p1
                binding.tvUser2Title1.text = data.p2
                binding.tvPointsObtained1.text = data.points_obtained.toString()
                binding.tvReceivedCount1.text = data.max_ponits.toString()
            }


        }
    }
}