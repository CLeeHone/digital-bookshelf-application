package com.cst2335.androidfinalproject;

import java.util.ArrayList;

public class ConcreteBookBuilder extends BookBuilder {
    private long id;
    private String isbn10;
    private String title;
    private ArrayList<String> authors;
    private String year;
    private String description;
    private String pageCount;
    private String genre;
    private String pictureName;
    private double rating;

    // Getters used in Book class to set Book's variables
    public long getId() { return this.id; }
    public String getIsbn10() { return this.isbn10; }
    public String getTitle() { return this.title; }
    public ArrayList<String> getAuthors() { return this.authors; }
    public String getYear() { return this.year; }
    public String getDescription() { return this.description; }
    public String getPageCount() { return this.pageCount; }
    public String getGenre() { return this.genre; }
    public String getPictureName() { return this.pictureName; }
    public double getRating() { return this.rating; }

    // Setters used to create a Book object
    public ConcreteBookBuilder create() {
        return new ConcreteBookBuilder();
    }

    public ConcreteBookBuilder id(long id) {
        this.id = id;
        return this;
    }

    public ConcreteBookBuilder isbn10(String isbn10) {
        this.isbn10 = isbn10;
        return this;
    }

    public ConcreteBookBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ConcreteBookBuilder authors(ArrayList<String> authors) {
        this.authors = authors;
        return this;
    }

    public ConcreteBookBuilder year(String year) {
        this.year = year;
        return this;
    }

    public ConcreteBookBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ConcreteBookBuilder pageCount(String pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public ConcreteBookBuilder genre(String genre) {
        this.genre = genre;
        return this;
    }

    public ConcreteBookBuilder pictureName(String pictureName) {
        this.pictureName = pictureName;
        return this;
    }

    public ConcreteBookBuilder rating(double rating) {
        this.rating = rating;
        return this;
    }

    public Book build() {
        Book book = new Book(this);
        return book;
    }

}
