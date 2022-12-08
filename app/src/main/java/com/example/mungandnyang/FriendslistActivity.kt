package com.example.mungandnyang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungandnyang.databinding.ActivityFriendslistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FriendslistActivity : AppCompatActivity() {
    lateinit var binding : ActivityFriendslistBinding
    lateinit var adapter: UserAdapter
    lateinit var userAuth: FirebaseAuth
    lateinit var database : DatabaseReference
    lateinit var userList : MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendslistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAuth = Firebase.auth
        database = Firebase.database.reference

        userList = mutableListOf<User>()
        adapter = UserAdapter(this, userList)

        binding.allUserRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.allUserRecyclerView.setHasFixedSize(true)
        binding.allUserRecyclerView.adapter = adapter

        setSupportActionBar(binding.friendlistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //사용자 정보 가져오기
        database.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    //나를 제외한 모든 유저의 리스트
                    if(userAuth.currentUser?.uid != currentUser?.uId){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}