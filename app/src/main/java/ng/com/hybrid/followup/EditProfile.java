package ng.com.hybrid.followup;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class EditProfile extends AppCompatActivity {

    EditText newname;
    CardView cdname,btnsave;
    private Spinner spingender, spinstate;
    private  EditText edate;
    Animation animation;
    ImageView gobak;
    FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference edRef;
    CircleImageView newImage;
    ProgressDialog progressDialog;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    FirebaseUser firebaseUser;
    TextView tvspin, tvgenda;
    String[] states = {"Delta", "Rivers", "Lagos","Akwa ibom"};
    String[] gender = {"Male","Female"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);


  //Checking current user
     if(firebaseUser != null){
      currentUserId = firebaseUser.getUid();
     }

        tvspin = findViewById(R.id.tvspinner);
     tvgenda = findViewById(R.id.tvgenda);
         spingender = findViewById(R.id.spinstate);
         spingender = findViewById(R.id.spigender);
         edate = findViewById(R.id.dedialog);
        newname = findViewById(R.id.newname);
        cdname = findViewById(R.id.cdname);
        newImage =  findViewById(R.id.newImage);
       btnsave = findViewById(R.id.btnsave);
       gobak = findViewById(R.id.gobac);
       spinstate = findViewById(R.id.spinstate);

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
     edRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);


        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,states);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinstate.setAdapter(adapter);


        spinstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvspin.setText(parent.getItemAtPosition(position).toString());

                if(position==0){
                    tvspin.setText("Delta");
                }

                if(position==1){
                    tvspin.setText("Rivers");
                }

                if(position==2){
                    tvspin.setText("Lagos");
                }

                if(position==3){
                    tvspin.setText("Akwa ibom");
                }

                btnsave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gender);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spingender.setAdapter(adapter1);

        spingender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvgenda.setText(parent.getItemAtPosition(position).toString());


                if(position==0){
                    tvgenda.setText("Male");
                }

                if(position==1){
                    tvgenda.setText("Female");
                }

                btnsave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

     edate.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             final Calendar cldr = Calendar.getInstance();
             int day = cldr.get(Calendar.DAY_OF_MONTH);
             int month = cldr.get(Calendar.MONTH);
             int year = cldr.get(Calendar.YEAR);

             final DatePickerDialog datepicker = new DatePickerDialog(EditProfile.this,
                     new DatePickerDialog.OnDateSetListener() {

                         @Override
                         public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                             edate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                         }
                     }, year, month, day);
             datepicker.show();

         }
     });

     gobak.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent ui = new Intent(getApplicationContext(),Profile.class);
             ui.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(ui);
             Animatoo.animateSlideLeft(EditProfile.this);
             finish();
         }
     });


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              validateUser();
            }
        });

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallery_Pick);

            }
        });

        //Retrieving user data

        edRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myusername = dataSnapshot.child("username").getValue().toString();
                    String mydob = dataSnapshot.child("DOB").getValue().toString();
                   String mystate = dataSnapshot.child("State").getValue().toString();
                   String mygen = dataSnapshot.child("gender").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.avatar).into(newImage);
                    edate.setText(mydob);
                    tvgenda.setText(mygen);
                    tvspin.setText(mystate);
                    newname.setText(myusername);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == Gallery_Pick){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                progressDialog.setTitle("Profile Image");
                progressDialog.setMessage("Please wait, while we are updating your new profile image...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();
                Uri resultUri = result.getUri();

                final StorageReference ref = UserProfileImageRef.child(currentUserId + ".jpg");
                UploadTask uploadTask = ref.putFile(resultUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
                            String link = downloadUri.toString();
                            edRef.child("profileimage").setValue(link).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent selfIntent = new Intent(getApplicationContext(), EditProfile.class);
                                        startActivity(selfIntent);
                                        Toasty.success(getApplicationContext(),"Profile Image Uploaded",Toast.LENGTH_SHORT,true).show();
                                    }else{
                                        String message = task.getException().getMessage();
                                        Toasty.error(getApplicationContext(),"Error occured: "+message,Toast.LENGTH_SHORT,true).show();
                                    }
                                }
                            });
                        } else {
                            // Handle failures
                            // ...
                            String message = task.getException().getMessage();
                            Toasty.error(getApplicationContext(),"Error occured: "+message,Toast.LENGTH_SHORT,true).show();
                        }
                    }
                });
                //
            } else {
                Toasty.error(getApplicationContext(), "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT,true).show();
                progressDialog.dismiss();
            }
        }
    }



    private void validateUser() {

        String username = newname.getText().toString();
        String strstate = tvspin.getText().toString();
        String strgender = tvgenda.getText().toString();
        String strdate = edate.getText().toString();

        if(TextUtils.isEmpty(username)) {
            Toasty.warning(getApplicationContext(), "Name please", Toast.LENGTH_SHORT, true).show();

        }else if(strdate.isEmpty()){

            Toasty.warning(getApplicationContext(),"Date of birth please",Toast.LENGTH_SHORT,true).show();

        }else if(strgender.isEmpty()) {
            Toasty.warning(getApplicationContext(),"Gender please");
        }else if(strstate.isEmpty()){
            Toasty.warning(getApplicationContext(),"State please", Toast.LENGTH_SHORT,true).show();
        }else {
            progressDialog.setTitle("Saving Information");
            progressDialog.setMessage("Please wait, while we are updating your data...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            updateUser(username,strstate,strgender,strdate);

        }


    }

    public void updateUser(String username, String strstate, String strgender, String strdate){

        HashMap userMap = new HashMap();
        userMap.put("username",username);
        userMap.put("state",strstate);
        userMap.put("gender",strgender);
        userMap.put("DOB",strdate);

        edRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toasty.success(getApplicationContext(),"Details updated",Toast.LENGTH_SHORT,true).show();
                    sendUserToMain();
                }else{
                    progressDialog.dismiss();
                   Toasty.error(getApplicationContext(),"Error occured,Please try again",Toast.LENGTH_SHORT,true).show();
                }

            }
        });


    }

    private void sendUserToMain() {

        Intent send = new Intent(getApplicationContext(),MainActivity.class);
        send.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(send);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent send1 = new Intent(getApplicationContext(),Profile.class);
        send1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(send1);
        Animatoo.animateSlideUp(EditProfile.this);
        finish();

    }
}
