package ng.com.hybrid.followup


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_likes.view.*


private const val ARG_PARAM1 = "param1"

class LikesFragment : Fragment() {

    private var mPageNo: Int? = null
    private lateinit var mAdapter: HomeFragment.ProjectAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageNo = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_likes, container, false)

        view.liked_projects.apply {
            layoutManager = LinearLayoutManager(context)
            mAdapter = HomeFragment.ProjectAdapter(context)
            adapter = mAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLikes()
    }

    private fun getLikes() {

        FirebaseAuth.getInstance().currentUser?.uid?.also {id ->
            val userLikesRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(id).child("likes")
            userLikesRef.addChildEventListener(object : ChildAddedListener{

                override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                    snapshot.key?.also {key ->

                        FirebaseDatabase.getInstance().getReference("projects")
                                .child(key).addListenerForSingleValueEvent(object : ValueChangeListener{

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            snapshot.getValue(Project::class.java)?.also {
                                                mAdapter.projects.add(it)
                                                mAdapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                })
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    snapshot.key?.also { key ->

                        mAdapter.projects = ArrayList<Project>().apply {
                            addAll(mAdapter.projects.filter { p -> p.id != key }.map { it })
                        }

                        mAdapter.notifyDataSetChanged()
                    }
                }
            })

        }
    }

    companion object {

        @JvmStatic
        fun newInstance(pageNo: Int) =
                LikesFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, pageNo)
                    }
                }
    }


}
