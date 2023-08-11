package com.shahtott.sh_musify.common.handler

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView


fun TextView.startAutoTextHorizontalScrolling(
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
