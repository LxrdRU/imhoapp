package com.example.imho_socialv101;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText gameName;
    private EditText gameDescription;
    private  static final int Gallery_pic = 1;
    private Uri ImageUri;
private  String GameNameDescrption;
private  String GameDataDescriptiom;
private String saveCurrentDate, saveCurrentTime, postRandomName;

private StorageReference PostImagesRefernce;

private  String dowloadUrl, current_user_id;


private DatabaseReference UsersRef, PostRef;

private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        PostImagesRefernce = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();



        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        gameDescription = (EditText) findViewById(R.id.game_text_about);
        gameName = (EditText) findViewById(R.id.game_name_description);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");



        mToolbar = (Toolbar) findViewById(R.id.update_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");



        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInfo();
            }
        });



    }

    private void ValidatePostInfo() {

        GameNameDescrption = gameName.getText().toString();
         GameDataDescriptiom = gameDescription.getText().toString();


        if (TextUtils.isEmpty(GameNameDescrption)){
            Toast.makeText(PostActivity.this, "Please write Game Name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(GameDataDescriptiom)){
            Toast.makeText(PostActivity.this, "Please write Game Description", Toast.LENGTH_SHORT).show();
        }else{
            StoringImageToStorage();
        }

    }




    private void StoringImageToStorage() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMMM-yyyy");
        saveCurrentDate =currentDate.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime =currentTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostImagesRefernce.child("postimage").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    dowloadUrl = task.getResult().getStorage().getDownloadUrl().toString();

                    Toast.makeText(PostActivity.this, "Game uploaded", Toast.LENGTH_SHORT).show();

                    SavingPostInformationToDataBase();

                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void SavingPostInformationToDataBase() {

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userfullName = dataSnapshot.child("fullname").getValue().toString();
                   // String useProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    //смотреть описание на русском ниже

                    HashMap postMap = new HashMap();
                    postMap.put("uid", current_user_id);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("gamename", gameName);
                    postMap.put("gamedes", gameDescription);
                    postMap.put("postimage", dowloadUrl);
                   // postMap.put("profileimage", useProfileImage);
                    //все ломается если у юзера нета авочки авочка не отображается
                    postMap.put("fullname",userfullName);

                    PostRef.child(current_user_id + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){
                                SenUserToMaintaCtivity();
                                Toast.makeText(PostActivity.this, "Post updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_pic );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==Gallery_pic && resultCode==RESULT_OK && data!=null){
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            SenUserToMaintaCtivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SenUserToMaintaCtivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
