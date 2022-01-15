package com.example.p6grnjordsapp.Activities.ui.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.p6grnjordsapp.Activities.LoginActivity;
import com.example.p6grnjordsapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class NotificationsFragment extends Fragment {

private NotificationsViewModel notificationsViewModel;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    //private Uri pickedImgUri;
    private Button logout;
//
public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
    notificationsViewModel =
            new ViewModelProvider(this).get(NotificationsViewModel.class);
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    TextView navUsername = view.findViewById(R.id.nav_username);
    ImageView navUserPhot = view.findViewById(R.id.nav_user_photo);
    TextView navUserMail = view.findViewById(R.id.nav_user_mail);
    //not working
    //Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);
   // if (pickedImgUri != null) {
        //  currentUser.getPhotoUrl().toString();
        //navUserPhot.setImageURI(pickedImgUri);
        //currentUser.getPhotoUrl().toString();

//    }
//        else{
//        pickedImgUri=null;
//    }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        logout = view.findViewById(R.id.nav_logout);


        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {


            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText();
                navUserMail.setText(currentUser.getEmail());
                navUsername.setText(currentUser.getDisplayName());


            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
//    }
//    public void updateProfile (View V){
//        TextView t = findViewById(R.id.nav_username);



//    public void updateNavProfile (){
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        View profileView = navView.getRootView();


}}