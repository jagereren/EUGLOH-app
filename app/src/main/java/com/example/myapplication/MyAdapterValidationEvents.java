package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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

public class MyAdapterValidationEvents extends RecyclerView.Adapter<MyAdapterValidationEvents.MyViewHolder> {
    Context context;
    ArrayList<Evenements> proposedEventArrayList;
    FirebaseFirestore db;

    public MyAdapterValidationEvents(Context context, ArrayList<Evenements> proposedEventArrayList) {
        this.context = context;
        this.proposedEventArrayList = proposedEventArrayList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_validation_event, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Evenements evenements = proposedEventArrayList.get(position);

        holder.titre.setText(evenements.titre);
        holder.date.setText(evenements.date);
        holder.localisation.setText(evenements.localisation);
        holder.groupeCible.setText(evenements.groupeCible);
        holder.host.setText(evenements.host);
        holder.dateLimite.setText(evenements.dateLimite);
        holder.description.setText(evenements.description);

        // Action de clique sur le bouton d'acceptation d'une proposition d'evenement
        holder.bouttonAccepter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Evenements evt = new Evenements(evenements.getTitre(), evenements.getDate(), evenements.getLocalisation(), evenements.getGroupeCible(),
                        evenements.getHost(), evenements.getDateLimite(), evenements.getDescription());
                db.collection("AcceptedEvents").add(evt);
                db.collection("ProposedEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("titre") == evenements.getTitre()) {
                                    db.collection("ProposedEvents").document(document.getId())
                                            .delete();
                                    proposedEventArrayList.remove(evenements);
                                    notifyDataSetChanged();
                                }
                            }
                            Toast.makeText(context, R.string.event, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "Une erreur s'est produite: ", task.getException());
                        }
                    }
                });
            }
        });

        // Action de clique sur le bouton de refus d'une proposition d'evenement
        holder.bouttonRefuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("ProposedEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("titre") == evenements.getTitre()) {
                                    db.collection("ProposedEvents").document(document.getId())
                                            .delete();
                                    proposedEventArrayList.remove(evenements);
                                    notifyDataSetChanged();
                                }
                            }
                            Toast.makeText(context, R.string.eventR, Toast.LENGTH_SHORT).show();
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
        return proposedEventArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titre, date, localisation, groupeCible, host, dateLimite, description;
        Button bouttonAccepter, bouttonRefuser;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titre = itemView.findViewById(R.id.tvTitreProposedEvent);
            date = itemView.findViewById(R.id.tvDateProposedEvent);
            localisation = itemView.findViewById(R.id.tvLocalisationProposedEvent);
            groupeCible = itemView.findViewById(R.id.tvGroupeCibleProposedEvent);
            host = itemView.findViewById(R.id.tvHostProposedEvent);
            dateLimite = itemView.findViewById(R.id.tvDateLimiteProposedEvent);
            description = itemView.findViewById(R.id.tvDesctiptionProposedEvent);
            bouttonAccepter = itemView.findViewById(R.id.boutonValiderEvent);
            bouttonRefuser = itemView.findViewById(R.id.boutonRefuserEvent);
        }
    }
}