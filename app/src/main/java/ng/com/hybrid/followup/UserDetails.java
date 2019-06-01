package ng.com.hybrid.followup;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import ng.com.hybrid.followup.Model.UserModel;


public class UserDetails extends AppCompatActivity {


   String[] states = {"Delta", "Rivers", "Lagos","Akwa ibom"};
   String[] gender = {"Male","Female"};


    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    String currentUserID;
    final static int Gallery_Pick = 1;
    private EditText displayname, firstname,lastname, datedialog;
    private Spinner spinnerstate, spingender;
    private CardView btnsave;
    private TextView tvspin, tvgender;
    private CircleImageView userpic;
    DatabaseReference UsersRef;
    private String image;
    private boolean isTaken=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
       UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        userpic = findViewById(R.id.userpic);
        tvspin = findViewById(R.id.tvspin);
        tvgender = findViewById(R.id.tvgender);
        spingender = findViewById(R.id.spingender);
        displayname = findViewById(R.id.displayname);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        spinnerstate = findViewById(R.id.spinnerstate);



         ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,states);
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinnerstate.setAdapter(adapter);


        spinnerstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                tvgender.setText(parent.getItemAtPosition(position).toString());


                if(position==0){
                    tvgender.setText("Male");
                }

                if(position==1){
                    tvgender.setText("Female");
                }

                btnsave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        btnsave = findViewById(R.id.btnsave);
        datedialog = findViewById(R.id.datedialog);


        datedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

               final DatePickerDialog datepicker = new DatePickerDialog(UserDetails.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                datedialog.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datepicker.show();

            }
        });







        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        userpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallery_Pick);
            }
        });


      UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.avatar).into(userpic);
                    } else {
                        Toasty.warning(getApplicationContext(), "Please select profile image first.", Toast.LENGTH_SHORT,true).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile image...");
                loadingBar.show();
                loadingBar.setCancelable(false);
                Uri resultUri = result.getUri();

                final StorageReference ref = UserProfileImageRef.child(currentUserID + ".jpg");
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
                            loadingBar.dismiss();
                            Uri downloadUri = task.getResult();
                            Picasso.get().load(downloadUri.toString()).into(userpic);
                            image = downloadUri.toString();
                            Toasty.success(getApplicationContext(),"Uploaded",Toast.LENGTH_SHORT,true).show();

                        } else {
                            // Handle failures
                            // ...
                            loadingBar.dismiss();
                            String message = task.getException().getMessage();
                            Toasty.error(getApplicationContext(),"Error occured: "+message,Toast.LENGTH_SHORT,true).show();
                        }
                    }
                });
                //
            } else {
                Toasty.error(getApplicationContext(), "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT,true).show();
                loadingBar.dismiss();
            }
        }
    }


    private void SaveAccountSetupInformation() {
        String username = displayname.getText().toString();
        String myfirstname = firstname.getText().toString();
        String mylastname = lastname.getText().toString();
        String userbod = datedialog.getText().toString();
        String userstate = tvspin.getText().toString();
        String mygender = tvgender.getText().toString();
        String userId = mAuth.getUid();
        if (TextUtils.isEmpty(username)) {
            Toasty.warning(getApplicationContext(), "Enter your username please", Toast.LENGTH_SHORT, true).show();

        }else if(userpic.getDrawable() == null){

            Toasty.warning(getApplicationContext(), "Enter your image please", Toast.LENGTH_SHORT, true).show();

        }else if (TextUtils.isEmpty(myfirstname)) {

            Toasty.warning(getApplicationContext(), "Enter  first name please", Toast.LENGTH_SHORT, true).show();

        }else if (TextUtils.isEmpty(mylastname)) {

            Toasty.warning(getApplicationContext(), "Enter your last name please", Toast.LENGTH_SHORT, true).show();

        }else if(userbod.isEmpty()) {
            Toasty.warning(getApplicationContext(), "Date Of Birth please", Toast.LENGTH_SHORT, true);
        }else if(userstate.isEmpty()) {
            Toasty.warning(getApplicationContext(), "Your state please", Toast.LENGTH_SHORT, true);
        }else if(mygender.isEmpty()){
            Toasty.warning(getApplicationContext(), "Your gender please", Toast.LENGTH_SHORT, true);
        } else {

            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.setCancelable(false);
            loadingBar.show();
            UserModel user = new UserModel();
            user.setDisplayname(username);
            user.setDob(userbod);
            user.setFirstname(myfirstname);
            user.setLastname(mylastname);
            user.setUsergender(mygender);
            user.setUserid(currentUserID);
            user.setUserstate(userstate);
            user.setProfileimage(image);

            UsersRef.setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loadingBar.dismiss();
                                Toasty.success(getApplicationContext(),"success",Toast.LENGTH_SHORT,true).show();
                                SendUserToMainActivity();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT,true).show();
                }
            });

        }
    }





    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }




}
