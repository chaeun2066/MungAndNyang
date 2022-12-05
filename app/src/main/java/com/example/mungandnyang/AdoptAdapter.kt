package com.example.mungandnyang

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mungandnyang.databinding.AdoptItemBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdoptAdapter(val context: Context, val adoptList: MutableList<AnimalVO>):RecyclerView.Adapter<AdoptAdapter.AdoptViewHolder>() {
    lateinit var animalDatabase : DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdoptViewHolder {
        val binding = AdoptItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return AdoptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdoptViewHolder, position: Int) {
        var selectedItems: SparseBooleanArray = SparseBooleanArray()
        val binding = (holder as AdoptViewHolder).binding
        val adoptAnimal = adoptList[position]
        animalDatabase = Firebase.database.reference

        animalDatabase.child("AdoptPhoto").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    val currentAnimal = postSnapshot.getValue(PhotoVO::class.java)
                    if(currentAnimal!!.ani_number == adoptAnimal.number){
                        Glide.with(context).load("https://${currentAnimal!!.photo_url}").into(binding.ivThumb)
                        Glide.with(context).load("https://${currentAnimal!!.photo_url}").into(binding.ivDeinfoPicture)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        binding.tvInfoName.text = adoptAnimal.name
        binding.deinfoName.text = adoptAnimal.name
        if(adoptAnimal.gender.equals("W")){
            binding.ivGender.setImageResource(R.drawable.female)
            binding.ivDeinfoGender.setImageResource(R.drawable.female)
        }else{
            binding.ivGender.setImageResource(R.drawable.male)
            binding.ivDeinfoGender.setImageResource(R.drawable.male)
        }
        binding.tvInfoKind.text = adoptAnimal.breed
        binding.deinfoKind.text = adoptAnimal.breed
        binding.deinfoWeight.text = adoptAnimal.weight + "kg"
        binding.deinfoNumber.text = adoptAnimal.number
        binding.deinfoAge.text = adoptAnimal.age
        binding.deinfoDate.text = adoptAnimal.date

        binding.btnGuide.setOnClickListener {
            val intent = Intent(binding.root.context, AdoptguideActivity::class.java)
            context.startActivity(intent)
        }

        binding.btnSite.setOnClickListener {
            val number = adoptAnimal.number!!.substring(0 until 4)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://animal.seoul.go.kr/animalInfo/view?aniNo=${number}"))
            context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            if (selectedItems[position]) {
                selectedItems.delete(position)
                binding.infoLayout.visibility = View.GONE
                binding.detailLayout.visibility = View.VISIBLE
            } else {
                selectedItems.put(position, true)
                binding.infoLayout.visibility = View.VISIBLE
                binding.detailLayout.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return adoptList.size
    }

    class AdoptViewHolder(val binding: AdoptItemBinding): RecyclerView.ViewHolder(binding.root)
}