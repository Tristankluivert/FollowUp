package ng.com.hybrid.followup


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.candidates.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

class HomeFragment : Fragment() {
    private var mPageNo: Int? = null
    private lateinit var mAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageNo = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.projects.apply {
            layoutManager = LinearLayoutManager(context)
            mAdapter = ProjectAdapter(context)
            adapter = mAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getProjects()
    }

    private fun getProjects() {

        // Todo: Get user state of origin from a persistent User class
        val state = ""

        val projectRef = FirebaseDatabase.getInstance().getReference("projects")
                .orderByChild("state").equalTo(state)

        projectRef.addChildEventListener(object : ChildAddedListener{
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                snapshot.getValue(Project::class.java)?.also {
                    mAdapter.projects.add(it)
                    mAdapter.notifyDataSetChanged()
                }
            }

        })
    }

    class ProjectAdapter(private val context: Context) : RecyclerView.Adapter<ProjectHolder>() {

        var projects = ArrayList<Project>()

        override fun onBindViewHolder(holder: ProjectHolder, position: Int) {

            // Todo: Remove place holder project and uncomment below line
            //holder.bind(projects[position])

            holder.bind(Project().apply {
                name="Tristan Kluivert"; this.position="President of the Federal Republic of Nigeria"
                title="Rehabilitation of East/West Road"; description="Construction work is ongoing for the rehabilitation of East/West Road"
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {

            return ProjectHolder(LayoutInflater.from(context).inflate(R.layout.candidates, parent, false))
        }

        override fun getItemCount(): Int {

            // Todo: Uncomment below line and remove arbitrary value
            //return projects.size

            return 8
        }

    }

    class ProjectHolder(private val item: View) : RecyclerView.ViewHolder(item) {

        fun bind(project: Project) {
            item.name.text = project.name
            item.position.text = project.position
            item.title.text = project.title
            item.description.text = project.description

            item.comment.setOnClickListener {
                Intent(item.context, CommentsActivity::class.java).apply {
                    putExtra("project_id", project.id)
                    item.context.startActivity(this)
                }
            }

            // Todo: Edit like button to show current like state. check if current project id is in users like list
            // Todo: replace ImageView with a ToggleButton

            item.like.setOnClickListener {
                //Todo get current like state
                val isLiked = false
                FirebaseAuth.getInstance().currentUser?.uid?.also { id ->

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(id).child("likes").child(project.id).apply {
                                if (!isLiked) setValue(true)
                                else removeValue()
                            }
                }
            }

            // Todo: Uncomment below lines to load required images

            //Picasso.get().load(project.profilePic).into(item.profilePic)
            //Picasso.get().load(project.partyLogo).into(item.party)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(pageNo: Int) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, pageNo)
                    }
                }
    }
}
