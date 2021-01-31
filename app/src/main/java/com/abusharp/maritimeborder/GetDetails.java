package com.abusharp.maritimeborder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GetDetails extends AppCompatActivity {
EditText boat,phone;
String boatno,mobileno,email,name;
DatabaseReference databaseReference;
FirebaseUser User;
String checkLogin;
FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);

        boat = findViewById(R.id.boatno);
        phone = findViewById(R.id.phone);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        User =(FirebaseUser) getIntent().getParcelableExtra("uid");
        checkLogin = getIntent().getStringExtra("log");
        if(checkLogin.equals("loggedin"))
            startActivity(new Intent(GetDetails.this,MapsActivity.class).putExtra("user",User));


        ((Button) findViewById(R.id.continu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boatno = boat.getText().toString();
                mobileno = phone.getText().toString();
                name = User.getDisplayName();
                email = User.getEmail().substring(0,User.getEmail().indexOf('@'));
                final Map<String,Object> update = new HashMap<>();
                update.put("userDetails" , Arrays.asList(email,name,boatno,mobileno));
                try {
                    databaseReference.child("User").child(User.getUid()).setValue(update);
                }
                catch (Exception e){
                     Toast.makeText(getApplicationContext(),"Error"+e, Toast.LENGTH_SHORT).show();
                    Log.e("Error" , ""+e);
                }
                startActivity(new Intent(GetDetails.this,MapsActivity.class).putExtra("user",User));
            }
        });


    }
}
