package com.example.lenovo.moviereviewapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {
     RatingBar ratingBar;
     EditText editText;
     TextView textView;
     Button b;
     public static final String serverUrl="https://entertainment-95db0.firebaseio.com/";
     DatabaseReference databaseReference;
     public static  final String path="user_feedback";
     Firebase firebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ratingBar=findViewById(R.id.ratingbar);
        editText=findViewById(R.id.feededit);
        textView=findViewById(R.id.ratintv);
       /* b=findViewById(R.id.button2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               textView.setText("Your rating is"+ratingBar.getRating());
            }
        });
    */    Firebase.setAndroidContext(this);
       firebase=new Firebase(serverUrl);
       databaseReference=FirebaseDatabase.getInstance().getReference().child(path);


    }

    public void submitFeedback(View view) {
        textView.setText("Your rating is"+ratingBar.getRating());
        String feedback=editText.getText().toString();
        String record=databaseReference.push().getKey();
       //float rating= Float.parseFloat(textView.getText().toString());
        databaseReference.child(record).setValue(feedback);
       // databaseReference.child(record).setValue(rating);
        Toast.makeText(getApplicationContext(),"Thank you for your valuable feedback",Toast.LENGTH_LONG).show();
    }
}
