package com.demon.yu.na

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.demon.yu.avd.WebVadHelper
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_vad.*
import permissions.dispatcher.*


class VadActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vad)
        startRecord.setOnClickListener {

        }
    }


    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    fun showAudio() {

    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    fun showRationaleForAudio(request: PermissionRequest) {

    }


    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    fun onAudioDenied() {
        Toast.makeText(this,"语音权限", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    fun onAudioNeverAskAgain() {
        Toast.makeText(this,"", Toast.LENGTH_SHORT).show()
    }


}