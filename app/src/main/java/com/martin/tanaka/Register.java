package com.martin.tanaka;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.martin.tanaka.DBHelper.DBHelper;
import com.martin.tanaka.databinding.ActivityRegisterBinding;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;

    double latitude, longitude;

    String encodedBitmapProfile ="";

    private static final int LOCATION_PICK_REQUEST = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10001;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private LocationRequest locationRequest;

    AwesomeValidation awesomeValidation;

    DBHelper Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBar.toolTitle.setText("Register");

        binding.appBar.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        //VALIDATE USER INPUTS
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //INITIALIZE SQLITE DATABASE
        Db = new DBHelper(this);

        awesomeValidation.addValidation(this, R.id.ed_name, RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        awesomeValidation.addValidation(this, R.id.ed_phone, RegexTemplate.NOT_EMPTY, R.string.invalid_phone);
        awesomeValidation.addValidation(this, R.id.ed_age, RegexTemplate.NOT_EMPTY, R.string.invalid_age);
        awesomeValidation.addValidation(this, R.id.ed_height, RegexTemplate.NOT_EMPTY, R.string.invalid_height);
        awesomeValidation.addValidation(this, R.id.ed_location, RegexTemplate.NOT_EMPTY, R.string.invalid_location);
        awesomeValidation.addValidation(this, R.id.ed_password, RegexTemplate.NOT_EMPTY, R.string.invalid_password);
        awesomeValidation.addValidation(this, R.id.ed_confirm_password, RegexTemplate.NOT_EMPTY, R.string.invalid_confirm_password);

        binding.capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

        //BUTTON  LOCATION CLICK
        binding.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PICK_REQUEST);
                }
                else{
                    if(isGps()){
                        getCurrentLocation();
                    }
                    else{
                        Log.d("MainActivity.this", "turnGps");
                        turnOnGps();
                    }
                }
            }
        });

        //BUTTON CONTINUE CLICK
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {

                    if(binding.spMarital.getSelectedItem().equals("Select Marital Status")){
                        Toast.makeText(Register.this, "Select your marital status", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(encodedBitmapProfile.isEmpty()){
                            Toast.makeText(Register.this, "Your Profile image is required", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            boolean phoneCheck = Db.checkPhone(binding.edPhone.getText().toString());
                            if (!phoneCheck) {
                            Boolean insert = Db.insertData(
                                    binding.edName.getText().toString(),
                                    binding.edPhone.getText().toString(),
                                    encodedBitmapProfile,
                                    binding.edAge.getText().toString(),
                                    binding.edHeight.getText().toString(),
                                    binding.spMarital.getSelectedItem().toString(),
                                    binding.edLocation.getText().toString(),
                                    "Longitude: " + longitude + " : Latitude: " + latitude,
                                    binding.edPassword.getText().toString()
                            );

                                if (insert) {
                                    Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, IqTest.class);
                                    startActivity(intent);
                                    Register.this.finish();
                                } else {
                                    Toast.makeText(Register.this, "Registered Failed", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(Register.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //CAMERA RESULT FUNCTION
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            //convert bitmap to base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedBitmapProfile = Base64.encodeToString(byteArray, Base64.DEFAULT);

            //set image
            binding.capture.setImageBitmap(photo);
        }
    }

    //LOCATION PERMISSION REQUEST
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PICK_REQUEST && grantResults.length > 0) {
        } else {}
    }

    //CHECK GPS TURN ON
    private void  turnOnGps(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(Register.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Register.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    //TURN ON GPS
    private boolean isGps(){
        LocationManager locationManager = null;
        boolean isEnabled = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isEnabled;
    }


    //GET USER CURRENT LOCATION
    private void getCurrentLocation() {
        ProgressDialog dialog = new ProgressDialog(Register.this);
        dialog.show();
        dialog.setMessage("Getting your current location...");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(Register.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(Register.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestlocationIndex = locationResult.getLocations().size() - 1;
                    latitude = locationResult.getLocations().get(latestlocationIndex).getLatitude();
                    longitude = locationResult.getLocations().get(latestlocationIndex).getLongitude();

                    reverseGeo();

                    dialog.dismiss();

                    Log.d("LOCATION","Longitude: " + longitude + " Latitude: " + latitude);
                }
            }
        }, Looper.getMainLooper());


    }

    //REVERSE GEO TAGGING
    private void reverseGeo(){
        try{
            Geocoder geocoder =new Geocoder(this, Locale.getDefault());
            List<Address> ad = geocoder.getFromLocation(latitude,longitude,2);
            String address = ad.get(0).getAddressLine(0);
            String city = ad.get(0).getLocality();
            binding.edLocation.setText(address);
            Log.d("Location", "Adress : "+ address+" City "+city);
        }
        catch (Exception e){
            e.printStackTrace();  //Latitude: 9.524. Longitude: 77.855 --> Mepco
        }
    }


}