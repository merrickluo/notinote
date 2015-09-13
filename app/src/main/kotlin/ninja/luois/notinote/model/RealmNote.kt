package ninja.luois.notinote.model

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.util.Date

@RealmClass
open class RealmNote : RealmObject() {
    open var content: String = ""
    open var added: Date = Date()
    open var notify: Date = Date()
}