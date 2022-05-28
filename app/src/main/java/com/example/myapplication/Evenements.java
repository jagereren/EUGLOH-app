package com.example.myapplication;

public class Evenements {
    String titre, date, localisation, groupeCible, host, dateLimite, description;

    // Constructeurs de la classe Evenements

    public Evenements() {}

    public Evenements(String titre, String date, String localisation, String groupeCible, String host, String dateLimite, String description) {
        this.titre = titre;
        this.date = date;
        this.localisation = localisation;
        this.groupeCible = groupeCible;
        this.host = host;
        this.dateLimite = dateLimite;
        this.description = description;
    }

    // Accesseurs Get

    public String getTitre() {
        return titre;
    }

    public String getDate() {
        return date;
    }

    public String getLocalisation() {
        return localisation;
    }

    public String getGroupeCible() {
        return groupeCible;
    }

    public String getHost() {
        return host;
    }

    public String getDateLimite() {
        return dateLimite;
    }

    public String getDescription() {
        return description;
    }

    // Accesseurs Set
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public void setGroupeCible(String groupeCible) {
        this.groupeCible = groupeCible;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setDateLimite(String dateLimite) {
        this.dateLimite = dateLimite;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}