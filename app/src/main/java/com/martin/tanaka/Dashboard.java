package com.martin.tanaka;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.martin.tanaka.DBHelper.DBHelper;
import com.martin.tanaka.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity {

    ActivityDashboardBinding binding;

    DBHelper dbHelper;

    String phone = "";
    String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBar.toolTitle.setText("Dashboard");
        binding.appBar.img.setVisibility(View.GONE);

        dbHelper = new DBHelper(this);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                phone = null;
            } else {
                phone = extras.getString("Phone");
            }
        }
        else {
            phone = (String) savedInstanceState.getSerializable("Phone");
        }

        Cursor res = dbHelper.getData(phone);
        if(res.getCount()==0){
            Toast.makeText(Dashboard.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
            return;
        }

        while(res.moveToNext()){
            binding.txtName.setText("Full Names :" + res.getString(0));
            binding.txtPhone.setText("Phone Number : "+ res.getString(1));
            encodedImage = res.getString(2);
            binding.txtAge.setText("Age : "+ res.getString(3));
            binding.txtHeight.setText("Height : "+ res.getString(4));
            binding.txtMarital.setText("Marital Status : "+ res.getString(5));
            binding.txtLocation.setText("Location : "+ res.getString(6));
            binding.txtScore.setText("Your Score : "+ res.getString(7));
        }

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        binding.profile.setImageBitmap(decodedByte);
    }
}