package com.example.imho_socialv101;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageButton AddnewPostButton;

    //autefication
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, PostRef;


    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        //login
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        //**********
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        //toolbar section

        mToolbar =(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Imho Social App");


        AddnewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);
        //************************************
        //action bar layout
        drawerLayout =(DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
       drawerLayout.addDrawerListener(actionBarDrawerToggle);
       actionBarDrawerToggle.syncState();
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //**********************************************************


        navigationView = (NavigationView) findViewById(R.id.navigation_veiw);



        //Post LIST


        postList = (RecyclerView)findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(this);
        linerLayoutManager.setReverseLayout(true);
        linerLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linerLayoutManager);




        //**********



        //header section
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);



        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }


                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //add new ost activity
        AddnewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            SendUseToPostActivity();
            }
        });
        //*************************************************

//*****************************************************************************


        //conecting nawigation menu to project
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        //**********************************************************
    }

    private void SendUseToPostActivity() {

        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);


    }


    //login methid


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            SendUserToMLoginActivity();
        }
        else {
            CheckUserExistence();
        }

    }

    private void CheckUserExistence() {
        final String currentUserID =  mAuth.getCurrentUser().getUid();

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentUserID)){
                    SenUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        DisplayAllUserPosts();




    }

    private void DisplayAllUserPosts() {
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                (
                        Posts.class, R.layout.all_post_layout, PostsViewHolder.class, PostRef
                )
        {
            @Override
            protected void populateViewHolder(PostsViewHolder postsViewHolder, Posts posts, int i) {

                postsViewHolder.setFullname(posts.getFullname());
                postsViewHolder.setTime(posts.getDate());
                postsViewHolder.setDate(posts.getDate());
                postsViewHolder.setGamename(posts.getGamename());
                postsViewHolder.setPostimage(getApplicationContext(), posts.getPostimage());


            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public  void setFullname(String fullname){
            TextView username = (TextView)mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setTime(String time){
            TextView PostTime = (TextView)mView.findViewById(R.id.post_time);
            PostTime.setText("   " + time);
        }

        public void setDate(String date){
            TextView PostDate = (TextView)mView.findViewById(R.id.postdate);
            PostDate.setText("   " + date);
        }


        public void setGamename(String gamename){
            TextView PostGameName = (TextView)mView.findViewById(R.id.post_description);
            PostGameName.setText(gamename);
        }

        public void setPostimage(Context ctx, String postimage){
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postimage).placeholder(R.drawable.profile).into(PostImage);
        }

    }

    private void SenUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToMLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
//*************************************************************************

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //if user will click open Menu
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //User menu selector class (Боковая вываливающаяся панель)
    private void UserMenuSelector(MenuItem item){
        switch (item.getItemId())
        {

            case R.id.nav_log_post:
                SendUseToPostActivity();
                break;


            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;



            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;


            case R.id.nav_friends:
                Toast.makeText(this, "Friend List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friend", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                SendUserToSettingActivity();
                break;

            case R.id.nav_log_out:
                mAuth.signOut();
                SendUserToMLoginActivity();
                break;
        }
    }

    private void SendUserToSettingActivity() {
        Intent loginIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(loginIntent);
    }
}



