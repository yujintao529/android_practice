package com.demo.yu.http

import com.demon.yu.utils.HttpUtils
import com.demon.yu.utils.ThreadPoolUtils

object HttpManager {


    fun request(request: Request, callBack: CallBack) {
        val r = RequestRunnable(request, callBack)
        ThreadPoolUtils.getIoExecutor().execute(r)
    }


    open class CallBack {
        open fun onResult(result: String) {

        }
    }

    class RequestRunnable(private val request: Request, val callBack: CallBack) : Runnable {
        override fun run() {
            val result = HttpUtils.getInstance().getString(request.url)
            callBack.onResult(result)
        }

    }
}