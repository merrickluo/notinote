package ninja.luois.notinote

import android.app.IntentService
import android.content.Intent

public class DeleteNoteService(name: String) : IntentService(name) {

    constructor() : this("name") {

    }

    override fun onHandleIntent(intent: Intent) {
    }
}
