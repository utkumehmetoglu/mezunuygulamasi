package com.utku.odev;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.utku.odev.databinding.ActivityRegisterBinding;
import com.utku.odev.entities.User;
import com.utku.odev.util.ImageUtil;
import com.utku.odev.entities.UserInfo;


import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    private FirebaseStorage firebaseStorage;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    private StorageReference storageReference;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = FirebaseDatabase.getInstance("https://utkuodev-c408d-default-rtdb.europe-west1.firebasedatabase.app").getReference("users");

    Uri imageData;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String>  permissionLauncher;

    Bitmap selectedImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();


        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();


    }


    public void registerClicked(View view){
        String email = binding.userMail.getText().toString();
        String password = binding.userPassword.getText().toString();
        String name = binding.userName.getText().toString();
        String lname = binding.userLName.getText().toString();
        int entranceYear = Integer.parseInt(binding.userEntranceYear.getText().toString());
        int gradYear = Integer.parseInt(binding.userGradYear.getText().toString());

        if (validate_register(binding) == true) {

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    String userId = authResult.getUser().getUid();

                    // Dosya yolunu yapılandırın
                    String fileName = "profile_picture.jpg";
                    StorageReference uploadRef = storageReference.child("images/" + userId + "/" + fileName);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = uploadRef.putBytes(data);


                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(RegisterActivity.this, "Error While Uploading Profile Picture ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Başarıyla yüklendikten sonra, FeedActivity'e yönlendirin.
                            String b64pic = "";
                            User user = new User(userId,name,lname,entranceYear,gradYear,email,password);
                            usersRef.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        usersRef.child(userId).setValue(user);
                                        Toast.makeText(RegisterActivity.this, "Register Success! ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, FeedActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Register Error " + task.getException().getMessage() + " Stack Trace :" +  task.getException().getStackTrace(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });

                        }
                    });



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            });


        }


    }


    private boolean validate_register(ActivityRegisterBinding binding){
        if(binding.userPassword.getText().toString().isEmpty() || binding.userPassword.getText().toString().length() < 8) {
            Toast.makeText(RegisterActivity.this, "Password cannot be empty or shorter than 8 characters!", Toast.LENGTH_LONG).show();
            return  false;
        }
        else if(binding.userMail.getText().toString().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") == false){
            Toast.makeText(RegisterActivity.this, "Invalid Email!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(imageData == null){
            Toast.makeText(RegisterActivity.this, "No Profile Picture Selected", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(auth.isSignInWithEmailLink(binding.userMail.getText().toString())){
            Toast.makeText(RegisterActivity.this, "This mail is already being used", Toast.LENGTH_LONG).show();
            return false;
        }
        try{
            Integer.parseInt(binding.userGradYear.getText().toString());
            Integer.parseInt(binding.userEntranceYear.getText().toString());
        }
        catch (Exception ex){
            return false;
        }



        return true;
    }


    private void registerLauncher (){

        activityResultLauncher  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode()== RESULT_OK){
                    Intent intentFromResult =  result.getData();
                    if(intentFromResult!=null){
                        imageData = intentFromResult.getData();
                        // binding.imageView.setImageURI(imageData);

                        try {

                            if (Build.VERSION.SDK_INT > 28) {

                                // versiyon 28den buyuklerde createSource

                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView2.setImageBitmap(selectedImage);
                            }
                            else  {
                                // Version 28den kucukse

                                selectedImage = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), imageData);
                                binding.imageView2.setImageBitmap(selectedImage);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result){
                    //izin verildi

                    Intent intentToGallery  = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );

                    activityResultLauncher.launch(intentToGallery);

                }
                else {
                    //izin verilmedi

                    Toast.makeText(RegisterActivity.this, "izin verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void selectImageRegister (View view){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            // Android 33 ve ustu READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)){


                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                            //Request permission
                        }
                    }).show();

                }else{

                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }
                //request
            }
            else {
                //gallery'e git
                Intent intentToGallery  = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );

                activityResultLauncher.launch(intentToGallery);
            }

        }
        else {

            // Android version 32 ve alti ise READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){


                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                            //Request permission

                        }
                    }).show();

                }else{

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }
                //request
            }
            else {
                //gallery'e git
                Intent intentToGallery  = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );

                activityResultLauncher.launch(intentToGallery);

            }
        }
    }


}
