package com.example.p6grnjordsapp.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResult;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.p6grnjordsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class RegisterAct extends AppCompatActivity {


    ImageView imgUserImage;
    static int PReqCode = 1;
    static int REQUESCODE= 1;
    Uri chosenImgUri;
    private FirebaseAuth mAuth;

    private EditText userName, userEmail, userPassword, userPassword2;
    private ProgressBar loadingProgress;
    private Button userBtn;
    private Button toLogBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);
        userName =findViewById(R.id.regName);
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);
        userBtn = findViewById(R.id.regBtn);
        loadingProgress = findViewById(R.id.regProgressBar);
        loadingProgress.setVisibility(View.INVISIBLE);
        toLogBtn = findViewById(R.id.logBtn);


        mAuth = FirebaseAuth.getInstance();
        toLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterAct.this, LoginActivity.class));
            }
        });
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();
                final String name = userName.getText().toString();
//check if all fields are filled/display warning otherwise
                if( email.isEmpty() || name.isEmpty() || password.isEmpty()  || !password.equals(password2)) {

                    showMessage("Recheck all fields, please!") ;
                    userBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
                else {

                }
                    CreateUserAccount(email,name,password);

                }

        });




        imgUserImage = findViewById(R.id.regUserImage);
        imgUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 19){
                    checkAndRequestForPermission();
                }
                else
                {
                    openGallery();
                }

            }
        });
    }

    private void CreateUserAccount(String email, final String name, String password) {
//creating user accounts with firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // display message account created
                            showMessage("Your account is successfully created!");
                            // updating users info after account creation
                            updateUserInfo(name, chosenImgUri, mAuth.getCurrentUser());

                        } else {

                            //display messages account failed
                            showMessage("Something went wrong, account not created!" + task.getException().getMessage());
                            userBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }

    private void updateUserInfo (final String name, Uri chosenImgUri, FirebaseUser currentUser) {
        //uploading user image to Firebase database
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(chosenImgUri.getLastPathSegment());
        imageFilePath.putFile(chosenImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //get Url which has uri
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // display message registration completed
                                            showMessage("your registration is completed");
                                            updateUI();
                                        }

                                    }
                                });

                    }
                });


            }
        });
    }


        private void updateUI() {

            Intent homeActivity = new Intent(getApplicationContext(),Home.class);
            startActivity(homeActivity);
            finish();


        }

        //Display message to user
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    //opens phone gallery for user to pick an image
    private void openGallery() {

       //image selected from gallery
        mGetContent.launch("image/*");

        }




    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterAct.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale( RegisterAct.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterAct.this, "Allow app to access my gallery", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(RegisterAct.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                   PReqCode);
            }

        }
        else
            openGallery();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
//            //User picked a profile photo
//            //photo reference must be saved to Uniform Resource Identifier
//            chosenImgUri = data.getData();
//            imgUserImage.setImageURI(chosenImgUri);
ActivityResultLauncher<String> mGetContent = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    imgUserImage.setImageURI(result);
                    chosenImgUri = result;

                }
            }
        });

}