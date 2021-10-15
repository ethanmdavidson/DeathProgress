//package com.machinerychorus.lifeprogresswallpaperkotlin
//
//import android.content.SharedPreferences
//import android.graphics.Canvas
//import android.preference.PreferenceManager
//import android.service.wallpaper.WallpaperService
//import android.view.SurfaceHolder
//
//class Wallpaper : WallpaperService() {
//    override fun onCreateEngine(): Engine {
//        return WallpaperEngine()
//    }
//
//    private inner class WallpaperEngine() : Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
//        private var lastDrawTime: Long = 0
//        override fun onDestroy() {
//            //remove preference listener
//            PreferenceManager.getDefaultSharedPreferences(applicationContext)
//                .unregisterOnSharedPreferenceChangeListener(this)
//        }
//
//        override fun onSurfaceCreated(holder: SurfaceHolder) {
//            super.onSurfaceCreated(holder)
//            drawFrame()
//        }
//
//        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
//            super.onSurfaceDestroyed(holder)
//        }
//
//        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder) {
//            drawFrame()
//        }
//
//        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
//            drawFrame()
//        }
//
//        override fun onVisibilityChanged(visible: Boolean) {
//            //only check the time between draws here, because in all other events we want
//            //it to redraw every time
//            if (visible && System.currentTimeMillis() - lastDrawTime > MIN_TIME_BETWEEN_DRAWS_MS) {
//                drawFrame()
//            }
//        }
//
//        fun drawFrame() {
//            var canvas: Canvas? = null
//            try {
//                canvas = surfaceHolder.lockCanvas()
//                if (canvas != null) {
//                    val pref = PreferenceManager.getDefaultSharedPreferences(
//                        applicationContext
//                    )
//                    val birthdate =
//                        DateTime.parse(pref.getString(getString(R.string.birthdateKey), "1994"))
//                    //get screen width/height from surface because getDesiredMinimumWidth()/Height()
//                    // doesn't always return the correct values (e.g. 1280x800 tablet returned 1920x1280)
//                    val screenWidth = surfaceHolder.surfaceFrame.right
//                    val screenHeight = surfaceHolder.surfaceFrame.bottom
//                    val hoursAlive = Hours.hoursBetween(birthdate, DateTime()).hours
//                        .toFloat()
//                    val yearsExpectancy = pref.getString(getString(R.string.expectancyKey), "85")!!
//                        .toInt()
//                    var hoursExpectancy =
//                        Hours.hoursBetween(birthdate, birthdate.plusYears(yearsExpectancy)).hours
//                            .toFloat()
//                    if (hoursExpectancy <= 0) {
//                        hoursExpectancy = 1.0f
//                    } //prevent div by zero
//                    val percentDead = hoursAlive / hoursExpectancy
//                    val paint = Paint()
//                    paint.style = Paint.Style.FILL
//                    paint.color = pref.getInt(
//                        getString(R.string.bgColorKey), ContextCompat.getColor(
//                            applicationContext, R.color.wholesomeTeal
//                        )
//                    )
//                    canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)
//                    paint.color = pref.getInt(
//                        getString(R.string.fgColorKey), ContextCompat.getColor(
//                            applicationContext, R.color.blackAsMySOUUUUUUUULLLL
//                        )
//                    )
//                    canvas.drawRect(
//                        0f, (screenHeight - (screenHeight * percentDead).toInt()).toFloat(),
//                        screenWidth.toFloat(), screenHeight.toFloat(), paint
//                    )
//                    paint.textSize =
//                        pref.getString(getString(R.string.progressFontSizeKey), "240")!!.toFloat()
//                    val progressLabel = String.format(
//                        Locale.US,
//                        "%." + pref.getString(getString(R.string.decimalsKey), "4") + "f%%",
//                        percentDead * 100f
//                    )
//                    canvas.drawText(
//                        progressLabel,
//                        10f,
//                        (screenHeight - (screenHeight * percentDead).toInt() - 10).toFloat(),
//                        paint
//                    )
//
//                    //draw goals text
//                    val statusBarHeight = pref.getInt(getString(R.string.statusBarHeightKey), 100)
//                    paint.textSize =
//                        pref.getString(getString(R.string.goalsFontSizeKey), "75")!!.toFloat()
//                    val goals = pref.getString(getString(R.string.goalsKey), "")!!
//                        .split("\n").toTypedArray()
//                    var lineNumber = 0
//                    val fontHeight = paint.fontMetrics.descent - paint.fontMetrics.ascent
//                    for (line in goals) {
//                        canvas.drawText(
//                            line,
//                            10f,
//                            statusBarHeight + (0 - paint.fontMetrics.top) + lineNumber * fontHeight,
//                            paint
//                        )
//                        lineNumber += 1
//                    }
//                }
//                lastDrawTime = System.currentTimeMillis()
//            } finally {
//                if (canvas != null) {
//                    surfaceHolder.unlockCanvasAndPost(canvas)
//                }
//            }
//        }
//
//        init {
//            setOffsetNotificationsEnabled(false)
//            PreferenceManager.getDefaultSharedPreferences(applicationContext)
//                .registerOnSharedPreferenceChangeListener(this)
//        }
//    }
//
//    companion object {
//        //only draw every hour, in order to keep battery usage down.
//        //Users would be able to see it move every 10 minutes with like 6 digits precision
//        //so it might be cool to have it increase draw rate as precision increases
//        private const val MIN_TIME_BETWEEN_DRAWS_MS = 3600000
//    }
//}