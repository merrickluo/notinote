package me.himessiah.notinote

import android.content.Intent
import io.realm.Realm
import io.realm.RealmConfiguration

public class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        val defaultConfiguration = RealmConfiguration.Builder(this)
                .build()

        Realm.setDefaultConfiguration(defaultConfiguration)

        val service: Intent = Intent(this, javaClass<NotificationService>())
        startService(service)
    }
}
