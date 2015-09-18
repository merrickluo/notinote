package me.himessiah.notinote.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager

import me.himessiah.notinote.NotificationService

public class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constant.StartAtBootKey, false)) {
            startService(context)
        }
    }

    fun startService(context: Context) {
        val service: Intent = Intent(context, NotificationService::class.java)
        context.startService(service)
    }

}
