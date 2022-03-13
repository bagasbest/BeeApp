package com.project.beeapp.ui.homepage.ui.home.verify_driver

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.beeapp.R
import com.project.beeapp.databinding.ItemOrderBinding
import com.project.beeapp.databinding.ItemVerifyDriverBinding
import com.project.beeapp.ui.homepage.ui.home.akumulasi_pendapatan_mitra.AccumulatePartnerOrderDetailActivity
import com.project.beeapp.ui.homepage.ui.order.OrderDetailActivity
import com.project.beeapp.ui.homepage.ui.order.OrderModel

class VerifyDriverAdapter(private val option: String) : RecyclerView.Adapter<VerifyDriverAdapter.ViewHolder>() {


    private val driverList = ArrayList<VerifyDriverModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<VerifyDriverModel>) {
        driverList.clear()
        driverList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemVerifyDriverBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: VerifyDriverModel) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                fullname.text = model.fullname
                address.text = "${model.locKabupaten}, ${model.locProvinsi}"
                status.text = model.status

                when (model.status) {
                    "Menunggu" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.darker_gray)
                    }
                    "Aktif" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_green_dark)
                    }
                    "Blokir" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_orange_dark)
                    }
                    "PHK" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_red_dark)
                    }
                    "Ditolak" -> {
                        bgStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, android.R.color.holo_red_light)
                    }
                }


                if(option == "verify") {
                    cv.setOnClickListener {
                        val intent = Intent(itemView.context, VerifyDriverDetailActivity::class.java)
                        intent.putExtra(VerifyDriverDetailActivity.EXTRA_DRIVER, model)
                        itemView.context.startActivity(intent)
                    }
                } else {
                    cv.setOnClickListener {
                        val intent = Intent(itemView.context, AccumulatePartnerOrderDetailActivity::class.java)
                        intent.putExtra(AccumulatePartnerOrderDetailActivity.EXTRA_DRIVER, model)
                        itemView.context.startActivity(intent)
                    }
                }



            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVerifyDriverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(driverList[position])
    }

    override fun getItemCount(): Int = driverList.size
}