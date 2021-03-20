package com.example.imho_socialv101;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private EditText userName, userProfileName, userstatus, usercountry, userGender, userDOB;
    private Button UpdateAccountSettingsButton;

private DatabaseReference SettingsuserRef;
private FirebaseAuth mAth;
private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        currentUserId = mAth.getCurrentUser().getUid();
        mAth = FirebaseAuth.getInstance();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        userName = (EditText) findViewById(R.id.settings_user_name);
        userProfileName = (EditText) findViewById(R.id.settings_user_fullname);
        userstatus = (EditText) findViewById(R.id.settings_status);
        usercountry = (EditText) findViewById(R.id.settings_user_country);
        userGender = (EditText) findViewById(R.id.settings_gender);

        userDOB = (EditText) findViewById(R.id.settings_dob);

        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myuserName = dataSnapshot.child("user name").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String mycountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();


                    userName.setText(myuserName);
                    userProfileName.setText(myProfileName);
                    userstatus.setText(myProfileStatus);
                    userDOB.setText(myDOB);
                    usercountry.setText(mycountry);
                    userGender.setText(myGender);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        UpdateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings);

    }
}
