package me.himessiah.notinote

import android.content.Intent
import android.preference.PreferenceManager
import de.greenrobot.event.EventBus
import io.realm.Realm
import io.realm.RealmConfiguration
import me.himessiah.notinote.model.Constant
import me.himessiah.notinote.model.Events

public class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        val defaultConfiguration = RealmConfiguration.Builder(this)
                .build()

        Realm.setDefaultConfiguration(defaultConfiguration)
        EventBus.getDefault().register(this)

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constant.EnableServiceKey, true)) {
            startService()
        }
    }

    fun startService() {
        val service: Intent = Intent(this, NotificationService::class.java)
        startService(service)
    }

    fun stopService() {
        val service: Intent = Intent(this, NotificationService::class.java)
        stopService(service)
    }

    fun onEvent(event: Events.StartService) {
        startService()
    }

    fun onEvent(event: Events.StopService) {
        stopService()
    }

}
