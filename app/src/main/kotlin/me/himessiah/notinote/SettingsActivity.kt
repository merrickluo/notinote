package me.himessiah.notinote

import android.app.Activity
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import de.greenrobot.event.EventBus
import me.himessiah.notinote.model.Constant
import me.himessiah.notinote.model.Events

public class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        getFragmentManager().beginTransaction()
                .replace(R.id.content, SettingsFragment)
                .commit()
    }

    object SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_general)

            val enableService = findPreference(Constant.EnableServiceKey)
            enableService.setOnPreferenceChangeListener { pref, newValue ->
                if (newValue as Boolean) {
                    EventBus.getDefault().post(Events.StartService)
                } else {
                    EventBus.getDefault().post(Events.StopService)
                }
                true
            }

            val priority = findPreference(Constant.NotificationPriorityKey)
            priority.setOnPreferenceChangeListener{ pref, newValue ->
                EventBus.getDefault().post(Events.UpdatePriority(newValue.toString()))
                true
            }
        }
    }
}