package com.demon.yu.avatar.interact

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.Logger
import com.example.mypractice.R
import kotlin.random.Random

class CloneXAvatarComposeAct : AppCompatActivity() {
    private var lightInteractView: LightInteractView? = null
    private var circleBg: View? = null
    private var rootView: FrameLayout? = null
    private val lightInteractComponent: LightInteractComponent by lazy {
        LightInteractComponent.getLightInteractComponent(this)
    }

    private var cloneXAvatarComposeLayout: CloneXAvatarComposeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clonexavator_compose_layout)
        lightInteractView = findViewById(R.id.lightInteractView)
        rootView = findViewById(R.id.rootView)
        circleBg?.visibility = View.GONE
        lightInteractView?.updateList(createInteractModels(), true)
        cloneXAvatarComposeLayout = findViewById(R.id.cloneXAvatarComposeLayout)
        cloneXAvatarComposeLayout?.updateData(createColorObj(20))
        cloneXAvatarComposeLayout?.avatarComposeRecyclerView?.onLayoutListener =
            object : CloneXComposeRecyclerView.OnLayoutListener {
                override fun onCenter(point: Point) {
                    Logger.debug(CloneXComposeUiConfig.TAG, "onCenter point = $point")
                    lightInteractView?.setInteractPoint(point)
                }
            }
        cloneXAvatarComposeLayout?.onCenterChangeListener =
            object : CloneXAvatarComposeLayout.OnAvatarComposeListener {
                override fun onCenter(position: Int, any: Any?) {
                    Log.d("CloneXAvatar", "OnCenterChangeListener onCenter position=$position")
                }

                override fun onScrolled() {
                    lightInteractView?.backToIdleLock()
                    Log.d("CloneXAvatar", "OnCenterChangeListener onScrolled")
                }
            }
        findViewById<View>(R.id.submit).setOnClickListener {
            val input = findViewById<EditText>(R.id.inputNumber)
            val result = input.editableText.toString().toIntOrNull()
            if (result != null && result > 0) {
//                val colorObj = createAvatarObj(1)
//                val dest = mutableListOf<CloneXStaticObj>()
//                dest.addAll(colorObj)
//                dest.addAll(avatarObj)
//                val obj = createAvatarWebpObj(Uri.parse("asset:///avatar2.webp"))
//                cloneXAvatarComposeLayout?.notifyItemChanged(1, obj)
//                val mutable = mutableListOf<CloneXStaticObj>()
//                mutable.add(createAvatarWebpObj(Uri.parse("asset:///avatar2.webp")))
//                mutable.add(createAvatarWebpObj(Uri.parse("asset:///avatar3.webp")))
                cloneXAvatarComposeLayout?.notifyItemChanged(createColorObj(result - 1))
            }
        }
    }


    private fun createInteractModels(): List<LightInteractModel> {
        val drawableArr =
            arrayListOf(R.drawable.heart, R.drawable.zhuoyizhuo, R.drawable.baba, R.drawable.paozhu)
        return (0..10).map { LightInteractModel(resourceID = drawableArr[it % 4]) }
    }


    private fun createAvatarWebpObj(uri: Uri? = null): CloneXStaticObj {
        return CloneXStaticObj(viewType = 1).apply {
            assetAvatarUri = uri
        }
    }

    private val assetsArray =
        mutableListOf("asset:///avatar2.webp", "asset:///avatar1.webp", "asset:///avatar3.webp")

    private fun createAvatar(number: Int): List<CloneXStaticObj> {
        return (0 until number).map {
            createAvatarWebpObj(Uri.parse(assetsArray[Random.nextInt(0, assetsArray.size)]))
        }.toList()
    }

    private fun createAvatarAndColorObj(
        colorNumber: Int,
        placeHolderNumber: Int,
        avatarNumber: Int
    ): List<CloneXStaticObj> {
        return (0 until (colorNumber + placeHolderNumber + avatarNumber)).map {
            if (it < colorNumber) {
                CloneXStaticObj(viewType = 2)
            } else if (it < (placeHolderNumber + colorNumber)) {
                createAvatarWebpObj(Uri.parse("asset:///avatar_placeholder_anim.webp"))
            } else {
                createAvatarWebpObj(Uri.parse("asset:///avatar2.webp"))
            }
        }.toList()
    }

    private fun createAvatarAndColorObj(
        colorNumber: Int,
        avatarNumber: Int
    ): List<CloneXStaticObj> {
        return (0 until (colorNumber + avatarNumber)).map {
            if (it < avatarNumber) {
                createAvatarWebpObj(Uri.parse("asset:///avatar2.webp"))
            } else {
                CloneXStaticObj(viewType = 2)
            }
        }.toList()
    }


    private fun createColorObj(number: Int): List<CloneXStaticObj> {
        return (0 until number).map { CloneXStaticObj(viewType = 2) }.toList()
    }

}