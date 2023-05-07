package com.utku.odev;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utku.odev.entities.News;


import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private EditText newsEditText;
    private Button postButton;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private FirebaseAuth auth;
    private DatabaseReference newsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsEditText = findViewById(R.id.news_edittext);
        postButton = findViewById(R.id.post_button);
        recyclerView = findViewById(R.id.news_recyclerview);
        auth = FirebaseAuth.getInstance();
        // Firebase veritabanı referansını alıyoruz
        newsRef = FirebaseDatabase.getInstance("https://utkuodev-c408d-default-rtdb.europe-west1.firebasedatabase.app").getReference("news");

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNews();
            }
        });

        // RecyclerView için layout yöneticisini ayarlıyoruz
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // RecyclerView için adapteri oluşturuyoruz
        newsAdapter = new NewsAdapter();
        recyclerView.setAdapter(newsAdapter);

        // Firebase veritabanında duyuru kayıtlarını dinliyoruz
        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<News> newsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    News news = snapshot.getValue(News.class);
                    newsList.add(news);
                }
                newsAdapter.setNewsList(newsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewsActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void postNews() {
        String newsText = newsEditText.getText().toString().trim();

        if (!newsText.isEmpty()) {
            // Firebase veritabanına yeni bir duyuru kaydediyoruz
            String newsId = newsRef.push().getKey();
            News news = new News(newsId, newsText, auth.getUid() );
            newsRef.child(newsId).setValue(news).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(NewsActivity.this, "Duyuru paylaşıldı", Toast.LENGTH_SHORT).show();
                        newsEditText.setText("");
                    } else {
                        Toast.makeText(NewsActivity.this, "Duyuru paylaşma hatası: " + task.getException().getMessage() + " Stack Trace :" +  task.getException().getStackTrace(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Duyuru metni boş olamaz", Toast.LENGTH_SHORT).show();
        }
    }
}
