package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ConnexionCAS extends AppCompatActivity {
    private WebView webView;
    String NomUtilisateur = "";
    String PrenomUtilisateur = "";
    String RoleUtilisateur = "";
    String MailUtilisateur = "";
    UtilisateurConnecte utilisateurConnecte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_connexion_cas);

        webView = (WebView) findViewById(R.id.myWebView);

        // Ajout des autorisations pour réaliser des commandes JavaScript sur la WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // Parametrage de la WebView
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Si la connexion CAS réussi alors on redirige vers la page CAS contenant l'ensemble des informations de l'utilisateur
                if(url.contains("idp.universite-paris-saclay.fr")) {
                    webView.setVisibility(View.GONE);
                    webView.loadUrl("https://sso.universite-paris-saclay.fr/cas/login");
                }
                return false;
            }

            // Cette méthode récupere les infomartions de l'utilisateur connecté sur la page CAS : nom, prénom, role, mail
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                if (url.equals("https://sso.universite-paris-saclay.fr/cas/login")) {
                    // Récupération du nom
                    webView.evaluateJavascript("document.getElementsByClassName(\"mdc-data-table__cell\")[19].textContent",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    if (html != null && html != "")
                                        redirectionAccueil(html, "", "", "");
                                }
                    });
                    // Récupération du prenom
                    webView.evaluateJavascript("document.getElementsByClassName(\"mdc-data-table__cell\")[15].textContent",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    if (html != null && html != "")
                                        redirectionAccueil("", html, "", "");
                                }
                    });
                    // Récupération du role
                    webView.evaluateJavascript("document.getElementsByClassName(\"mdc-data-table__cell\")[9].textContent",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    if (html != null && html != "")
                                        redirectionAccueil("", "", html, "");
                                }
                    });
                    // Récupération du mail
                    webView.evaluateJavascript("document.getElementsByClassName(\"mdc-data-table__cell\")[11].textContent",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    if (html != null && html != "")
                                        redirectionAccueil("", "", "", html);
                                }
                    });

                }
            }

        });
        // Chargement de la page d'authentification eCampus de Paris-Saclay
        webView.loadUrl("https://sso.universite-paris-saclay.fr/cas/login?service=https%3A%2F%2Fidp.universite-paris-saclay.fr%2Fidp%2FAuthn%2FExternal%3Fconversation%3De1s2&entityId=https%3A%2F%2Fecampus.paris-saclay.fr%2Fauth%2Fsaml2%2Fsp%2Fmetadata.php");
    }

    // Cette méthode recoit les informations de l'utilisateur connecté et les stockes dans les variables globale NomUtilisateur, PrenomUtilisateur, RoleUtilisateur et MailUtilisateur
    public void redirectionAccueil(String nom, String prenom, String role, String mail){
        // Suppression des élements inutile des chaines de caractères (", [ , \, etc) grâce à la méthode replaceString
        if(nom != null && nom != "")
            NomUtilisateur = replaceString(nom);
        else if(prenom != null && prenom != "")
            PrenomUtilisateur = replaceString(prenom);
        else if(role != null && role != "")
            RoleUtilisateur = replaceString(role);
        else if(mail != null && mail != "")
            MailUtilisateur = replaceString(mail);

        Log.d("role",RoleUtilisateur );

        if(NomUtilisateur != "" && PrenomUtilisateur != "" && RoleUtilisateur != "" && MailUtilisateur != "") {
            if(NomUtilisateur.equals("Yhia") && PrenomUtilisateur.equals("Ounas"))
                utilisateurConnecte = new UtilisateurConnecte(NomUtilisateur,PrenomUtilisateur, MailUtilisateur, Role.Administrateur);
            else if(NomUtilisateur.equals("Souam") && PrenomUtilisateur.equals("Yacine") || !RoleUtilisateur.equals("student"))
                utilisateurConnecte = new UtilisateurConnecte(NomUtilisateur, PrenomUtilisateur, MailUtilisateur, Role.Enseignant);
            else
                utilisateurConnecte = new UtilisateurConnecte(NomUtilisateur, PrenomUtilisateur, MailUtilisateur, Role.Etudiant);
            Intent i = new Intent(ConnexionCAS.this, Accueil.class);
            i.putExtra("utilisateurConnecte", utilisateurConnecte);
            startActivity(i);
            finish();
        }
    }

    public String replaceString(String data){
        data = data.replaceAll("\\s", "");
        data = data.replaceAll("\\\\n", "");
        data = data.replaceAll("\\[", "");
        data = data.replaceAll("\\]", "");
        data = data.replaceAll("\"", "");
        data = data.replaceAll(" ", "");
        return data;
    }
}
