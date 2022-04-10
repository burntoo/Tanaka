package com.martin.tanaka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.martin.tanaka.DBHelper.DBHelper;
import com.martin.tanaka.databinding.ActivityIqTestBinding;
import com.martin.tanaka.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;

    DBHelper dbHelper;

    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBar.toolTitle.setText("Login");
        binding.appBar.img.setVisibility(View.GONE);

        dbHelper = new DBHelper(this);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.ed_phone, ".{9,}", R.string.invalid_phone);
        awesomeValidation.addValidation(this, R.id.ed_password, ".{6,}", R.string.invalid_password);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                Login.this.finish();
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login()) {
                    Intent intent = new Intent(Login.this, Dashboard.class);
                    startActivity(intent);
                    Login.this.finish();
                }
            }
        });
    }

    private boolean login(){
        if(awesomeValidation.validate()) {
            String phoneNum = binding.edPhone.getText().toString();
            String pass = binding.edPassword.getText().toString();

            boolean userLogin = dbHelper.login(phoneNum, pass);
            if(!userLogin){
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                return false;
            }
            else{
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        else{
            Toast.makeText(Login.this, "Please fill all the available fields", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}