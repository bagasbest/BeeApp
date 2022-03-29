package com.project.beeapp.ui.homepage.ui.home.rekening

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.project.beeapp.databinding.ItemRekeningBinding

class RekeningAdapter(
   val etBankName: TextInputEditText?,
   val etRecName: TextInputEditText?,
   val etRecNumber: TextInputEditText?,
    val addOrEdit: TextView?
) : RecyclerView.Adapter<RekeningAdapter.ViewHolder>() {



    private val rekeningList = ArrayList<RekeningModel>()
    var rekeningUid: String = ""

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<RekeningModel>) {
        rekeningList.clear()
        rekeningList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemRekeningBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: RekeningModel) {
            with(binding) {
                bankName.text = model.bankName
                recName.text = "Pemilik Rekening: ${model.recName}"
                recNumber.text = "Nomor Rekening: ${model.recNumber}"


                cv.setOnClickListener {
                    rekeningUid = model.uid.toString()
                    addOrEdit?.text = "Edit Rekening"
                    etBankName?.setText(model.bankName)
                    etRecName?.setText(model.recName)
                    etRecNumber?.setText(model.bankName)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRekeningBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rekeningList[position])
    }

    override fun getItemCount(): Int = rekeningList.size
}