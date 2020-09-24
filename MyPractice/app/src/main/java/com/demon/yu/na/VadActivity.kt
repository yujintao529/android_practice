package com.demon.yu.na

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_vad.*
import permissions.dispatcher.*

@RuntimePermissions
class VadActivity : AppCompatActivity() {


    private var audioRecordManager: AudioRecordManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vad)
        audioRecordManager = AudioRecordManager()
        audioRecordManager?.setOnPersonDetectListener {
            Log.d("VadActivity", " detect $it")
        }
        startRecord.setOnClickListener {
            showAudioWithPermissionCheck()
        }
    }


    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    fun showAudio() {
        audioRecordManager?.startRecord()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecordManager?.stop()
        audioRecordManager?.dead()
    }


    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    fun showRationaleForAudio(request: PermissionRequest) {
        showRationaleDialog("大哥，给我录音权限好么～", request)
    }


    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    fun onAudioDenied() {
        Toast.makeText(this, "语音权限", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    fun onAudioNeverAskAgain() {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
    }

    private fun showRationaleDialog(message: String, request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setPositiveButton("允许") { _, _ -> request.proceed() }
                .setNegativeButton("拒绝") { _, _ -> request.cancel() }
                .setCancelable(false)
                .setMessage(message)
                .show()
    }


}