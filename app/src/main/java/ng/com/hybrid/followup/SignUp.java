package ng.com.hybrid.followup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {


    Animation fromtop, frombottom, fromleft, fromright;
    RelativeLayout authback;
    CardView signupcard;
    TextViewImmacBytes text1,text2;
    FloatingActionButton signupfab;
    EditText email,password,password2;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    RelativeLayout signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromleft = AnimationUtils.loadAnimation(this,R.anim.fromleft);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        fromright = AnimationUtils.loadAnimation(this,R.anim.fromright);
        mainVidew();




       signupfab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

              startActivity(new Intent(SignUp.this, Login.class));
               Animatoo.animateSlideLeft(SignUp.this);
           }
       });

        signupcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });


    }

    private void sendUserIn() {
        Intent wel = new Intent(getApplicationContext(),MainActivity.class);
        wel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(wel);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser != null){
            sendUserIn();
        }
    }

    public void mainVidew(){

        authback = findViewById(R.id.authback);
        signupcard = findViewById(R.id.signupcard);
        signupfab = findViewById(R.id.signupfab);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);



        authback.startAnimation(fromtop);
        signupfab.startAnimation(frombottom);
        text1.startAnimation(fromleft);
        text2.startAnimation(fromleft);
        signupcard.startAnimation(frombottom);

    }

    public void createUser(){

        String stremail = email.getText().toString().trim();
        String strpass = password.getText().toString().trim();
        String strpass2 = password2.getText().toString().trim();
        int passsize = password.length();
        int passsize1 = password2.length();

        if(TextUtils.isEmpty(stremail)){
            email.setError("Email please");
            email.requestFocus();
        }else if(passsize <= 5){
           password.setError("Minimum characters of 6");
           password.requestFocus();
        }else if(TextUtils.isEmpty(strpass)) {
            password.setError("Password please");
            password.requestFocus();
        }else if(passsize1 <= 5){
            password2.setError("Minimum characters of 6");
           password2.requestFocus();
        }else if(TextUtils.isEmpty(strpass2)){
           password2.setError("Confirm password please");
           password2.requestFocus();
        }else if(!strpass2.equals(strpass2)){
           password2.setError("Password does not match");
           password2.requestFocus();
        }else{

            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please wait while we register your email");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(stremail,strpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toasty.success(getApplicationContext(),"Success",Toast.LENGTH_SHORT,true).show();
                        sendUserToDetails();

                    }else{
                        progressDialog.dismiss();
                        String message = task.getException().getMessage();
                        Toasty.error(getApplicationContext(),"Error occured: "+message,Toast.LENGTH_SHORT,true).show();

                    }
                }
            });

        }

        }




    private void sendUserToDetails() {

        Intent usersetup = new Intent(getApplicationContext(),UserDetails.class);
        usersetup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(usersetup);
        finish();

    }
}
