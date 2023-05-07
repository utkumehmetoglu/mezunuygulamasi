package com.utku.odev;



import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utku.odev.entities.News;
import com.utku.odev.entities.User;

import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> newsList;
    DatabaseReference usersRef = FirebaseDatabase.getInstance("https://utkuodev-c408d-default-rtdb.europe-west1.firebasedatabase.app").getReference("users");
    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        if(news.getUserID() != null)
         usersRef.child(news.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {

                }
                else {
                    User result = task.getResult().getValue(User.class);
                    Date date = new Date(news.getCreated_Millis());
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    String formatted = format.format(date);
                    System.out.println(formatted);
                    format.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
                    formatted = format.format(date);
                    holder.newsTextView.setText(formatted + "\n" +result.getName() + " " + result.getLastname() + " : " + news.getNewsText());
                }
            }});


    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTextView;

        NewsViewHolder(View itemView) {
            super(itemView);
            newsTextView = itemView.findViewById(R.id.news_textview);
        }
    }
}
