package com.example.myapplication;

import java.io.Serializable;

public class UtilisateurConnecte implements Serializable {
    String nom, prenom, email;
    Role role;

    // Constructeurs de la classe
    public UtilisateurConnecte() {
        this.nom = "";
        this.prenom = "";
        this.email = "";
    }

    public UtilisateurConnecte(String nom, String prenom, String email, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    // Accesseurs Get/Set
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
