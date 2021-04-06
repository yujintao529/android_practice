package com.demon.yu.camera

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demon.yu.utils.ToastUtils
import com.demon.yu.utils.isEmpty
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_camera2_preview.*
import java.io.File


class Camera2PreviewActivity : AppCompatActivity() {
    companion object {
        fun startPreviewActivity(context: Context, filePath: String) {
            context.startActivity(Intent(context, Camera2PreviewActivity::class.java).apply {
                putExtra("file_path", filePath)
            })
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2_preview)
        val filePath = intent.getStringExtra("file_path")
        if (filePath.isEmpty()) {
            ToastUtils.toast("没有图片路径")
            finish()
        }
        previewView.setImageURI(Uri.fromFile(File(filePath)))
    }
}