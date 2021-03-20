package com.example.imho_socialv101;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private EditText Username, FullName, CountryName;
    private Button SaveInformationButton;
    private CircleImageView ProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBaar;

    String currentUserID;

    final static int Gallery_pick =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);



mAuth = FirebaseAuth.getInstance();
currentUserID = mAuth.getCurrentUser().getUid();
UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile images");



        Username = (EditText) findViewById(R.id.setup_user_name);
        FullName = (EditText) findViewById(R.id.setup_fullname);
        CountryName = (EditText) findViewById(R.id.setup_country);
        SaveInformationButton = (Button) findViewById(R.id.setup_information_button);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        loadingBaar = new ProgressDialog(this);


        SaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation();
            }
        });


        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick);
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    }
                    else
                    {
                        Toast.makeText(SetUpActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data != null){
            Uri ImageUri = data.getData();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){



                loadingBaar.setTitle("Saving account information");
                loadingBaar.setMessage("Please Wait, while we are smoking (saving your picture)");
                loadingBaar.show();
                loadingBaar.setCanceledOnTouchOutside(true);



                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                     if(task.isSuccessful()){
                         Toast.makeText(SetUpActivity.this, "Profile Image stored successfull...", Toast.LENGTH_LONG).show();

                         final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                         UsersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){

                                     Intent selfIntent = new Intent(SetUpActivity.this, SetUpActivity.class);
                                     startActivity(selfIntent);

                                     Toast.makeText(SetUpActivity.this, "Now we have your profile image, thanks...", Toast.LENGTH_LONG).show();

                                                        loadingBaar.dismiss();
                                 }
                                 else {
                                     String message = task.getException().getMessage();
                                     Toast.makeText(SetUpActivity.this, "Error ???..." + message, Toast.LENGTH_LONG).show();
                                     loadingBaar.dismiss();
                                 }
                             }
                         });

                     }
                    }
                });


            }
        }else {
            Toast.makeText(this, "Error ((((Try again...", Toast.LENGTH_LONG).show();
            loadingBaar.dismiss();
        }
    }

    private void SaveAccountSetupInformation() {
        String username = Username.getText().toString();
        String fullname = FullName.getText().toString();
        String country = CountryName.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(country)){
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_LONG).show();
        }
        else {

            loadingBaar.setTitle("Saving account information");
            loadingBaar.setMessage("Please Wait, while we are sleeping (saving your information)");
            loadingBaar.show();
            loadingBaar.setCanceledOnTouchOutside(true);


            HashMap userMap = new HashMap();
                userMap.put("user name", username);
            userMap.put("fullname", fullname);
            userMap.put("country",country);
            userMap.put("status", "hey there, i am using Imho Social network, developed by ukrainians ");
            userMap.put("gender", "pokemon");
            //userMap.put("game platform", "PS4 and Pk");
            userMap.put("dob", "none");
            //userMap.put("favorite game", "Star Wars the Fallen Order");
            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                 if(task.isSuccessful()){
                     SendUserToMainActivity();
                     Toast.makeText(SetUpActivity.this, "your Account is created...", Toast.LENGTH_LONG).show();
                     loadingBaar.dismiss();
                 }
                 else {
                     String message = task.getException().getMessage();
                     Toast.makeText(SetUpActivity.this, "UUUPS, Error..." + message, Toast.LENGTH_LONG).show();
                     loadingBaar.dismiss();
                 }
                }
            });

        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetUpActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
