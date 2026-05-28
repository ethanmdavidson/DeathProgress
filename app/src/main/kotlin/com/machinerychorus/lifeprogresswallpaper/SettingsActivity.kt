package com.machinerychorus.lifeprogresswallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		if (savedInstanceState == null) {
			supportFragmentManager
				.beginTransaction()
				.replace(android.R.id.content, WallpaperSettingsFragment())
				.commit()
		}

		ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

			/* The wallpaper service needs to know the height of the status/notification bar so
			that it can avoid drawing the text underneath the status bar (looks bad).
			We get this from the system insets.
			This method also assumes that the status bar is always at the top.
			*/
			val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
				applicationContext,
			)
			pref.edit { putInt(getString(R.string.statusBarHeightKey), systemBars.top) }

			insets
		}
	}


	fun setWallpaper(@Suppress("UNUSED_PARAMETER") view: View) {
		val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
		intent.putExtra(
			WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
			ComponentName(this, LifeWallpaper::class.java)
		)
		startActivity(intent)
	}
}
