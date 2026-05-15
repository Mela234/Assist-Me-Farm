package com.cropdoc.app.data.agent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("cropdoc", Context.MODE_PRIVATE)
            if (prefs.getBoolean("agent_active", false)) {
                com.cropdoc.app.CropDocApplication.instance.scheduleAgent()
            }
        }
    }
}