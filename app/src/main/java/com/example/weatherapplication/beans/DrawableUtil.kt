package com.example.weatherapplication.utils

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

object DrawableUtil {
    fun getTextDrawable(initial: String, color: Int, size: Int): Drawable {
        val textSize = size / 2.5f

        val paint = Paint()
        paint.color = color
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        paint.color = Color.WHITE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER

        val xPos = (canvas.width / 2)
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

        canvas.drawText(initial, xPos.toFloat(), yPos, paint)

        return BitmapDrawable(bitmap)
    }

    private fun getColor(initial: String): Int {
        val colors = listOf(
            "#FFB300", "#FF4081", "#3F51B5", "#4CAF50", "#03A9F4",
            "#8BC34A", "#FF5722", "#795548", "#9E9E9E", "#607D8B"
        )
        val index = initial.uppercase()[0].code % colors.size
        return Color.parseColor(colors[index])
    }

    fun getTextDrawableWithColor(initial: String, size: Int): Drawable {
        val color = getColor(initial)
        return getTextDrawable(initial, color, size)
    }
}
