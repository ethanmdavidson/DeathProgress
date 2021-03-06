package com.machinerychorus.lifeprogresswallpaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.util.Locale;

import androidx.core.content.ContextCompat;

public class LifeWallpaper extends WallpaperService {
	//only draw every hour, in order to keep battery usage down.
	//Users would be able to see it move every 10 minutes with like 6 digits precision
	//so it might be cool to have it increase draw rate as precision increases
	private static final int MIN_TIME_BETWEEN_DRAWS_MS = 3600000;

	public LifeWallpaper() {
	}

	@Override
	public Engine onCreateEngine() {
		JodaTimeAndroid.init(this);

		return new WallpaperEngine();
	}

	private class WallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
		private long lastDrawTime;

		WallpaperEngine(){
			setOffsetNotificationsEnabled(false);
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onDestroy(){
			//remove preference listener
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			drawFrame();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
		}

		@Override
		public void onSurfaceRedrawNeeded(SurfaceHolder holder){
			drawFrame();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
			drawFrame();
		}

		@Override
		public void onVisibilityChanged(boolean visible){
			//only check the time between draws here, because in all other events we want
			//it to redraw every time
			if(visible && System.currentTimeMillis() - lastDrawTime > MIN_TIME_BETWEEN_DRAWS_MS){
				drawFrame();
			}
		}

		void drawFrame()
		{
			Canvas canvas = null;
			try {
				canvas = getSurfaceHolder().lockCanvas();
				if (canvas != null) {
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					DateTime birthdate = DateTime.parse(pref.getString(getString(R.string.birthdateKey), "1994"));
					//get screen width/height from surface because getDesiredMinimumWidth()/Height()
					// doesn't always return the correct values (e.g. 1280x800 tablet returned 1920x1280)
					int screenWidth = getSurfaceHolder().getSurfaceFrame().right;
					int screenHeight = getSurfaceHolder().getSurfaceFrame().bottom;

					float hoursAlive = (float) Hours.hoursBetween(birthdate, new DateTime()).getHours();
					int yearsExpectancy = Integer.parseInt(pref.getString(getString(R.string.expectancyKey), "85"));
					float hoursExpectancy = (float)Hours.hoursBetween(birthdate, birthdate.plusYears(yearsExpectancy)).getHours();
					if(hoursExpectancy <= 0){ hoursExpectancy = 1.0f; } //prevent div by zero
					float percentDead = hoursAlive / hoursExpectancy;

					Paint paint = new Paint();
					paint.setStyle(Paint.Style.FILL);

					paint.setColor(pref.getInt(getString(R.string.bgColorKey), ContextCompat.getColor(getApplicationContext(), R.color.wholesomeTeal)));
					canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

					paint.setColor(pref.getInt(getString(R.string.fgColorKey), ContextCompat.getColor(getApplicationContext(), R.color.blackAsMySOUUUUUUUULLLL)));
					canvas.drawRect(0, screenHeight-(int)(screenHeight*percentDead),
                            screenWidth, screenHeight, paint);

					paint.setTextSize(Float.parseFloat(pref.getString(getString(R.string.progressFontSizeKey), "240")));
					String progressLabel = String.format(Locale.US,
							"%."+pref.getString(getString(R.string.decimalsKey), "4")+"f%%",percentDead*100f);
					canvas.drawText(progressLabel, 10, screenHeight-(int)(screenHeight*percentDead)-10, paint);

					//draw goals text
					int statusBarHeight = pref.getInt(getString(R.string.statusBarHeightKey), 100);
					paint.setTextSize(Float.parseFloat(pref.getString(getString(R.string.goalsFontSizeKey), "75")));
					String[] goals = pref.getString(getString(R.string.goalsKey), "").split("\n");
					int lineNumber = 0;
					float fontHeight = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;
					for(String line : goals){
						canvas.drawText(line, 10, statusBarHeight+(0-paint.getFontMetrics().top)+(lineNumber * fontHeight), paint);
						lineNumber += 1;
					}
				}
				lastDrawTime = System.currentTimeMillis();
			} finally {
				if (canvas != null) {
					getSurfaceHolder().unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
