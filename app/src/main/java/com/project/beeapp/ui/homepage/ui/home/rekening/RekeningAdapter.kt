package com.project.beeapp.ui.homepage.ui.home.rekening

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
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
        fun bind(model: RekeningModel, rekeningList: ArrayList<RekeningModel>) {
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

                delete.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Konfirmasi Menghapus Rekening")
                        .setMessage("Apa kamu yakin ingin menghapus Rekening ini ?")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("YA") { dialogInterface, _ ->
                            dialogInterface.dismiss()

                            model.uid?.let { it1 ->
                                FirebaseFirestore
                                    .getInstance()
                                    .collection("payment")
                                    .document(it1)
                                    .delete()
                                    .addOnCompleteListener { task ->
                                        if(task.isSuccessful) {
                                            rekeningList.removeAt(adapterPosition)
                                            notifyDataSetChanged()
                                            Toast.makeText(itemView.context, "Berhasil menghapus Rekening ini!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                        .setNegativeButton("TIDAK", null)
                        .show()
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
        holder.bind(rekeningList[position], rekeningList)
    }

    override fun getItemCount(): Int = rekeningList.size
}