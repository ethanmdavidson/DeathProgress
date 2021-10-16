package com.machinerychorus.lifeprogresswallpaper

import android.app.WallpaperColors
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*

class Wallpaper : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    private inner class WallpaperEngine : Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        private var lastDrawTime: Long = 0

        init {
            setOffsetNotificationsEnabled(false)
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onDestroy() {
            //remove preference listener
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            prepAndDraw()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder) {
            prepAndDraw()
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
            prepAndDraw()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            //only check the time between draws here, because in all other events we want
            //it to redraw every time
            if (visible && System.currentTimeMillis() - lastDrawTime > MIN_TIME_BETWEEN_DRAWS_MS) {
                prepAndDraw()
            }
        }

        fun prepAndDraw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                canvas?.drawFrame()
                lastDrawTime = System.currentTimeMillis()
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }

        fun Canvas.drawFrame() {
            val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val birthdate = DATE_FORMATTER.parse(
                pref.getString(getString(R.string.birthdateKey), DEFAULT_DATE)!!)!!
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            //get screen width/height from surface because getDesiredMinimumWidth()/Height()
            // doesn't always return the correct values (e.g. 1280x800 tablet returned 1920x1280)
            val screenWidth = surfaceHolder.surfaceFrame.right
            val screenHeight = surfaceHolder.surfaceFrame.bottom
            val hoursAlive = ChronoUnit.HOURS.between(birthdate, LocalDateTime.now())
            val yearsExpectancy = pref.getString(getString(R.string.expectancyKey), "85")!!.toLong()
            val hoursExpectancy = ChronoUnit.HOURS.between(birthdate, birthdate.plusYears(yearsExpectancy))
            val percentDead = hoursAlive.toFloat() / hoursExpectancy.toFloat()
            val paint = Paint()
            paint.style = Paint.Style.FILL
            paint.color = pref.getInt(
                getString(R.string.bgColorKey), ContextCompat.getColor(
                    applicationContext, R.color.wholesomeTeal
                )
            )
            this.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)
            paint.color = pref.getInt(
                getString(R.string.fgColorKey), ContextCompat.getColor(
                    applicationContext, R.color.blackAsMySOUUUUUUUULLLL
                )
            )
            this.drawRect(
                0f, (screenHeight - (screenHeight * percentDead).toInt()).toFloat(),
                screenWidth.toFloat(), screenHeight.toFloat(), paint
            )
            paint.textSize =
                pref.getString(getString(R.string.progressFontSizeKey), "240")!!.toFloat()
            val progressLabel = String.format(
                Locale.US,
                "%." + pref.getString(getString(R.string.decimalsKey), "4") + "f%%",
                percentDead * 100f
            )
            this.drawText(progressLabel, 10f,
                (screenHeight - (screenHeight * percentDead).toInt() - 10).toFloat(),
                paint
            )

            //draw goals text
            val statusBarHeight = pref.getInt(getString(R.string.statusBarHeightKey), 100)
            paint.textSize =
                pref.getString(getString(R.string.goalsFontSizeKey), "75")!!.toFloat()
            val goals = pref.getString(getString(R.string.goalsKey), "")!!
                .split("\n").toTypedArray()
            var lineNumber = 0
            val fontHeight = paint.fontMetrics.descent - paint.fontMetrics.ascent
            for (line in goals) {
                val textWidth = paint.measureText(line)
                this.drawText(line, (screenWidth-textWidth)/2,
                    statusBarHeight + (0 - paint.fontMetrics.top) + lineNumber * fontHeight,
                    paint
                )
                lineNumber += 1
            }
        }

        @RequiresApi(Build.VERSION_CODES.O_MR1)
        override fun onComputeColors(): WallpaperColors {
            val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val primary = pref.getInt(getString(R.string.bgColorKey), ContextCompat.getColor(
                    applicationContext, R.color.wholesomeTeal))
            val secondary = pref.getInt(getString(R.string.fgColorKey), ContextCompat.getColor(
                    applicationContext, R.color.blackAsMySOUUUUUUUULLLL))
            return WallpaperColors(Color.valueOf(primary), Color.valueOf(secondary), null)
        }
    }

    companion object {
        //only draw every hour, in order to keep battery usage down.
        //Users would be able to see it move every 10 minutes with like 6 digits precision
        //so it might be cool to have it increase draw rate as precision increases
        private const val MIN_TIME_BETWEEN_DRAWS_MS = 3600000
        val DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        const val DEFAULT_DATE = "1994-05-31"
    }
}