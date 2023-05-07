package com.utku.odev;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utku.odev.entities.News;
import com.utku.odev.entities.Post;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private DatabaseReference postRef = FirebaseDatabase.getInstance("https://utkuodev-c408d-default-rtdb.europe-west1.firebasedatabase.app").getReference("posts");

    private FeedAdapter feedAdapter = new FeedAdapter();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        recyclerView  = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(feedAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        auth = FirebaseAuth.getInstance();

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> postsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    postsList.add(post);
                }
                feedAdapter.setPostList(postsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FeedActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.add_post) {
            // Upload Activity
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);


        } else if (item.getItemId() == R.id.signout) {

            //Signout

            auth.signOut();

            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();

        }


      else if(item.getItemId() == R.id.newsA){
           Intent intentToNews = new Intent (FeedActivity.this, NewsActivity.class);

           startActivity(intentToNews);

        }
      else if(item.getItemId() == R.id.edit_profile){
            Intent intentToEditProfile = new Intent (FeedActivity.this, ProfileActivity.class);

            startActivity(intentToEditProfile);
        }









        return super.onOptionsItemSelected(item);
    }
}