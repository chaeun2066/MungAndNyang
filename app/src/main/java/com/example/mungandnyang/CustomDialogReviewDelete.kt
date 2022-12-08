package com.example.mungandnyang

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import com.example.mungandnyang.databinding.CustomDialogReviewDeleteBinding
import com.example.mungandnyang.databinding.ReviewItemBinding

class CustomDialogReviewDelete(val context: Context, val reviewItemBinding: ReviewItemBinding) {
    val dialog = Dialog(context)

    fun showDialog(intent: Intent) {
        val binding = CustomDialogReviewDeleteBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        //Custom Dialog Window Size 조절
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        binding.btnYes.setOnClickListener {
            //관리자 비밀번호 점검
            if(binding.edtPassword.text.toString().equals("1163")){
                val adoptDAO = AdoptDAO()
                //해당 게시글 UID를 가진 Storage 이미지 삭제
                adoptDAO.storage!!.reference.child("images/${intent.getStringExtra("docId")}.jpg").delete()
                //해당 게시글 UID를 가진 Firebase 후기 내용 삭제
                adoptDAO.deleteReview(intent.getStringExtra("docId")!!).addOnSuccessListener {
                    Toast.makeText(binding.root.context, "삭제 성공", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(binding.root.context, "삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(binding.root.context, "관리자 비밀번호 오류", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}