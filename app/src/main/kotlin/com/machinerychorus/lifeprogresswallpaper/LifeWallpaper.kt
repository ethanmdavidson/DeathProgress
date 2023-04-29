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
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

const val DEFAULT_EXPECTANCY_YEARS = "85"
const val DEFAULT_NUM_DECIMALS = "4"
const val DEFAULT_STATUS_BAR_HEIGHT = 100
const val DAYS_IN_YEAR = 365.2425
const val PROGRESS_LABEL_MARGIN = 10f

class LifeWallpaper : WallpaperService() {
	override fun onCreateEngine(): Engine {
		return WallpaperEngine()
	}

	@Suppress("TooManyFunctions") // eh, whatever
	private inner class WallpaperEngine : Engine(),
		SharedPreferences.OnSharedPreferenceChangeListener {
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

		@Suppress("LongMethod") // I guess I should refactor this, but don't feel like it right now
		fun Canvas.drawFrame() {
			val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
			val percentDead = getPercentDead(pref)

			//get screen width/height from surface because getDesiredMinimumWidth()/Height()
			// doesn't always return the correct values (e.g. 1280x800 tablet returned 1920x1280)
			val screenWidth = surfaceHolder.surfaceFrame.right
			val screenHeight = surfaceHolder.surfaceFrame.bottom
			val paint = Paint()

			// Draw background color across whole screen
			paint.style = Paint.Style.FILL
			paint.color = getBackgroundColor(pref)
			this.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)

			// Draw foreground color across part of screen corresponding to progress
			val reverse = pref.getBoolean(getString(R.string.reverseKey), false)
			paint.color = getForegroundColor(pref)
			var top = (screenHeight - (screenHeight * percentDead).toInt()).toFloat()
			var bottom = screenHeight.toFloat()
			if (reverse) {
				bottom = top
				top = 0f
			}
			this.drawRect(
				0f, top, screenWidth.toFloat(), bottom, paint
			)

			// Draw progress text at edge of progress bar
			paint.textSize =
				pref.getString(getString(R.string.progressFontSizeKey), "240")!!.toFloat()
			var progressText = percentDead * @Suppress("MagicNumber") 100f
			if (reverse) {
				progressText = (1 - percentDead) * @Suppress("MagicNumber") 100f
			}
			val progressLabel = String.format(
				Locale.US,
				"%." + pref.getString(
					getString(R.string.decimalsKey),
					DEFAULT_NUM_DECIMALS
				) + "f%%",
				progressText
			)
			var textBaseline = (screenHeight - (screenHeight * percentDead) - PROGRESS_LABEL_MARGIN)
			if (reverse) {
				textBaseline -= paint.fontMetrics.ascent
			}
			this.drawText(
				progressLabel, PROGRESS_LABEL_MARGIN,
				textBaseline,
				paint
			)

			//draw goals text
			if (reverse) {
				paint.color = getBackgroundColor(pref)
			}
			val statusBarHeight = pref.getInt(
				getString(R.string.statusBarHeightKey),
				DEFAULT_STATUS_BAR_HEIGHT
			)
			paint.textSize = pref.getString(getString(R.string.goalsFontSizeKey), "75")!!.toFloat()
			val goals =
				pref.getString(getString(R.string.goalsKey), "")!!.split("\n").toTypedArray()
			var lineNumber = 0
			val goalsFontHeight = paint.fontMetrics.descent - paint.fontMetrics.ascent
			for (line in goals) {
				val textWidth = paint.measureText(line)
				this.drawText(
					line, (screenWidth - textWidth) / 2,
					statusBarHeight - paint.fontMetrics.top + lineNumber * goalsFontHeight,
					paint
				)
				lineNumber += 1
			}
		}

		private fun getPercentDead(pref: SharedPreferences): Float {
			val birthdate = DATE_FORMATTER.parse(
				pref.getString(getString(R.string.birthdateKey), DEFAULT_DATE)!!
			)!!.toInstant()
			val hoursAlive = ChronoUnit.HOURS.between(birthdate, Instant.now())
			val yearsPref =
				pref.getString(getString(R.string.expectancyKey), DEFAULT_EXPECTANCY_YEARS)!!
			val yearsExpectancy =
				Duration.of((yearsPref.toLong() * DAYS_IN_YEAR).toLong(), ChronoUnit.DAYS)
			val hoursExpectancy =
				ChronoUnit.HOURS.between(birthdate, birthdate.plus(yearsExpectancy))
			return hoursAlive.toFloat() / hoursExpectancy.toFloat()
		}

		private fun getBackgroundColor(pref: SharedPreferences): Int {
			return pref.getInt(
				getString(R.string.bgColorKey), ContextCompat.getColor(
					applicationContext, R.color.wholesomeTeal
				)
			)
		}

		private fun getForegroundColor(pref: SharedPreferences): Int {
			return pref.getInt(
				getString(R.string.fgColorKey), ContextCompat.getColor(
					applicationContext, R.color.blackAsMySOUUUUUUUULLLL
				)
			)
		}

		@RequiresApi(Build.VERSION_CODES.O_MR1)
		override fun onComputeColors(): WallpaperColors {
			val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
			val primary = getBackgroundColor(pref)
			val secondary = getForegroundColor(pref)
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
