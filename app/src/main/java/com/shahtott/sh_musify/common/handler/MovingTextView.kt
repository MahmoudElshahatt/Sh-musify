package com.shahtott.sh_musify.common.handler

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
class MovingTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    private var textShader: Shader? = null
    private var shaderDx = 0f
    private val shaderSpeed = 2f
    private var isMovingRight = true

    init {
        // Initialize the text shader with a linear gradient
        textShader = LinearGradient(0f, 0f, 0f, textSize, intArrayOf(currentTextColor,
            currentTextColor, currentTextColor), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        paint.shader = textShader
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.translate(shaderDx, 0f)
        super.onDraw(canvas)

        if (isMovingRight) {
            shaderDx += shaderSpeed
            if (shaderDx > width + textSize) {
                shaderDx = -textSize
            }
        } else {
            shaderDx -= shaderSpeed
            if (shaderDx < -textSize) {
                shaderDx = width.toFloat()
            }
        }

        postInvalidateOnAnimation()
    }

}