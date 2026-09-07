package com.companion.astrodating.ui.otp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.ui.otp.domain.model.CountryDomainEntity

class CountryAdapter(
    mContext: Context,
    private var countryList: MutableList<CountryDomainEntity> = ArrayList()
) : ArrayAdapter<CountryDomainEntity>(mContext, 0, countryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_country, parent, false)

        val tvCountryName: TextView = view.findViewById(R.id.countryName)
        val ivCountryFlag: ImageView = view.findViewById(R.id.countryImage)

        tvCountryName.text = getItem(position)?.country
        Glide.with(parent.context).load(getItem(position)?.iconUrl).into(ivCountryFlag)

        return view
    }

    fun submitData(countryList: List<CountryDomainEntity>) {
        this.countryList.clear()
        this.countryList.addAll(countryList)
        notifyDataSetChanged()
    }
}