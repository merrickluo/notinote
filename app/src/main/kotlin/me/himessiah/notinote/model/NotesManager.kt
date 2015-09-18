package me.himessiah.notinote.model

import android.util.Log
import io.realm.Realm
import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import rx.lang.kotlin.observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject

public object NotesManager {

    private val observableNotes: BehaviorSubject<Array<Note>> = BehaviourSubject(allNotes())

    fun notesObservable(): Observable<Array<Note>> {
        return observableNotes.asObservable()
    }

    fun allNotes(): Array<Note> {
        val result = Realm.getDefaultInstance().where(RealmNote::class.java).findAll()
        return Array(result.size(), { i -> Note(result.get(i)) })
    }

    fun removeNote(id: Long): Observable<Unit> {
        return observable { subscriber ->
            removeNoteFromRealm(id)
            updateNotesObservable()
            subscriber.onNext(Unit)
            subscriber.onCompleted()
        }
    }

    fun addNote(noteContent: String): Observable<Note> {
        return observable { subscriber ->
            val savedNote = saveNoteToRealm(Note(noteContent))
            updateNotesObservable()
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

    private fun removeNoteFromRealm(id: Long) {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val dbNote = realm.where(RealmNote::class.java).equalTo("id", id).findFirst()
        dbNote.removeFromRealm()
        realm.commitTransaction()
    }

    private fun nextNoteKey(): Long {
        val realm = Realm.getDefaultInstance()
        return realm.where(RealmNote::class.java).maximumInt("id") + 1
    }

    private fun updateNotesObservable() {
        observableNotes.onNext(allNotes())
    }

}