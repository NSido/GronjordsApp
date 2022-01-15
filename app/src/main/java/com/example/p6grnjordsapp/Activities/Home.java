package com.example.p6grnjordsapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p6grnjordsapp.Models.Post;
import com.example.p6grnjordsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class Home extends AppCompatActivity {

    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 2 ;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;
    ImageView popupUserImage, popupPostImage, popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;
    private Uri chosenImgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_contact, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // method call for posting popup
        iniPopup();

        setupPopupImageClick();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });
    }
        private void setupPopupImageClick () {
//
//
            popupPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 //Here, the when image is clicked, gallery is opened
                 //if granted permission to choose an image for post
                    checkAndRequestForPermission();
//
//
                }
            });


        }
//This is the same method as the one in registerAct
        private void checkAndRequestForPermission () {

            if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Toast.makeText(Home.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

                } else {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PReqCode);
                }

            } else
                openGallery();

        }

       private void openGallery () {
            //after gallery is open, user picks image
//            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            galleryIntent.setType("image/*");
//            startActivityForResult(galleryIntent, REQUESCODE);
              mGetContent.launch("image/*");

       }
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        chosenImgUri = result;
                        popupPostImage.setImageURI(chosenImgUri);


                    }
                }
            });


        private void iniPopup () {
        //display popup window
            popAddPost = new Dialog(this);
            popAddPost.setContentView(R.layout.popup_add_post);
            popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popAddPost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

            //Display popup details
            popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
            popupPostImage = popAddPost.findViewById(R.id.popup_img);
            popupTitle = popAddPost.findViewById(R.id.popup_title);
            popupDescription = popAddPost.findViewById(R.id.popup_description);
            popupAddBtn = popAddPost.findViewById(R.id.popup_add);
            popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

            //Display the current profile photo-Not working
//           Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserImage);


            //click listener for adding posts

            popupAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupAddBtn.setVisibility(View.INVISIBLE);
                    popupClickProgress.setVisibility(View.VISIBLE);

                    //Make sure all fields are filled, and image.
                    if (!popupTitle.getText().toString().isEmpty()
                            && !popupDescription.getText().toString().isEmpty()
                            && chosenImgUri != null) {

                        //if all is filled- upload to firebase.
                        //first, get to Firebase
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                        final StorageReference imageFilePath = storageReference.child(chosenImgUri.getLastPathSegment());
                      //  if (imageFilePath != null){
                        imageFilePath.putFile(chosenImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                    @Override

                                    public void onSuccess(Uri uri) {
                                       if (currentUser.getPhotoUrl() == null) {
                                     //   String imageDownlaodLink = uri.toString();
                                        Uri imageDownlaodLink = uri;
                             //         String imageDownlaodLink = uri !=null ? uri.toString(): null;
                                       // post Obj to be created
                                        Post post = new Post(popupTitle.getText().toString(),
                                                popupDescription.getText().toString(),
                                                imageDownlaodLink.toString(),
                                                currentUser.getUid(),
                                                currentUser.getPhotoUrl().toString());

                                        // Now upload post to Firebase

                                        addPost(post);
                                    }}
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // show message if error

                                        showMessage(e.getMessage());
                                        popupClickProgress.setVisibility(View.INVISIBLE);
                                        popupAddBtn.setVisibility(View.VISIBLE);


                                   }


                        });

                    }
                        });
                        }
                    else {
                        showMessage("Check that all fields are filled and an image is chosen, please!");
                        popupAddBtn.setVisibility(View.VISIBLE);
                        popupClickProgress.setVisibility(View.INVISIBLE);

                    }

                    }

            });
        }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID & upadate post key
        String key = myRef.getKey();
        post.setPostKey(key);

        // add post data to firebase database

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Your post is uploaded");
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });
    }

    private void showMessage(String message) {

        Toast.makeText(Home.this, message, Toast.LENGTH_LONG).show();

   }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @SuppressWarnings("StatementWithEmptyBody")
}
