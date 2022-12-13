package com.demon.yu.na

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_cstandard.*


class CStandardActivity : AppCompatActivity() {

//    private var studentNative: StudentNative? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cstandard)
        createStudentTV.setOnClickListener {
//            if (studentNative != null) {
//                studentNative = StudentNative()
//            }
        }
        setStudentAgeTV.setOnClickListener {
//            studentNative?.setAge(10)
        }
        releaseStudentTV.setOnClickListener {
//            studentNative?.release()
//            studentNative = null
        }
    }


}