package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapterNews extends RecyclerView.Adapter<MyAdapterNews.MyViewHolder> {
    Context context;
    ArrayList<News> newsArrayList;

    public MyAdapterNews(Context context, ArrayList<News> newsArrayList) {
        this.context = context;
        this.newsArrayList = newsArrayList;
    }

    @NonNull
    @Override
    public MyAdapterNews.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterNews.MyViewHolder holder, int position) {
        News news = newsArrayList.get(position);

        holder.titre.setText(news.titre);
        holder.date.setText(news.date);
        holder.description.setText(news.description);

        // Clique sur le lien de description d'un evenement
        holder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getDescription()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titre, date, description;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.tvTitreNews);
            date = itemView.findViewById(R.id.tvDateNews);
            description = itemView.findViewById(R.id.tvDescriptionNews);
        }
    }
}