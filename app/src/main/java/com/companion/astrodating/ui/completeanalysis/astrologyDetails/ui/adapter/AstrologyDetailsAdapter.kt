package com.companion.astrodating.ui.completeanalysis.astrologyDetails.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.databinding.RowItemAstrologyBinding
import com.companion.astrodating.ui.completeanalysis.astrologyDetails.ui.model.AstrologyDetailsData
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.showVisibility

class AstrologyDetailsAdapter :
    RecyclerView.Adapter<AstrologyDetailsAdapter.AstrologyDetailsViewHolder>() {

    private var dashakootDataList: MutableList<AstrologyDetailsData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AstrologyDetailsViewHolder {
        val binding =
            RowItemAstrologyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AstrologyDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AstrologyDetailsViewHolder, position: Int) {
        holder.bind(dashakootDataList[position],position)
    }

    override fun getItemCount(): Int {
        return dashakootDataList.size
    }

    fun submitData(list: List<AstrologyDetailsData>) {
        dashakootDataList.clear()
        dashakootDataList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class AstrologyDetailsViewHolder(
        val binding: RowItemAstrologyBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AstrologyDetailsData, position: Int) {
            if (position%2 == 0){
                binding.clLayout1.hideVisibility()
                binding.clLayout2.showVisibility()

                binding.tvAttributeTitle2.text = data.attribute
                binding.tvUser1Title2.text = data.user1
                binding.tvUser2Title2.text = data.user2

            }else{

                binding.clLayout2.hideVisibility()
                binding.clLayout1.showVisibility()

                binding.tvAttributeTitle1.text = data.attribute
                binding.tvUser1Title1.text = data.user1
                binding.tvUser2Title1.text = data.user2
            }


        }
    }
}