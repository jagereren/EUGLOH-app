package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyAdapterValidationNews extends RecyclerView.Adapter<MyAdapterValidationNews.MyViewHolder> {
    Context context;
    ArrayList<News> proposedNewsArrayList;
    FirebaseFirestore db;

    public MyAdapterValidationNews(Context context, ArrayList<News> proposedNewsArrayList) {
        this.context = context;
        this.proposedNewsArrayList = proposedNewsArrayList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_validation_news, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        News news = proposedNewsArrayList.get(position);

        holder.titre.setText(news.titre);
        holder.date.setText(news.date);
        holder.description.setText(news.description);

        // Action de clique sur le bouton d'acceptation d'une proposition de news
        holder.bouttonAccepter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                News actualite = new News(news.getTitre(), news.getDate(), news.getDescription());
                db.collection("AcceptedNews").add(actualite);
                db.collection("ProposedNews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("titre") == news.getTitre()) {
                                    db.collection("ProposedNews").document(document.getId())
                                            .delete();
                                    proposedNewsArrayList.remove(news);
                                    notifyDataSetChanged();
                                }
                            }
                            Toast.makeText(context, R.string.news, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "Une erreur s'est produite: ", task.getException());
                        }
                    }
                });
            }
        });

        // Action de clique sur le bouton de refus d'une proposition de news
        holder.bouttonRefuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("ProposedNews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("titre") == news.getTitre()) {
                                    db.collection("ProposedNews").document(document.getId())
                                            .delete();
                                    proposedNewsArrayList.remove(news);
                                    notifyDataSetChanged();
                                }
                            }
                            Toast.makeText(context, R.string.newsR, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "Une erreur s'est produite: ", task.getException());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return proposedNewsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titre, date, description;
        Button bouttonAccepter, bouttonRefuser;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titre = itemView.findViewById(R.id.tvTitreProposedNews);
            date = itemView.findViewById(R.id.tvDateProposedNews);
            description = itemView.findViewById(R.id.tvLocalisationProposedNews);
            bouttonAccepter = itemView.findViewById(R.id.boutonValiderNews);
            bouttonRefuser = itemView.findViewById(R.id.boutonRefuserNews);
        }
    }
}