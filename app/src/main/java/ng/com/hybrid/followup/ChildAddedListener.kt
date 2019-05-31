package ng.com.hybrid.followup

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

interface ChildAddedListener: ChildEventListener {

    override fun onCancelled(error: DatabaseError) {

    }

    override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {

    }

    override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {

    }

    override fun onChildRemoved(snapshot: DataSnapshot) {

    }

    override fun onChildAdded(snapshot: DataSnapshot, p1: String?)
}