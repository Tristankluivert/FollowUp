package ng.com.hybrid.followup;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

public class IntroScreen extends AppCompatActivity {

    Button btnget;
    ImageView bgone;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        bgone = findViewById(R.id.bgone);
                btnget = findViewById(R.id.cont);

                bgone.animate().scaleX(2).scaleY(2).setDuration(5000).start();

                btnget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent a = new Intent(getApplicationContext(), SlideActivity.class);
                        overridePendingTransition(R.anim.fromright, R.anim.fromleft);
                        startActivity(a);
                    }
                });

            }

            private void launchHomeScreen() {
                prefManager.setFirstTimeLaunch(false);
                startActivity(new Intent(getApplicationContext(), SlideActivity.class));
                finish();

            }
    }

