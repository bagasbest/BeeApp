package com.project.beeapp.ui.homepage.ui.notification

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.beeapp.R
import com.project.beeapp.databinding.ItemNotificationBinding
import com.project.beeapp.databinding.ItemOrderBinding
import com.project.beeapp.ui.homepage.ui.order.OrderDetailActivity
import com.project.beeapp.ui.homepage.ui.order.OrderModel

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    private val notificationList = ArrayList<NotificationModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<NotificationModel>) {
        notificationList.clear()
        notificationList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: NotificationModel) {
            with(binding) {
                title.text = model.title
                date.text = model.date
                message.text = model.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int = notificationList.size
}