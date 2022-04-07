package com.project.beeapp.ui.homepage.ui.home.promotion

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.beeapp.databinding.ItemPromoHomeBinding

class PromotionHomeAdapter : RecyclerView.Adapter<PromotionHomeAdapter.ViewHolder>() {


    private val promotionList = ArrayList<PromotionModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<PromotionModel>) {
        promotionList.clear()
        promotionList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemPromoHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: PromotionModel) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPromoHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(promotionList[position])
    }

    override fun getItemCount(): Int = promotionList.size
}