package me.himessiah.notinote.model

import io.realm.Realm
import java.util.Date

public class Note {

    public var content: String = ""
    public var added: Date = Date()
    public var notify: Date = Date()


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

    public fun save() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val dbNote = realm.createObject(javaClass<RealmNote>())
        dbNote.content = content
        dbNote.added = added
        dbNote.notify = notify
        realm.commitTransaction()
    }

}
