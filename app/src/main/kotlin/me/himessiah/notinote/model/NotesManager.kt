package me.himessiah.notinote.model

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import rx.Observable
import rx.lang.kotlin.observable
import kotlin.properties.Delegates

public class NotesManager(ctx: Context) {

    var realm: Realm by Delegates.notNull()

    init {
        val defaultConfiguration =
                RealmConfiguration.Builder(ctx)
                        .inMemory()
                        .build()

        Realm.setDefaultConfiguration(defaultConfiguration)
        realm = Realm.getDefaultInstance()

        Note("晚上洗衣服").save()
        Note("把法爷升到70级").save()
    }

    fun firstNote(): Observable<Note> {
        return observable { subscriber ->

        }
    }

    fun allNotes(): Observable<Array<Note>> {
        return observable<Array<Note>> { subscriber ->
            val result = realm.where(javaClass<RealmNote>()).findAll()
            subscriber.onNext(Array(result.size(), { i -> Note(result.get(i)) }))
            subscriber.onCompleted()
        }
    }
}