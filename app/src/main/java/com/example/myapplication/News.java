package com.example.myapplication;

public class News {
    String titre, date, description;

    // Constructeur de la classe News

    public News(){}

    public News(String titre, String date, String description) {
        this.titre = titre;
        this.date = date;
        this.description = description;
    }

    // Accesseurs Get
    public String getTitre() {
        return titre;
    }

    public String getDate() {
        return date;
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

    public void setDescription(String description) {
        this.description = description;
    }
}
