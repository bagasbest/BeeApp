package com.project.beeapp.ui.homepage.ui.home.promotion

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ItemPromotionBinding


class PromotionAdapter : RecyclerView.Adapter<PromotionAdapter.ViewHolder>() {


    private val promotionList = ArrayList<PromotionModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<PromotionModel>) {
        promotionList.clear()
        promotionList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemPromotionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: PromotionModel, promotionList: ArrayList<PromotionModel>) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)



                change.setOnClickListener {

                }

                delete.setOnClickListener {
                    model.uid?.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("image_slider")
                            .document(it1)
                            .delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    promotionList.removeAt(adapterPosition)
                                    notifyDataSetChanged()
                                    Toast.makeText(itemView.context, "Berhasil menghapus promosi", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(promotionList[position], promotionList)
    }

    override fun getItemCount(): Int = promotionList.size
}