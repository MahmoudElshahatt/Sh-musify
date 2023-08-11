package com.shahtott.sh_musify.common.extentions

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.*

fun Activity.showContentAboveStatusBar(color: Int = -1) {
    try {
        window.apply {
            statusBarColor = if (color != -1) Color.TRANSPARENT else color
            setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }
    } catch (e: Exception) {
        Log.e("ActivityExtensions", "showContentAboveStatusBar: ${e.localizedMessage}")
    }
}

fun Activity.showContentNormallyUnderStatusBar(color: Int) {
    window.apply {
        statusBarColor = ContextCompat.getColor(this@showContentNormallyUnderStatusBar, color)
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}


fun TextView.startAutoTextScrolling(
    screenWidth: Float,
    duration: Int = 10000,
    text: String,
    startFromLeft: Boolean = true
) {

    val fromX = if (startFromLeft) -1f else 1f
    val toX = if (startFromLeft) 1f else -1f

    val animator = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, fromX,
        Animation.RELATIVE_TO_SELF, toX,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 0f
    ).apply {
        interpolator = LinearInterpolator()
        this.duration = duration.toLong()
        fillAfter = true
        repeatMode = Animation.RESTART
        repeatCount = Animation.INFINITE
    }

    this.text = text
    isSelected = true
    startAnimation(animator)
}
