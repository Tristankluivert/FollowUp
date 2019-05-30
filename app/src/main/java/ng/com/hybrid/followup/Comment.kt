package ng.com.hybrid.followup

import com.google.firebase.database.DataSnapshot

class Comment constructor(main: CommentMain, snapshot: DataSnapshot?):
        CommentMain(main.body, main.author, main.score, main.id) {
    val children : ArrayList<Comment> = ArrayList()

    init {
        if (snapshot?.exists() == true) {
            snapshot.children.forEach {
                it.getValue(CommentMain::class.java)?.apply {
                    val comment = Comment(this, snapshot.child("children"))
                    children.add(comment)
                }
            }
        }
    }

}

open class CommentMain(val body : String,
                  val author : String,
                  val score : Int,
                  val id : String)