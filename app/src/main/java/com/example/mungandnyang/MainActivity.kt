package com.example.mungandnyang

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mungandnyang.databinding.ActivityMainBinding
import com.example.mungandnyang.databinding.TabLayoutItemBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var adoptlistFragment: AdoptlistFragment
    lateinit var reviewFragment: ReviewFragment
    var mBackWait:Long = 0
    lateinit var userAuth : FirebaseAuth
    var mySharedPreferences: MySharedPreferences = MySharedPreferences()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //구글 인증 사용자
        userAuth = Firebase.auth

        //Toolbar 설정
        setSupportActionBar(binding.toolbar)

        //viewPagerAdapter 설정
        val pagerAdapter = PagerAdapter(this)
        val title = mutableListOf<String>("보호 동물", "입양 후기")

        adoptlistFragment = AdoptlistFragment()
        reviewFragment = ReviewFragment()

        //Fragment와 TabTitle
        pagerAdapter.addFragment(adoptlistFragment, title[0])
        pagerAdapter.addFragment(reviewFragment, title[1])

        binding.mainViewPager2.adapter = pagerAdapter

        //Fragment와 Tablayout 서로 부착
        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager2){ tab, position ->
            tab.setCustomView(createTabView(title[position]))
        }.attach()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_logout_24)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun createTabView(title: String): View {
        val useTabBinding = TabLayoutItemBinding.inflate(layoutInflater)
        useTabBinding.tvTitle.text = title
        return useTabBinding.root
    }

    //Toolbar 메뉴 기능 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        val menuItem = menu?.findItem(R.id.menu_chat)

        menuItem?.setOnMenuItemClickListener {
            val intent = Intent(this, FriendslistActivity::class.java)
            startActivity(intent)
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    //로그아웃 버튼 눌렀을 때, 다이얼로그 발생
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val eventHandler = object : DialogInterface.OnClickListener{
            override fun onClick(userDialog: DialogInterface?, p1: Int) {
                if(p1 == DialogInterface.BUTTON_POSITIVE){
                    //계정 로그아웃
                    userAuth.signOut()
                    //shared 파일 초기화
                    mySharedPreferences.clearUser(this@MainActivity)
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "로그아웃 하셨습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        when(item.itemId) {
            android.R.id.home -> {
                AlertDialog.Builder(this).run {
                    setTitle("알림")
                    setIcon(android.R.drawable.ic_dialog_alert)
                    setMessage("정말 로그아웃 하시겠습니까?")
                    setPositiveButton("네", eventHandler)
                    setNegativeButton("아니오", eventHandler)
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭 - 2초 이내 클릭한다면, 종료
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Snackbar.make(binding.root,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}