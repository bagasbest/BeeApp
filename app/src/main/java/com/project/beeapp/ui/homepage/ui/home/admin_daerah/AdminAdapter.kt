package com.project.beeapp.ui.homepage.ui.home.admin_daerah

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ItemAdminListBinding
import com.project.beeapp.databinding.ItemPromotionBinding
import com.project.beeapp.ui.homepage.ui.home.promotion.PromotionModel

class AdminAdapter : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {


    private val adminList = ArrayList<AdminModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<AdminModel>) {
        adminList.clear()
        adminList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemAdminListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: AdminModel) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                fullname.text = model.fullname
                model.locationTask?.distinct()
                binding.locationTask.text = model.locationTask?.joinToString(", ")

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, AdminDaerahDetailActivity::class.java)
                    intent.putExtra(AdminDaerahDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAdminListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(adminList[position])
    }

    override fun getItemCount(): Int = adminList.size
}