package com.cst2335.androidfinalproject;

import java.util.ArrayList;

/** Representation of a Book */
public class Book {

    private long id;
    private String isbn10;
    private String title;
    private ArrayList<String> authors;
    private String year;
    private String description;
    private String pageCount;
    private String genre;
    private double rating;
    private String pictureName;

    /**
     * Parameterized constructor.
     * @param builder The builder used to create a Book object
     */
    public Book(ConcreteBookBuilder builder) {
        this.id = builder.getId();
        this.isbn10 = builder.getIsbn10();
        this.title = builder.getTitle();
        this.authors = builder.getAuthors();
        this.year = builder.getYear();
        this.description = builder.getDescription();
        this.genre = builder.getGenre();
        this.pageCount = builder.getPageCount();
        this.rating = builder.getRating();
        this.pictureName = builder.getPictureName();
    }

    /**
     * Returns the Book's id
     * @return the Book's id
     */
    public long getId() { return id; }

    /**
     * Returns the Book's ISBN 10
     * @return the Book's ISBN 10
     */
    public String getISBN() {
        return isbn10;
    }

    /**
     * Returns the Book's name
     * @return the Book's name
     */
    public String getTitle() { return title; }

    /**
     * The Books's authors
     * @return the Books's authors
     */
    public ArrayList<String> getAuthors() { return authors; }

    /**
     * Returns the Books's publishing year
     * @return the Book's publishing year
     */
    public String getYear() { return year; }

    /**
     * Returns the Books's description
     * @return the Book's description
     */
    public String getDescription() { return description; }

    /**
     * Returns the Book's genre
     * @return the Book's genre
     */
    public String getGenre() { return genre; }

    /**
     * Returns the Book's page count.
     * @return the Book's page count.
     */
    public String getPageCount() { return pageCount; }

    /**
     * Returns the Book's rating out of five stars
     * @return the Book's rating
     */
    public double getRating() { return rating; }

    /**
     * Sets the Book's rating
     */
    public void setRating(double rating) { this.rating = rating; }

    /**
     * Returns the Books's picture name. Used to retrieve the picture from local storage.
     * @return the Books's picture name.
     */
    public String getPictureName() { return pictureName; }

    /**
     * Returns a String representation of the Book
     * @return a String representation of the Book
     */
    public String toString() {
        return "Book details:\nTitle: " + title + "\nAuthors: " + authors.toString() + "\nYear: " + year + "\nGenre: " + genre + "\n" + description;
    }

    /**
     * Returns a formatted String with the Book's authors' names separated by commas. Used in ListView.
     * @return a formatted String with the Book's authors' names separated by commas
     */
    public String formattedAuthors() {
        if (authors.size() > 0) {
            StringBuilder printAuthors = new StringBuilder();
            for(int i = 0; i < authors.size(); i++) {
                if(i < authors.size() - 1) {
                    printAuthors.append(authors.get(i) + ", ");
                } else {
                    printAuthors.append(authors.get(i));
                }
            }
            return printAuthors.toString();
        }
        return "";
    }

}
