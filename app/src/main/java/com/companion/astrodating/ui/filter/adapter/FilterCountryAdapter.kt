package com.companion.astrodating.ui.filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.R
import com.companion.astrodating.databinding.RowItemFilterBinding
import com.companion.astrodating.ui.filter.model.FilterDomainEntity

class FilterCountryAdapter : RecyclerView.Adapter<FilterCountryAdapter.FilterViewHolder>() {

    private var list: MutableList<FilterDomainEntity> = arrayListOf()
//    var onItemClick: ((FilterDomainOptionEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding =
            RowItemFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FilterViewHolder(val binding: RowItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            if (list[bindingAdapterPosition].isSelected) {
                binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_selected)
            } else {
                binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_unselected)
            }
            binding.tvItem.text = list[bindingAdapterPosition].title

            itemView.setOnClickListener {
                list[bindingAdapterPosition].isSelected = !list[bindingAdapterPosition].isSelected
                if (list[bindingAdapterPosition].isSelected) {
                    binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_selected)
                } else {
                    binding.ivFilterItem.setImageResource(R.drawable.ic_checkbox_unselected)
                }
            }
        }
    }

    fun submitData(filterDomainList: List<FilterDomainEntity>) {
        list.clear()
        this.list = filterDomainList.toMutableList()
        notifyDataSetChanged()
    }

    fun getListOfSelectedCountry(): ArrayList<String> {
        val listOfSelectedItem: ArrayList<String> = ArrayList()
        for (i in list.indices) {
            if (list[i].isSelected) {
                listOfSelectedItem.add(list[i].title)
            }
        }
        return listOfSelectedItem
    }
    fun selectAll(select: Boolean) {
        list.forEach { it.isSelected = select }
        notifyDataSetChanged()
    }
    fun clearAll() {
        list.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }
}
