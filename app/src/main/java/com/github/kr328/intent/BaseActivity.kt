package com.github.kr328.intent

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kr328.intent.compat.isLightNavigationBarCompat
import com.github.kr328.intent.compat.isLightStatusBarsCompat
import com.github.kr328.intent.compat.resolveThemedBoolean
import com.github.kr328.intent.compat.resolveThemedColor

abstract class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        applyDayNight()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        recreate()
    }

    private fun applyDayNight(config: Configuration = resources.configuration) {
        if ((config.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            theme.applyStyle(R.style.AppThemeComposeDark, true)
        } else {
            theme.applyStyle(R.style.AppThemeComposeLight, true)
        }

        window.statusBarColor = resolveThemedColor(android.R.attr.statusBarColor)
        window.navigationBarColor = resolveThemedColor(android.R.attr.navigationBarColor)

        window.isLightStatusBarsCompat =
            resolveThemedBoolean(android.R.attr.windowLightStatusBar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.isLightNavigationBarCompat =
                resolveThemedBoolean(android.R.attr.windowLightNavigationBar)
        }
    }
}