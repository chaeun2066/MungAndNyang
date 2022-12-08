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
        // 동시에 boolean 값과 현재 list의 Position 값 삽입할 수 있는 Array (단점: Data의 양이 많을수록 속도 저하)
        // SparseBooleanArray : Int 정수에 Boolean 값을 Mapping 하는 것 ex) Map(int key, boolean value)
        var selectedItems: SparseBooleanArray = SparseBooleanArray()
        val binding = (holder as AdoptViewHolder).binding
        val adoptAnimal = adoptList[position]
        animalDatabase = Firebase.database.reference
        //Database의 AdoptPhoto라는 자식 객체 접근
        animalDatabase.child("AdoptPhoto").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    val currentAnimal = postSnapshot.getValue(PhotoVO::class.java)
                    // 현재 Adoptphoto에 있는 동물 사진의 동물 번호와 adoptlist에 있는 동물들의 번호가 같을 때 사진을 매칭
                    if(currentAnimal!!.ani_number == adoptAnimal.number){
                        Glide.with(context.applicationContext).load("https://${currentAnimal!!.photo_url}").into(binding.ivThumb)
                        Glide.with(context.applicationContext).load("https://${currentAnimal!!.photo_url}").into(binding.ivDeinfoPicture)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.tvInfoName.text = adoptAnimal.name
        binding.tvDeinfoName.text = adoptAnimal.name

        if(adoptAnimal.gender.equals("W")){
            binding.ivGender.setImageResource(R.drawable.female)
            binding.ivDeinfoGender.setImageResource(R.drawable.female)
        }else{
            binding.ivGender.setImageResource(R.drawable.male)
            binding.ivDeinfoGender.setImageResource(R.drawable.male)
        }

        if(adoptAnimal.type.equals("CAT")){
            binding.ivType.setImageResource(R.drawable.cat)
            binding.ivDeinfoType.setImageResource(R.drawable.cat)
        }else{
            binding.ivType.setImageResource(R.drawable.dog)
            binding.ivDeinfoType.setImageResource(R.drawable.dog)
        }

        binding.tvInfoKind.text = adoptAnimal.breed
        binding.tvDeinfoKind.text = adoptAnimal.breed
        binding.tvDeinfoWeight.text = adoptAnimal.weight + "kg"
        binding.tvDeinfoNumber.text = adoptAnimal.number.substring(0..3)
        binding.tvDeinfoAge.text = adoptAnimal.age
        binding.tvInfoDate.text = adoptAnimal.date
        binding.tvDeinfoDate.text = adoptAnimal.date

        binding.btnGuide.setOnClickListener {
            val intent = Intent(binding.root.context, AdoptguideActivity::class.java)
            context.startActivity(intent)
        }

        binding.btnSite.setOnClickListener {
            val number = adoptAnimal.number!!.substring(0 until 4)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://animal.seoul.go.kr/animalInfo/view?aniNo=${number}"))
            context.startActivity(intent)
        }

        binding.root.setOnClickListener {
            //내가 선택한 ItemView의 position(Int) + boolean (default : Fasle)
            if (selectedItems[position]) { // selectedItems.get(position, false)
                //해당 ItemView를 삭제, delete = put(position, false)
                selectedItems.delete(position)
                binding.infoLayout.visibility = View.VISIBLE
                binding.detailLayout.visibility = View.GONE
            } else {
                //boolean 값이 true라면 Detail Layout이 확장
                selectedItems.put(position, true)
                binding.infoLayout.visibility = View.GONE
                binding.detailLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return adoptList.size
    }

    class AdoptViewHolder(val binding: AdoptItemBinding): RecyclerView.ViewHolder(binding.root)
}