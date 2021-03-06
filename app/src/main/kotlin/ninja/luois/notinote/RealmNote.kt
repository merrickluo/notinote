package ninja.luois.notinote

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.util.Date

@RealmClass
open class RealmNote : RealmObject() {
    open var content: String = ""
    open var added: Date = Date()
    open var notify: Date = Date()
    open var id: String = ""
}