package com.adobe.phonegap.push

import android.app.Activity
import android.app.KeyguardManager
import android.app.KeyguardManager.KeyguardDismissCallback
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.annotation.Nullable

class FullScreenActivity : Activity() {
    public override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onCreate")
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()
        forceMainActivityReload()
        finish()
    }

    private fun forceMainActivityReload() {
        val pm = packageManager
        val launchIntent = pm.getLaunchIntentForPackage(applicationContext.packageName)
        launchIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        launchIntent.addFlags(Intent.FLAG_FROM_BACKGROUND)
        startActivity(launchIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Log.d(LOG_TAG, "setShowWhenLocked")
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            Log.d(LOG_TAG, "addFlags")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
        val candidate = getSystemService(KEYGUARD_SERVICE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && candidate != null) {
            val keyguardManager = candidate as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, object : KeyguardDismissCallback() {
                override fun onDismissError() {
                    super.onDismissError()
                    Log.d(LOG_TAG, "onDismissError")
                }

                override fun onDismissSucceeded() {
                    super.onDismissSucceeded()
                    Log.d(LOG_TAG, "onDismissSucceeded")
                }
            })
        }
    }

    private fun turnScreenOffAndKeyguardOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }

    companion object {
        private const val LOG_TAG = "FullScreenActivity"
    }
}