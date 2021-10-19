package com.machinerychorus.lifeprogresswallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.SharedPreferences
import android.view.View
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, WallpaperSettingsFragment())
            .commit()

        /* The wallpaper service needs to know the height of the status/notification bar so
        that it can avoid drawing the text underneath the status bar (looks bad).
        Apparently the best way to do this is by getting the data while inside an activity,
        then saving it in the preferences so it can be access from the service.
        https://stackoverflow.com/questions/3044552
        https://stackoverflow.com/questions/3355367
        This method also assumes that the status bar is always at the top.
        We default to 100 so that it (hopefully) won't be covered by the status bar even if we don't
            get the real height.
        */
        var statusBarHeight = 100
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            applicationContext
        )
        pref.edit().putInt(getString(R.string.statusBarHeightKey), statusBarHeight).apply()
    }


    fun setWallpaper(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(this, LifeWallpaper::class.java))
        startActivity(intent)
    }
}