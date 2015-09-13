package ninja.luois.notinote.model

import io.realm.Realm
import java.util.Date

class Note {

    var content: String = ""
    var added: Date = Date()
    var notify: Date = Date()


    constructor(str: String) {
        content = str
        added = Date()
        notify = Date()
    }

    constructor(dbNote: RealmNote) {
        content = dbNote.content
        added = dbNote.added
        notify = dbNote.notify
    }

    fun save() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val dbNote = realm.createObject(RealmNote::class.java)
        dbNote.content = content
        dbNote.added = added
        dbNote.notify = notify
        realm.commitTransaction()
    }

}
