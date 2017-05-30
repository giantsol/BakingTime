package com.lee.hansol.bakingtime.extensions

import android.app.Activity
import android.util.Log

private val Activity.TAG
    get() = "hello"

private fun Activity.log(msg: String) {
    Log.d(TAG, msg)
}

private fun Activity.log(msgId: Int) {
    log(getString(msgId) ?: throw RuntimeException("No string id for $msgId"))
}
