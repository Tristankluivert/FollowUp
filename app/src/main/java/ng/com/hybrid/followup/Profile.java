package ng.com.hybrid.followup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private ImageButton bac;
    private MyTextView tvstate,tvusername,tvdate,tvgender,tvrealname;
    private CircleImageView profilepic;
    private Button btedit;
    FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference edRef;
    FirebaseUser firebaseUser;
    private StorageReference UserProfileImageRef;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading Profile");
        progressDialog.setCanceledOnTouchOutside(false);

        if(firebaseUser != null){
            currentUserId = firebaseUser.getUid();
        }

        profilepic = findViewById(R.id.profilepic);
        bac = findViewById(R.id.bac);
        tvdate = findViewById(R.id.tvdate);
        tvgender = findViewById(R.id.tvgender);
        tvrealname = findViewById(R.id.tvrealname);
        tvstate = findViewById(R.id.tvstate);
        tvusername = findViewById(R.id.tvusername);
        btedit = findViewById(R.id.btedit);


        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bc = new Intent(getApplicationContext(),MainActivity.class);
                bc.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(bc);
                Animatoo.animateSlideLeft(Profile.this);
                finish();

            }
        });

        btedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent ed = new Intent(getApplicationContext(),EditProfile.class);
              ed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(ed);
              Animatoo.animateSwipeRight(Profile.this);

            }
        });

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        edRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        edRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    progressDialog.dismiss();
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myusername = dataSnapshot.child("displayname").getValue().toString();
                    String myfirstname = dataSnapshot.child("firstname").getValue().toString();
                    String mylastname = dataSnapshot.child("lastname").getValue().toString();
                    String mystate = dataSnapshot.child("userstate").getValue().toString();
                    String mygender = dataSnapshot.child("usergender").getValue().toString();
                    String mydob = dataSnapshot.child("dob").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.avatar).into(profilepic);
                    tvusername.setText(myusername);
                    tvrealname.setText(myfirstname+" "+mylastname);
                    tvstate.setText(mystate);
                    tvgender.setText(mygender);
                    tvdate.setText(mydob);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent off1 = new Intent(getApplicationContext(),MainActivity.class);
        off1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(off1);
        finish();

    }
}
