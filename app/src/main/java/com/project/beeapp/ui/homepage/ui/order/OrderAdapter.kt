package com.project.beeapp.ui.homepage.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.beeapp.R
import com.project.beeapp.databinding.ItemOrderBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {


    private val orderList = ArrayList<OrderModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<OrderModel>) {
        orderList.clear()
        orderList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(orderModel: OrderModel) {
            with(binding) {



                orderId.text = "ID Pemesanna: INV-${orderModel.orderId}"
                customerName.text = "Pemesan: ${orderModel.username}"
                date.text = "Tanggal: ${orderModel.date}"
                orderType.text = "${orderModel.orderType}, ${orderModel.option}"
                status.text = orderModel.status


                if(orderModel.orderType == "BeeWash" && orderModel.option == "car") {
                    Glide.with(itemView.context)
                        .load(R.drawable.wash)
                        .into(image)
                } else if (orderModel.orderType == "BeeWash" && orderModel.option == "bike") {
                    Glide.with(itemView.context)
                        .load(R.drawable.motocycle)
                        .into(image)
                } else if (orderModel.orderType == "BeeTire" && orderModel.option == "bike") {
                    Glide.with(itemView.context)
                        .load(R.drawable.tires)
                        .into(image)
                } else if (orderModel.orderType == "BeeTire" && orderModel.option == "car") {
                    Glide.with(itemView.context)
                        .load(R.drawable.tires)
                        .into(image)
                } else if (orderModel.orderType == "BeeFuel") {
                    Glide.with(itemView.context)
                        .load(R.drawable.fuel)
                        .into(image)
                }

                when (orderModel.status) {
                    "Order Diterima" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_orange_dark)
                    }
                    "Sudah Bayar" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_green_dark)
                    }
                    "Selesai" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.purple_500)
                    }
                    else -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.darker_gray)
                    }
                }

                

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, OrderDetailActivity::class.java)
                    intent.putExtra(OrderDetailActivity.EXTRA_ORDER, orderModel)
                    itemView.context.startActivity(intent)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size
}