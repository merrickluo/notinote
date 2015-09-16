package me.himessiah.notinote.model

import io.realm.Realm
import java.util.Date

public class Note {

    public var id: Long = 0
    public var content: String = ""
    public var added: Date = Date()
    public var notify: Date = Date()


    constructor(str: String) {
        content = str
        added = Date()
        notify = Date()
    }

    constructor(dbNote: RealmNote) {
        id = dbNote.id
        content = dbNote.content
        added = dbNote.added
        notify = dbNote.notify
    }

    public fun toDbNote(): RealmNote {
        val dbNote = RealmNote()
        dbNote.content = content
        dbNote.added = added
        dbNote.notify = notify

        return dbNote
    }



}
