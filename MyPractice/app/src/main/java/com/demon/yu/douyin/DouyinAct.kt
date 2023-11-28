package com.demon.yu.douyin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.R

/**
 * @description
 * @author yujinta.529
 * @create 2023-11-20
 */
class DouyinAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_douyin_act)
        val listenAweme = findViewById<View>(R.id.listenAweme)
        listenAweme.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("snssdk1128://listen/feed?show_skylight=true&title=listenaweme&enter_from=xiaoyi"))
            startActivity(intent)
        }

    }
}