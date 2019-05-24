package ng.com.hybrid.followup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity
{
    Animation fromtop, frombottom, fromleft;
    RelativeLayout authback;
    CardView logincard;
    TextViewImmacBytes text1,text2;
    FloatingActionButton loginfab;
    EditText email,password;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromleft = AnimationUtils.loadAnimation(this,R.anim.fromleft);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        mainVidew();

        loginfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, SignUp.class));
                Animatoo.animateSlideRight(Login.this);
            }
        });

        logincard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loginUser();
            }
        });

    }


    private void sendUserIn() {
        Intent wel = new Intent(getApplicationContext(),MainActivity.class);
        wel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(wel);
        //overridePendingTransition(R.anim.fromright, R.anim.fromleft);
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
        logincard = findViewById(R.id.logincard);
        loginfab = findViewById(R.id.loginfab);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);




        authback.startAnimation(fromtop);
        loginfab.startAnimation(frombottom);
        text1.startAnimation(fromleft);
        text2.startAnimation(fromleft);
        logincard.startAnimation(frombottom);

    }

    private void loginUser() {

        String stremail = email.getText().toString();
        String strpass = password.getText().toString();
        int passsize = password.length();


        if(TextUtils.isEmpty(stremail)){
            email.setError("Email please");
            email.requestFocus();
        }else if(passsize <= 5){
            password.setError("Minimum characters of 6");
            password.requestFocus();
        }else if(TextUtils.isEmpty(strpass)) {
            password.setError("Password please");
            password.requestFocus();
        }else {
            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please wait while we register your email");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(stremail,strpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                       sendUserIn();
                        Toasty.success(getApplicationContext(),"Success",Toast.LENGTH_SHORT,true).show();
                        progressDialog.dismiss();
                    }else{
                        String message = task.getException().getMessage();
                        Toasty.error(getApplicationContext(),"error "+message,Toast.LENGTH_SHORT,true).show();
                       progressDialog.dismiss();
                    }

                }
            });


            }
        }






    }


