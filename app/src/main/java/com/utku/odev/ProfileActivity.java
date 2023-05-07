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
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.utku.odev.databinding.ActivityProfileBinding;
import com.utku.odev.databinding.ActivityProfileBinding;
import com.utku.odev.entities.*;

import java.io.ByteArrayOutputStream;


public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    private FirebaseStorage firebaseStorage;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    private StorageReference storageReference;

    private DatabaseReference profileInfoRef;

    private DatabaseReference profileRef;
    private User user;

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String>  permissionLauncher;

    Bitmap selectedImage;
    DatabaseReference usersRef = FirebaseDatabase.getInstance("https://utkuodev-c408d-default-rtdb.europe-west1.firebasedatabase.app").getReference("users");
    UserInfo userInfo;
    DataSnapshot snapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        String UserId = auth.getUid();

        usersRef.child(UserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Veritabanı hatası: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                else {
                    user = task.getResult().getValue(User.class);
                    binding.fname.setText(user.getName());
                    binding.lname.setText(user.getLastname());
                    binding.entyear.setText(user.getEntranceYear() + "");
                    binding.gradYear.setText(user.getGradYear() + "");
                    binding.mail.setText(user.getEmail());
                    binding.password.setText(user.getPassword());
                    binding.address.setText(user.getAddress());
                    binding.contact.setText(user.getContact());
                    binding.company.setText(user.getCompany());

                    StorageReference uploadRef = storageReference.child("images/" + user.getId() + "/profile_picture.jpg");
                    uploadRef.getBytes(1024 * 1024 * 512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            binding.imageButton.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                    //binding.imageButton.setImageBitmap(user.getProfilePic());
                }
            }
        });

       // DataSnapshot infosnapshot = usersRef.child(UserId).child("Info").get().getResult();
       // userInfo = infosnapshot.getValue(UserInfo.class);



    }

    public StorageReference uploadFile(String userId){
        String fileName = "profile_picture.jpg";
        StorageReference uploadRef = storageReference.child("images/" + userId + "/" + fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = uploadRef.putBytes(data);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this, "Error While Uploading Profile Picture ", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Başarıyla yüklendikten sonra, FeedActivity'e yönlendirin.

            }
        });

        return uploadRef;
    }

    public void updateButtonClicked(View view){

        if(selectedImage != null)
            uploadFile(user.getId());



           User user_new = new User(user.getId(),binding.fname.getText().toString(),binding.lname.getText().toString(),Integer.parseInt(binding.entyear.getText().toString()),Integer.parseInt(binding.gradYear.getText().toString()),binding.mail.getText().toString(),binding.password.getText().toString()
                   ,binding.address.getText().toString(),binding.company.getText().toString(),binding.contact.getText().toString(),"grad");
           //UserInfo userInfo_new = new UserInfo(user,binding.address.getText().toString(),binding.contact.getText().toString(),binding.company.getText().toString(),"grad");
           usersRef.child(user.getId()).setValue(user_new).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()) {
                       usersRef.child(user_new.getId()).setValue(user_new);
                       Toast.makeText(ProfileActivity.this, "Update Success! ", Toast.LENGTH_SHORT).show();

                       finish();
                   } else {
                       Toast.makeText(ProfileActivity.this, "Update Error " + task.getException().getMessage() + " Stack Trace :" +  task.getException().getStackTrace(), Toast.LENGTH_SHORT).show();
                   }
               }

           });
          // usersRef.child(user_new.getId()).child("Info").setValue(userInfo_new);
    }



    public void reselectImage (View view){

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
                                binding.imageButton.setImageBitmap(selectedImage);
                            }
                            else  {
                                // Version 28den kucukse

                                selectedImage = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), imageData);
                                binding.imageButton.setImageBitmap(selectedImage);
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

                    Toast.makeText(ProfileActivity.this, "izin verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
