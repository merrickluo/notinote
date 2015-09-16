package me.himessiah.notinote.model

import android.util.Log
import io.realm.Realm
import rx.Observable
import rx.lang.kotlin.observable

public object NotesManager {

    fun allNotes(): Observable<Array<Note>> {
        return observable<Array<Note>> { subscriber ->
            val result = Realm.getDefaultInstance().where(javaClass<RealmNote>()).findAll()
            subscriber.onNext(Array(result.size(), { i -> Note(result.get(i)) }))
            subscriber.onCompleted()
        }
    }

    fun removeNote(id: Long): Observable<Unit> {
        return observable { subscriber ->
            Log.w("llllll", "attempting to remove note for id:" + id)
            removeNoteFromRealm(id)
            subscriber.onNext(Unit)
            subscriber.onCompleted()
        }
    }

    fun addNote(noteContent: String): Observable<Note> {
        return observable { subscriber ->
            val savedNote = saveNoteToRealm(Note(noteContent))
            subscriber.onNext(savedNote)
            subscriber.onCompleted()
        }
    }


    private fun saveNoteToRealm(note: Note): Note {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val dbNote = realm.copyToRealm(note.toDbNote())
        dbNote.id = nextNoteKey()
        realm.commitTransaction()

        return Note(dbNote)
    }

    fun removeNoteFromRealm(id: Long) {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val dbNote = realm.where(javaClass<RealmNote>()).equalTo("id", id).findFirst()
        dbNote.removeFromRealm()
        realm.commitTransaction()
    }

    fun nextNoteKey(): Long {
        val realm = Realm.getDefaultInstance()
        return realm.where(javaClass<RealmNote>()).maximumInt("id") + 1
    }

}