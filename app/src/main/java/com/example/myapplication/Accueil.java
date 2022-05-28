package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class Accueil extends AppCompatActivity {
    Animation rotateOpen, rotateClose, fromBottom, toBottom, fromRight, toRight ;
    FloatingActionButton fb1, fb2, fb3, fb4, fb5, fb6, fb7, deco;
    Boolean clicked = false;
    UtilisateurConnecte utilisateurConnecte;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String BT1 = "BTFR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_accueil);

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

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "38paGBlFn9g";
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        // Recupération des données de l'utilisateurs envoyé par ConnexionCAS
        utilisateurConnecte = (UtilisateurConnecte) getIntent().getSerializableExtra("utilisateurConnecte");
        String nom = utilisateurConnecte.getNom();
        String prenom = utilisateurConnecte.getPrenom();
        String role = String.valueOf(utilisateurConnecte.getRole());
        String mail = utilisateurConnecte.getEmail();

        // MENU
        fb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Accueil.this, PageEvenements.class);
                i.putExtra("utilisateurConnecte", utilisateurConnecte);
                startActivity(i);
                finish();
            }
        });

        fb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Accueil.this, PageNews.class);
                i.putExtra("utilisateurConnecte", utilisateurConnecte);
                startActivity(i);
                finish();
            }
        });
        if(utilisateurConnecte.getRole() == Role.Enseignant || utilisateurConnecte.getRole() == Role.Administrateur) {
            fb4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Accueil.this, PropositionEvenements.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });

            fb5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Accueil.this, PropositionNews.class);
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
                    Intent i = new Intent(Accueil.this, ValidationEvents.class);
                    i.putExtra("utilisateurConnecte", utilisateurConnecte);
                    startActivity(i);
                    finish();
                }
            });

            fb7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Accueil.this, ValidationNews.class);
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
                Intent i = new Intent(Accueil.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Affichage du message d'accueil
        LinearLayout linearLayout = findViewById(R.id.editTextContainer);
        TextView tvAccueil = new TextView(this);
        if(utilisateurConnecte.getPrenom()!="") {
            tvAccueil.setHint(getResources().getString(R.string.bienvenue) + "\n" + nom.toUpperCase() + " " + prenom);
            tvAccueil.setTextSize(30);
        }
        else {
            tvAccueil.setHint(getResources().getString(R.string.bien));
            tvAccueil.setTextSize(25);
        }
        tvAccueil.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvAccueil.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tvAccueil.setTypeface(null, Typeface.BOLD_ITALIC);

        if (linearLayout != null) {
            linearLayout.addView(tvAccueil);
        }

        // Evenement de clique sur le bouton de menu
        fb1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAddButtonClicked();
            }});
    }

    public void onAddButtonClicked(){
        Log.d("role", String.valueOf(utilisateurConnecte.getRole()));
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