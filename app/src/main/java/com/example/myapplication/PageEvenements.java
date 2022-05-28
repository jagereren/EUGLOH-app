package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PageEvenements extends AppCompatActivity {
    private WebView webViewEvents;
    String titre = "";
    String description = "";
    String date = "";
    String location = "";
    String tg = "";
    String host = "";
    String dl = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    ArrayList<Evenements> eventArrayList;
    MyAdapterEvents myAdapter;
    ProgressDialog progressDialog;
    Animation rotateOpen, rotateClose, fromBottom, toBottom, fromRight, toRight ;
    FloatingActionButton fb1, fb2, fb3, fb4, fb5, fb6, fb7, deco;
    Boolean clicked = false;
    UtilisateurConnecte utilisateurConnecte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_evenements);
        getSupportActionBar().hide();

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        fromRight = AnimationUtils.loadAnimation(this, R.anim.from_right_anim);
        toRight = AnimationUtils.loadAnimation(this, R.anim.to_right_anim);

        fb1 = (FloatingActionButton) findViewById(R.id.boutonMenu);
        fb2 = (FloatingActionButton) findViewById(R.id.boutonEvents);
        fb3 = (FloatingActionButton) findViewById(R.id.boutonNews);
        fb4 = (FloatingActionButton) findViewById(R.id.boutonPropositionEvent);
        fb5 = (FloatingActionButton) findViewById(R.id.boutonPropositionNews);
        fb6 = (FloatingActionButton) findViewById(R.id.boutonVerifierEvent);
        fb7 = (FloatingActionButton) findViewById(R.id.boutonVerifierNews);
        deco = (FloatingActionButton) findViewById(R.id.boutonDeconnexion);

        utilisateurConnecte = (UtilisateurConnecte) getIntent().getSerializableExtra("utilisateurConnecte");

        // Suppression de tout les evenements stocké dans FireStore
        db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection("Events").document(document.getId())
                                .delete();
                    }
                } else {
                    Log.d(TAG, "Une erreur s'est produite: ", task.getException());
                }
            }
        });

        // Ajout des evenement validé par l'administrateur à la collection Events
        db.collection("AcceptedEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Evenements evenements = new Evenements(document.getString("titre"),
                                document.getString("date"), document.getString("localisation"),
                                document.getString("groupeCible"), document.getString("host"),
                                document.getString("dateLimite"), document.getString("description"));
                        db.collection("Events")
                                .add(evenements);
                    }
                } else {
                    Log.d(TAG, "Une erreur s'est produite: ", task.getException());
                }
            }
        });

        webViewEvents = (WebView) findViewById(R.id.webViewEvents);
        webViewEvents.getSettings().setJavaScriptEnabled(true);
        webViewEvents.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // Récupération des évenements présent sur le site Eugloh
        webViewEvents.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                super.onPageFinished(webViewEvents, url);
                // Recupération du nombre d'évenement du site Eugloh
                webViewEvents.evaluateJavascript("document.getElementsByClassName(\"mb-8\").length",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                int i;
                                if(html != null && html != "") {
                                    for (i = 0; i <Integer.valueOf(html); i++){
                                        int iterationEvents = i;
                                        // Récupération du nombre d'informations pour l'evenement courant
                                        webViewEvents.evaluateJavascript("document.getElementsByClassName(\"mb-8\")[" + i + "].childNodes[1].childElementCount",
                                                new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String html) {
                                                        if (html != null && html != ""){
                                                            int numberDataEvent = Integer.valueOf(html);
                                                            // Récupération du titre de l'evenement courant
                                                            webViewEvents.evaluateJavascript("document.getElementsByClassName(\"h4 mb-3\")[" + iterationEvents + "].textContent",
                                                                    new ValueCallback<String>() {
                                                                        @Override
                                                                        public void onReceiveValue(String html) {
                                                                            if (html != null && html != ""){
                                                                                html = html.replaceAll("\"", "");
                                                                                storeEvents(html,"","", -1,numberDataEvent/2);
                                                                            }
                                                                        }
                                                                    }
                                                            );
                                                            // Récupération du lien qui mène à la description de l'event courant sur le site Eugloh
                                                            webViewEvents.evaluateJavascript("document.getElementsByClassName(\"h4 mb-3\")[" + iterationEvents + "].getElementsByTagName(\"a\")[0].getAttribute(\"href\")",
                                                                    new ValueCallback<String>() {
                                                                        @Override
                                                                        public void onReceiveValue(String html) {
                                                                            if (html != null && html != ""){
                                                                                html = html.replaceAll("\"", "");
                                                                                html = "https://www.eugloh.eu/" + html;
                                                                                storeEvents("",html,"",-1,numberDataEvent/2);
                                                                            }
                                                                        }
                                                                    }
                                                            );
                                                            // Récupération des informations de l'evenement courant
                                                            int j;
                                                            for(j = 0; j < numberDataEvent/2; j++){
                                                                int finalJ = j;
                                                                webViewEvents.evaluateJavascript("document.getElementsByClassName(\"mb-8\")[" + iterationEvents + "].childNodes[1].getElementsByClassName(\"cell medium-2\")[" + j + "].textContent + \" : \" + document.getElementsByClassName(\"mb-8\")[" + iterationEvents + "].childNodes[1].getElementsByClassName(\"cell medium-10\")[" + j + "].textContent",
                                                                        new ValueCallback<String>() {
                                                                            @Override
                                                                            public void onReceiveValue(String html) {
                                                                                if (html != null && html != ""){
                                                                                    html = html.replaceAll("\\\\n", "");
                                                                                    html = html.replaceAll("\"", "");
                                                                                    html = html.replaceAll("  ", "");
                                                                                    storeEvents("","",html, finalJ,numberDataEvent/2);
                                                                                }
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                            }
                        }
                );
            }
        });
        // Chargement de la page des events du site Eugloh
        webViewEvents.loadUrl("https://www.eugloh.eu/study-and-mobility/events");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.chargement));
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventArrayList = new ArrayList<Evenements>();
        myAdapter = new MyAdapterEvents(PageEvenements.this, eventArrayList, utilisateurConnecte);

        recyclerView.setAdapter(myAdapter);
        EventChangeListener();

        // MENU
        fb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PageEvenements.this, R.string.deja, Toast.LENGTH_SHORT);
            }
        });

        fb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PageEvenements.this, PageNews.class);
                i.putExtra("utilisateurConnecte", utilisateurConnecte);
                startActivity(i);
                finish();
            }
        });
        if(utilisateurConnecte.getRole() == Role.Enseignant || utilisateurConnecte.getRole() == Role.Administrateur) {
            fb4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PageEvenements.this, PropositionEvenements.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });

            fb5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PageEvenements.this, PropositionNews.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });
        }
        if(utilisateurConnecte.getRole() == Role.Administrateur) {
            fb6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PageEvenements.this, ValidationEvents.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });

            fb7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PageEvenements.this, ValidationNews.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });
        }
        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    //Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();
                }
                else{
                    CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(getApplication().getApplicationContext());
                    cookieSyncMngr.startSync();
                    CookieManager cookieManager=CookieManager.getInstance();
                    cookieManager.removeAllCookie();
                    cookieManager.removeSessionCookie();
                    cookieSyncMngr.stopSync();
                    cookieSyncMngr.sync();
                }
                Intent i = new Intent(PageEvenements.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        fb1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAddButtonClicked();
            }});
    }

    // Fonction qui stocke les events récupérés sur le site Eugloh
    public void storeEvents(String titreR, String descriptionR, String dataR, int iterationEvents, int derniereIteration){
        if(iterationEvents == derniereIteration-1) {
            if (dataR != "" && dataR.contains("Date :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Date : ", "");
                date = dataR;
            }
            if (dataR != "" && dataR.contains("Location :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Location : ", "");
                location = dataR;
            }
            if (dataR != "" && dataR.contains("Target group :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Target group : ", "");
                tg = dataR;
            }
            if (dataR != "" && dataR.contains("Host :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Host : ", "");
                host = dataR;
            }
            if (dataR != "" && dataR.contains("Registration :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("\\(", "");
                dataR = dataR.replaceAll("\\)", "");
                dataR = dataR.replaceAll(" : ", "");
                dataR = dataR.replaceAll("Deadline: ", "");

                if(dataR.contains("Open"))
                    dataR = dataR.replaceAll("RegistrationOpen", "");
                else
                    dataR = dataR.replaceAll("RegistrationClosed", "");
                dl = dataR;
            }

            Evenements evenements = new Evenements(titre, date, location, tg, host, dl, description);
            db.collection("Events")
                    .add(evenements);
            titre = "";
            description = "";
            date = "";
            location = "";
            tg = "";
            host = "";
            dl = "";
        }
        else {
            if (titreR != "") {
                titre = titreR;
            }
            if (descriptionR != "") {
                description = descriptionR;
            }
            if (dataR != "" && dataR.contains("Date :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Date : ", "");
                date = dataR;
            }
            if (dataR != "" && dataR.contains("Location :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Location : ", "");
                location = dataR;
            }
            if (dataR != "" && dataR.contains("Target group :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Target group : ", "");
                tg = dataR;
            }
            if (dataR != "" && dataR.contains("Host :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("Host : ", "");
                host = dataR;
            }
            if (dataR != "" && dataR.contains("Registration :")) {
                dataR = dataR.replaceAll("\"", "");
                dataR = dataR.replaceAll("\\(", "");
                dataR = dataR.replaceAll("\\)", "");
                dataR = dataR.replaceAll(" : ", "");
                dataR = dataR.replaceAll("Deadline: ", "");

                if(dataR.contains("Open"))
                    dataR = dataR.replaceAll("RegistrationOpen", "");
                else
                    dataR = dataR.replaceAll("RegistrationClosed", "");
                dl = dataR;
            }
        }
    }
    private void EventChangeListener() {
        db.collection("Events")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("FireStore error : ", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                eventArrayList.add(dc.getDocument().toObject(Evenements.class));
                            }
                            myAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }

    public void onAddButtonClicked(){
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        if(!clicked){
            clicked = true;
        }
        else{
            clicked = false;
        }
    }

    private void setAnimation(Boolean clicked) {
        if(!clicked){
            fb2.startAnimation(fromBottom);
            fb3.startAnimation(fromBottom);
            if(utilisateurConnecte.getRole() == Role.Administrateur || utilisateurConnecte.getRole() == Role.Enseignant) {
                fb4.startAnimation(fromRight);
                fb5.startAnimation(fromRight);
            }
            if(utilisateurConnecte.getRole() == Role.Administrateur) {
                fb6.startAnimation(fromRight);
                fb7.startAnimation(fromRight);
            }
            deco.startAnimation(fromBottom);

            fb1.startAnimation(rotateOpen);
        }
        else{
            fb2.startAnimation(toBottom);
            fb3.startAnimation(toBottom);
            if(utilisateurConnecte.getRole() == Role.Administrateur || utilisateurConnecte.getRole() == Role.Enseignant) {
                fb4.startAnimation(toRight);
                fb5.startAnimation(toRight);
            }
            if(utilisateurConnecte.getRole() == Role.Administrateur) {
                fb6.startAnimation(toRight);
                fb7.startAnimation(toRight);
            }
            deco.startAnimation(toBottom);
            fb1.startAnimation(rotateClose);
        }
    }

    private void setVisibility(Boolean clicked) {
        if(!clicked){
            fb2.setVisibility(View.VISIBLE);
            fb3.setVisibility(View.VISIBLE);
            if(utilisateurConnecte.getRole() == Role.Administrateur || utilisateurConnecte.getRole() == Role.Enseignant) {
                fb4.setVisibility(View.VISIBLE);
                fb5.setVisibility(View.VISIBLE);
            }
            if(utilisateurConnecte.getRole() == Role.Administrateur) {
                fb6.setVisibility(View.VISIBLE);
                fb7.setVisibility(View.VISIBLE);
            }
            deco.setVisibility(View.VISIBLE);
        }
        else{
            fb2.setVisibility(View.INVISIBLE);
            fb3.setVisibility(View.INVISIBLE);
            fb4.setVisibility(View.INVISIBLE);
            fb5.setVisibility(View.INVISIBLE);
            fb6.setVisibility(View.INVISIBLE);
            fb7.setVisibility(View.INVISIBLE);
            deco.setVisibility(View.INVISIBLE);

        }
    }

    private void setClickable(Boolean clicked){
        if(!clicked){
            fb2.setClickable(true);
            fb3.setClickable(true);
            if(utilisateurConnecte.getRole() == Role.Administrateur || utilisateurConnecte.getRole() == Role.Enseignant) {
                fb4.setClickable(true);
                fb5.setClickable(true);
            }
            if(utilisateurConnecte.getRole() == Role.Administrateur) {
                fb6.setClickable(true);
                fb7.setClickable(true);
            }
            deco.setClickable(true);
        }
        else{
            fb2.setClickable(false);
            fb3.setClickable(false);
            fb4.setClickable(false);
            fb5.setClickable(false);
            fb6.setClickable(false);
            fb7.setClickable(false);
            deco.setClickable(false);
        }
    }
}
