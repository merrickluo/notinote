package ninja.luois.notinote

import android.content.Intent

public class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        val service: Intent = Intent(this, NotificationService::class.java)
        startService(service)
    }
}
