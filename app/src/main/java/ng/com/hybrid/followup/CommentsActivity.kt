package ng.com.hybrid.followup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private val groupAdapter = GroupAdapter<ViewHolder>()
    private lateinit var groupLayoutManager: GridLayoutManager
    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)


        id = intent.extras?.getString("Project_id")?:""


        // Todo: remove dummy comments
        //fetchComments()
        setDummyComments()

        groupLayoutManager = GridLayoutManager(this, groupAdapter.spanCount).apply {
            spanSizeLookup = groupAdapter.spanSizeLookup
        }

        comments.apply {
            layoutManager = groupLayoutManager
            adapter = groupAdapter
        }
    }

    private fun fetchComments() {
        FirebaseDatabase.getInstance().getReference("comments").child(id)
                .addChildEventListener(object : ChildAddedListener{
                    override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                        snapshot.children.forEach {
                            it.getValue(CommentMain::class.java)?.apply {
                                val comment = Comment(this, snapshot.child("children"))
                                showComment(comment)
                            }
                        }

                    }

                })
    }

    private fun setDummyComments() {

        for (i in 0..5){
            val comment = Comment(
                    CommentMain("This is the comment body", "Tristan kluivert", 2, "${i*1000}"), null)
            showComment(comment)
        }
    }

    private fun showComment(comment: Comment) {
        groupAdapter.add(ExpandableCommentGroup(comment))
    }

    private fun addNewComment() {
        // Todo: Implement adding of new comments
    }

}
