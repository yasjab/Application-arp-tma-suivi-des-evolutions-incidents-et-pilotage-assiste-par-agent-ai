package com.atos.arp.models;

import java.io.Serializable;

public class Categorie implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String categorieName;
    private String statutName;

    public Categorie() {
    }

    public Categorie(int id, String categorieName, String statutName) {

        this.id = id;
        this.categorieName = categorieName;
        this.statutName = statutName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategorieName() {
        return categorieName;
    }

    public void setCategorieName(String categorieName) {
        this.categorieName = categorieName;
    }

    public String getStatutName() {
        return statutName;
    }

    public void setStatutName(String statutName) {
        this.statutName = statutName;
    }

    public String toString() {
        return "Categorie [id=" + id + ", categorie=" + categorieName + ", statutName=" + statutName + "]";
    }
}
