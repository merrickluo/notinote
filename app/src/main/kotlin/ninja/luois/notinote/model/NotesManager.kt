package ninja.luois.notinote.model

import android.content.Context
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

        Note("晚上洗衣服").save()
        Note("把法爷升到70级").save()
    }

    fun firstNote(): Observable<Note> {
        return Observable.create<Note> { subscriber ->
        }
    }

    fun allNotes(): Observable<Array<Note>> {
        return Observable.create<Array<Note>> { subscriber ->
            val results = realm.where(RealmNote::class.java).findAll()
            subscriber.onNext(Array(results.size, { i -> Note(results[i]) }))
            subscriber.onComplete()
        }
    }
}