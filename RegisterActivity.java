package com.example.lenovo.moviereviewapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {


    EditText emailid,password;
    String email,pswd;
    FirebaseAuth auth;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailid=findViewById(R.id.editEmail);
        password=findViewById(R.id.confirmpswd);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);


    }

    public void showProgress() {
        dialog.setTitle("Authenticating the user");
        dialog.setMessage("Loading please wait");
        dialog.setCancelable(false);
        dialog.show();
    }
    public void register(View view) {
        email = emailid.getText().toString();
        pswd = password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(pswd)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();
        auth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Error in registration", Toast.LENGTH_SHORT).show();
                    password.setText(null);
                    emailid.setText(null);

                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Intent intent = new Intent(RegisterActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

}
