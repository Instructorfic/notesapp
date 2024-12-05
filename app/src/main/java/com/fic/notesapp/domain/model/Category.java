package com.fic.notesapp.domain.model;

public class Category {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int id;

    @Override
    public String toString() {
        return name;
    }

    private String name;
}
