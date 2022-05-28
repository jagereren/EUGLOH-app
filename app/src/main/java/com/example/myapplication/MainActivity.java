package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.PointerIcon;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    UtilisateurConnecte utilisateurConnecte;
    TextView tvBienvenue;
    Button btnCommencer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        ImageButton btnConnexion = findViewById(R.id.boutonConnexion);
        btnCommencer = findViewById(R.id.boutonCommencer);
        ImageButton langueFR = findViewById(R.id.imageLangueFr);
        ImageButton langueEN = findViewById(R.id.imageLangueAng);
        tvBienvenue = findViewById(R.id.tvBienvenue);

        // Clique sur le bouton de connexion
        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ConnexionCAS.class);
                startActivity(i);
                finish();
            }
        });

        // Clique sur le bouton "Commencer"
        btnCommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilisateurConnecte = new UtilisateurConnecte();
                Intent i = new Intent(MainActivity.this, Accueil.class);
                i.putExtra("utilisateurConnecte", utilisateurConnecte);
                startActivity(i);
            }
        });
        langueFR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String language = "fr";
                    setLocale(language);

                }
        }
        );

        langueEN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   String language = "en";
                    setLocale(language);
                }
            }
        );
    }

    private void setLocale(String language) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration,metrics);
        onConfigurationChanged(configuration);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tvBienvenue.setText(getString(R.string.accueil_titre));
        btnCommencer.setText(getString(R.string.commencer));
    }
}