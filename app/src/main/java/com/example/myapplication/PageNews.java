package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class PageNews extends AppCompatActivity {
    private WebView webViewNews;
    String titre = "";
    String description = "";
    String date = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    ArrayList<News> newsArrayList;
    MyAdapterNews myAdapter;
    ProgressDialog progressDialog;
    Animation rotateOpen, rotateClose, fromBottom, toBottom, fromRight, toRight ;
    FloatingActionButton fb1, fb2, fb3, fb4, fb5, fb6, fb7, deco;
    Boolean clicked = false;
    UtilisateurConnecte utilisateurConnecte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_news);
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
        db.collection("News").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection("News").document(document.getId())
                                .delete();
                    }
                } else {
                    Log.d(TAG, "Une erreur s'est produite: ", task.getException());
                }
            }
        });

        // Ajout des news validé par l'administrateur à la collection News
        db.collection("AcceptedNews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        News news = new News(document.getString("titre"),
                                document.getString("date"), document.getString("description"));
                        db.collection("News")
                                .add(news);
                    }
                } else {
                    Log.d(TAG, "Une erreur s'est produite: ", task.getException());
                }
            }
        });

        webViewNews = (WebView) findViewById(R.id.webViewNews);
        webViewNews.getSettings().setJavaScriptEnabled(true);
        webViewNews.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webViewNews.setWebViewClient(new WebViewClient() {

            // Récupération des news présente sur le site Eugloh
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Récupération du nombre de news du site Eugloh
                view.evaluateJavascript("document.getElementsByClassName(\"mb-8\").length",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                int i;
                                // Itération sur chaque news du site
                                for (i = 1; i < Integer.parseInt(html); i++) {
                                    // Récupération du titre de la news
                                    view.evaluateJavascript("document.getElementsByClassName(\"mb-8\")[" + i + "].childNodes[1].children[1].getElementsByClassName(\"h4 mb-3\")[0].textContent",
                                            new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String html) {
                                                    html = html.replaceAll("\"", "");
                                                    storeNews(html,"","");
                                                }
                                            }
                                    );
                                    // Récupération de la date de la news
                                    view.evaluateJavascript("document.getElementsByClassName(\"mb-8\")[" + i + "].childNodes[1].children[1].getElementsByTagName(\"time\")[0].textContent",
                                            new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String html) {
                                                    html = html.replaceAll("\\\\n", "");
                                                    html = html.replaceAll("\"", "");
                                                    html = html.replaceAll("  ", "");
                                                    storeNews("",html,"");
                                                }
                                            }
                                    );
                                    // Récupération des liens qui mènent aux descriptions des news du site Eugloh
                                    view.evaluateJavascript("document.getElementsByClassName(\"mb-8\")[" + i + "].childNodes[1].children[1].getElementsByClassName(\"h4 mb-3\")[0].getElementsByTagName(\"a\")[0].getAttribute(\"href\")",
                                            new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String html) {
                                                    html = html.replaceAll("\"", "");
                                                    html = "https://www.eugloh.eu" + html;
                                                    storeNews("","",html);
                                                }
                                            }
                                    );
                                }
                            }
                        }
                );

            }
        });
        // Chargement de la page des news du site Eugloh
        webViewNews.loadUrl("https://www.eugloh.eu/news/eugloh-news");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.chargement));
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerViewNews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        newsArrayList = new ArrayList<News>();
        myAdapter = new MyAdapterNews(PageNews.this, newsArrayList);

        recyclerView.setAdapter(myAdapter);
        EventChangeListener();

        // MENU
        fb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PageNews.this, PageEvenements.class);
                i.putExtra("utilisateurConnecte", utilisateurConnecte);
                startActivity(i);
                finish();
            }
        });

        fb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PageNews.this, R.string.deja2, Toast.LENGTH_SHORT).show();
            }
        });
        if(utilisateurConnecte.getRole() == Role.Enseignant || utilisateurConnecte.getRole() == Role.Administrateur) {
            fb4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PageNews.this, PropositionEvenements.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });

            fb5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PageNews.this, PropositionNews.class);
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
                    Intent i = new Intent(PageNews.this, ValidationEvents.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });

            fb7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PageNews.this, ValidationNews.class);
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
                Intent i = new Intent(PageNews.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        fb1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAddButtonClicked();
            }});
    }

    public void storeNews(String titreR,String dateR, String descriptionR){
        if (titreR != "") {
            titre = titreR;
        }
        if (dateR != "") {
            date = dateR;
        }
        if (descriptionR != "") {
            description = descriptionR;
        }
        if(titre != "" && date != "" && description != ""){
            News news = new News(titre, date, description);
            db.collection("News")
                    .add(news);
            titre = "";
            date = "";
            description = "";
        }
    }

    private void EventChangeListener() {
        db.collection("News")
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
                                newsArrayList.add(dc.getDocument().toObject(News.class));
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