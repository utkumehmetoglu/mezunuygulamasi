package com.utku.odev;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.utku.odev.databinding.ActivityUploadBinding;
import com.utku.odev.entities.Post;

import java.io.ByteArrayOutputStream;


public class UploadActivity extends AppCompatActivity {

    private ActivityUploadBinding binding;

    private FirebaseStorage firebaseStorage;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    private StorageReference storageReference;


    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String>  permissionLauncher;

    Bitmap selectedImage;
    DatabaseReference postsRef = FirebaseDatabase.getInstance("https://utkuodev-c408d-default-rtdb.europe-west1.firebasedatabase.app").getReference("posts");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();


    }

    public void uploadButtonClicked (View view){

        if (imageData!= null){

            String postId  = postsRef.push().getKey();
            Post post = new Post(postId, auth.getUid(), binding.nameText.getText().toString());

            StorageReference uploadRef = storageReference.child("posts").child(auth.getUid()).child(postId + ".jpg");


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = uploadRef.putBytes(data);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(UploadActivity.this, "Error Posting ", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    postsRef.child(postId).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UploadActivity.this, "Post Success! ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(UploadActivity.this, "Post Error " + task.getException().getMessage() + " Stack Trace :" +  task.getException().getStackTrace(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });



                }
            });
        }

    }



    public void selectImage (View view){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            // Android 33 ve ustu READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){


                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                            //Request permission
                        }
                    }).show();

                }else{

                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){


                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
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
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                          else  {
                              // Version 28den kucukse

                                selectedImage = MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(), imageData);
                                binding.imageView.setImageBitmap(selectedImage);
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

                    Toast.makeText(UploadActivity.this, "izin verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}