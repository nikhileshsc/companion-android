package com.companion.astrodating.ui.interests.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.databinding.RowItemInterestsBinding
import com.companion.astrodating.ui.home.domain.model.InterestDomain

class InterestsAdapter(private val listOfInterests : List<InterestDomain>) : RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder>() {

    var onItemClicked: ((InterestDomain) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestsViewHolder {
        val binding = RowItemInterestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InterestsViewHolder(binding) { onItemClicked?.invoke(it) }
    }

    override fun onBindViewHolder(holder: InterestsViewHolder, position: Int) {
        holder.bind(listOfInterests[position])
    }

    override fun getItemCount(): Int {
        return listOfInterests.size
    }

//    fun submitList(categoryList: List<InterestDomain>) {
//        listOfInterests.clear()
//        listOfInterests = categoryList.toMutableList()
//        notifyDataSetChanged()
//    }

    inner class InterestsViewHolder(val binding: RowItemInterestsBinding,onItemClicked: ((InterestDomain) -> Unit)) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cvItem.setOnClickListener{
                onItemClicked(listOfInterests[adapterPosition])
            }
        }
        fun bind(interestDomain: InterestDomain) {
            binding.ivItem.setImageResource(interestDomain.image)
            binding.tvTitle.text = binding.tvTitle.context.getString(interestDomain.title)
        }
    }
}