package com.example.mungandnyang

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mungandnyang.databinding.ReviewItemBinding

class UploadAdapter(val context: Context, val uploadList: MutableList<UploadVO>): RecyclerView.Adapter<UploadAdapter.UploadViewHolder>() {

    override fun onBindViewHolder(viewholder: UploadViewHolder, position: Int) {
        val binding = (viewholder as UploadViewHolder).binding
        val uploadData = uploadList.get(position)

        binding.tvRevTitle.text = uploadData.name
        binding.tvRevDate.text = uploadData.date
        uploadData.docId
        val adoptDAO = AdoptDAO()
        val imgRef = adoptDAO.storage!!.reference.child("images/${uploadData.docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener {
            if(it.isSuccessful){
                Glide.with(context).load(it.result).centerCrop().into(binding.ivRevPicture)
                binding.ivRevHashtag.setImageResource(R.drawable.phone)
            }
        }

        viewholder.itemView.setOnClickListener {
            val intent = Intent(binding.root.context, DetailInfoActivity::class.java)
            intent.putExtra("docId", uploadData.docId)
            intent.putExtra("name", uploadData.name)
            intent.putExtra("gender", uploadData.gender)
            intent.putExtra("breed", uploadData.breed)
            intent.putExtra("date", uploadData.date)
            intent.putExtra("text", uploadData.text)
            context.startActivity(intent)
        }

        viewholder.itemView.setOnLongClickListener {
            val intent = Intent(binding.root.context, CustomDialogReviewDelete::class.java)
            val dialog = CustomDialogReviewDelete(binding.root.context, binding)
            intent.putExtra("docId", uploadData.docId)
            dialog.showDialog(intent)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadViewHolder {
        var binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UploadViewHolder(binding)
    }

    override fun getItemCount() = uploadList.size

    class UploadViewHolder(val binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root)
}