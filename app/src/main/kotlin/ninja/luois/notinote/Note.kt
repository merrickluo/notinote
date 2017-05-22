package ninja.luois.notinote

import io.realm.Realm
import java.util.*

class Note {

    var id: String = ""
    var content: String = ""
    var added: Date = Date()
    var notify: Date = Date()

    constructor(str: String) {
        content = str
        added = Date()
        notify = Date()
        id = UUID.randomUUID().toString()
    }

    constructor(dbNote: RealmNote) {
        content = dbNote.content
        added = dbNote.added
        notify = dbNote.notify
        id = dbNote.id
    }

    fun save() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val dbNote = realm.createObject(RealmNote::class.java)
        dbNote.content = content
        dbNote.added = added
        dbNote.notify = notify
        dbNote.id = id
        realm.commitTransaction()
    }

}
