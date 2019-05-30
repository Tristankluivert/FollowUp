package ng.com.hybrid.followup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import live.utkarshdev.maincontentslidingnavview.ContentSlidingDrawerLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout mTabLayout;
    private TextViewImmacBytes navname;
    private CircleImageView navpic;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    String currentUserID;

    private int[] mTabsIcons = {
            R.drawable.message,
            R.drawable.chat,
            R.drawable.notification};


    ContentSlidingDrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View  headerView = navigationView.getHeaderView(0);
        navpic = headerView.findViewById(R.id.navpic);
        navname = headerView.findViewById(R.id.navname);

        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("username")) {
                        String fullname = dataSnapshot.child("username").getValue().toString();
                        navname.setText(fullname);
                    }
                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).into(navpic);
                    } else {
                        navpic.setImageResource(R.drawable.avatar);
                       /* Intent intent = new Intent(getApplicationContext(),PickPic.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();*/
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        drawer = findViewById(R.id.drawer_layout);
        drawer.init(this,navigationView,getSupportActionBar());
        //or use drawer.init(this,navigationView,getSupportActionBar());
        //if action bar syncstate animation is not needed

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        MainActivity.MyPagerAdapter pagerAdapter = new MainActivity.MyPagerAdapter(getSupportFragmentManager());
        if (viewPager != null)
            viewPager.setAdapter(pagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(pagerAdapter.getTabView(i));
            }

            mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser  = mAuth.getCurrentUser();

        if(currentuser == null){
            SendUserToLogin();
        }else{
            CheckUserData();
        }



    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentuser  = mAuth.getCurrentUser();

        if(currentuser == null){
            SendUserToLogin();
        }else{
            CheckUserData();
        }
    }

    private void CheckUserData() {

        final String current_user_id = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(current_user_id)){
                    sendUserToDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
                     startActivity(new Intent(MainActivity.this,Profile.class));
                     finish();
        } else if (id == R.id.nav_rate) {

        } else if (id == R.id.nav_share) {

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT,"Check out yearonesuite");
            String applink = "";
            share.putExtra(Intent.EXTRA_TEXT,"Download the app: " + applink);
            startActivity(Intent.createChooser(share,"Share Via"));


        } else if (id == R.id.nav_logout) {

            mAuth.signOut();
           SendUserToLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 3;

        private final String[] mTabsTitle = {"Candidates", "Likes", "Notifications"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.toolbar, null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(mTabsIcons[position]);
            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return HomeFragment.newInstance(1);

                case 1:
                    return LikesFragment.newInstance(2);
                case 2:
                    return SecondFragment.newInstance(3);
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }


    private void sendUserToDetails() {

        Intent userintent = new Intent(getApplicationContext(),UserDetails.class);
        userintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(userintent);
        finish();

    }

    private void SendUserToLogin() {

        Intent loginintent = new Intent(getApplicationContext(),Login.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }
}
