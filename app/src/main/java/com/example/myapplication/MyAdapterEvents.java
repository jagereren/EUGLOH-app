package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.util.Locale;

public class MyAdapterEvents extends RecyclerView.Adapter<MyAdapterEvents.MyViewHolder> {
    Context context;
    ArrayList<Evenements> eventArrayList;
    FirebaseFirestore db;
    UtilisateurConnecte utilisateurConnecte;

    public MyAdapterEvents(Context context, ArrayList<Evenements> eventArrayList, UtilisateurConnecte utilisateurConnecte) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.utilisateurConnecte = utilisateurConnecte;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyAdapterEvents.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterEvents.MyViewHolder holder, int position) {
        Evenements evenements = eventArrayList.get(position);

        holder.titre.setText(evenements.titre);
        holder.date.setText(evenements.date);
        holder.localisation.setText(evenements.localisation);
        holder.groupeCible.setText(evenements.groupeCible);
        holder.host.setText(evenements.host);
        holder.dateLimite.setText(evenements.dateLimite);
        holder.description.setText(evenements.description);

        String nom = utilisateurConnecte.getNom();
        String prenom = utilisateurConnecte.getPrenom();
        String mail = utilisateurConnecte.getEmail();
        Role role = utilisateurConnecte.getRole();

        // Clique sur le lien de description d'un evenement
        holder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(evenements.getDescription()));
                context.startActivity(browserIntent);
            }
        });

            // Clique sur le bouton d'inscription à un evenement
            holder.boutonSinscrire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(role==Role.Enseignant){
                        Toast.makeText(context, R.string.t1, Toast.LENGTH_SHORT).show();
                    }
                    else if(prenom != "") {
                        Thread gfgThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Mailer.send("euglohsystem@gmail.com", "euglohsystemYACINEOUNASILYASS", "burkhart.wolff@universite-paris-saclay.fr", "Demande d'inscription à un evenement",
                                            "L'étudiant " + nom.toUpperCase() + " " + prenom + " souhaite s'inscrire à l'évènement suivant : " + evenements.getTitre());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        gfgThread.start();
                        Toast.makeText(context, R.string.t2, Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(context, R.string.t3, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titre, date, localisation, groupeCible, host, dateLimite, description;
        Button boutonSinscrire;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.tvTitreEvent);
            date = itemView.findViewById(R.id.tvDateEvent);
            localisation = itemView.findViewById(R.id.tvLocalisationEvent);
            groupeCible = itemView.findViewById(R.id.tvGroupeCibleEvent);
            host = itemView.findViewById(R.id.tvHostEvent);
            dateLimite = itemView.findViewById(R.id.tvDateLimiteEvent);
            description = itemView.findViewById(R.id.tvDesctiptionEvent);
            boutonSinscrire = itemView.findViewById(R.id.boutonSinscrireEvent);
        }
    }
}