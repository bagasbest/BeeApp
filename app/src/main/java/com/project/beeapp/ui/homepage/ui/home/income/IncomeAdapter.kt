package com.project.beeapp.ui.homepage.ui.home.income

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.beeapp.databinding.ItemIncomeBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class IncomeAdapter : RecyclerView.Adapter<IncomeAdapter.ViewHolder>() {


    private val incomeList = ArrayList<IncomeModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<IncomeModel>) {
        incomeList.clear()
        incomeList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemIncomeBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: IncomeModel) {
            val nominalCurrency: NumberFormat = DecimalFormat("#,###")
            with(binding) {
                orderType.text = model.orderType
                orderId.text = "Order ID: ${model.orderId}"
                price.text = "Rp.${nominalCurrency.format(model.income)}"

                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(incomeList[position])
    }

    override fun getItemCount(): Int = incomeList.size
}