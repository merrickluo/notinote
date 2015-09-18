package me.himessiah.notinote.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.Date

@RealmClass
public open class RealmNote : RealmObject() {

    @PrimaryKey public open var id: Long = 0
    public open var content: String = ""
    public open var added: Date = Date()
    public open var notify: Date = Date()

}