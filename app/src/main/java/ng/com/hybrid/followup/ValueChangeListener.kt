package ng.com.hybrid.followup

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

interface ValueChangeListener: ValueEventListener {
    override fun onCancelled(error: DatabaseError) {

    }

    override fun onDataChange(snapshot: DataSnapshot)
}