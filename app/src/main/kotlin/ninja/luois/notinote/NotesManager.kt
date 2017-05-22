package ninja.luois.notinote

import android.content.Context
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlin.properties.Delegates

class NotesManager(ctx: Context) {

    var realm: Realm by Delegates.notNull()

    init {
        Realm.init(ctx)
        val defaultConfiguration = RealmConfiguration.Builder().inMemory().build()

        Realm.setDefaultConfiguration(defaultConfiguration)
        realm = Realm.getDefaultInstance()
    }

    fun allNotes(): Observable<Array<Note>> {
        val ob = realm.where(RealmNote::class.java).findAllAsync().asObservable()
                .map { results ->
                    Array(results.size, { i -> Note(results[i]) })
                }
        return RxJavaInterop.toV2Observable(ob)
    }

    fun addNote(text: String) {
        Note(text).save()
    }

    fun deleteNote(noteId: String) {
        realm.beginTransaction()
        realm.where(RealmNote::class.java).equalTo("id", noteId).findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }
}