package com.cst2335.androidfinalproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class BookAddingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SQLiteDatabase db;

    private String title = null;
    private String authors = null;
    private String year = null;
    private String description = null;
    private String genre = null;
    private String isbn10 = null;
    private String pageCount = null;
    private double rating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

//        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle hamburger = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
//        drawerLayout.addDrawerListener(hamburger);
//        hamburger.syncState();
//
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(this);

        Button addButton = findViewById(R.id.add_book_button);
        addButton.setOnClickListener(v -> {
            // Get values from EditTexts
            EditText titleView = findViewById(R.id.add_title);
            EditText authorView = findViewById(R.id.add_author);
            EditText yearView = findViewById(R.id.add_year);
            EditText descriptionView = findViewById(R.id.add_description);
            EditText genreView = findViewById(R.id.add_genre);
            EditText isbn10View = findViewById(R.id.add_isbn);
            EditText pageCountView = findViewById(R.id.add_page_count);
            RatingBar ratingBar = findViewById(R.id.add_user_rating);
            // Values must be validated prior to entering them in db
            title = titleView.getText().toString();
            authors = authorView.getText().toString();
            year = yearView.getText().toString();
            description = descriptionView.getText().toString();
            genre = genreView.getText().toString();
            isbn10 = isbn10View.getText().toString();
            pageCount = pageCountView.getText().toString();
            rating = Double.valueOf(ratingBar.getRating());

            String[] data = {title, authors, year, description, genre, isbn10, pageCount};

            if (isValidData(data)) {
                // Create new Book object
                Book book = new ConcreteBookBuilder().create()
                        .title(title)
                        .authors(formatAuthors(authors))
                        .year(year)
                        .description(description)
                        .genre(genre)
                        .isbn10(isbn10)
                        .pageCount(pageCount)
                        .build();

                if (isInDatabase(title, String.valueOf(year))) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setTitle("A book with the same title and year is already in your bookshelf")
                            .setMessage("Would you like to add this book anyways?")
                            .setPositiveButton(R.string.list_prompt_yes, (click, arg) -> {
                                addBookToDatabase(book);
                                Toast.makeText(this, "'" + title + "' " + getText(R.string.toast_db_added), Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton(R.string.list_prompt_no, (click, arg) -> {})
                            .create().show();
                } else {
                    // Add Book to SQLite database
                    addBookToDatabase(book);
                }

                // Clear results
                titleView.setText("");
                authorView.setText("");
                yearView.setText("");
                descriptionView.setText("");
                genreView.setText("");
                isbn10View.setText("");
                pageCountView.setText("");
            } else {
                // This should not be a Toast, but rather an error message or visual signal under/in the field that is not valid
                Toast.makeText(this, "Please ensure all information is added in the form", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isValidData(String[] data) {
        Boolean isValid = true;
        for (int i = 0; i < data.length; i++) {
            if (data[i].isEmpty()) {
                isValid = false;
            }
        }
        return isValid;
    }

    public ArrayList<String> formatAuthors(String authors) {
        ArrayList<String> authorsList = new ArrayList<>();
        String[] parts = authors.split(",");
        for (int i = 0; i < parts.length; i++) {
            authorsList.add(parts[i].trim());
        }
        return authorsList;
    }

    public boolean isInDatabase(String title, String year) {
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyOpener.BOOK_TABLE + " WHERE " + MyOpener.COL_TITLE + " = ? AND " + MyOpener.COL_YEAR + " = ?" , new String[] {title, year});
        return cursor.getCount() > 0;
    }

    public long addBookToDatabase(Book book) {
        // Get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        // Create ContentValues to insert row
        ContentValues cValues = new ContentValues();

        // Insert Book information into the table columns
        cValues.put(MyOpener.COL_TITLE, book.getTitle());
        cValues.put(MyOpener.COL_YEAR, book.getYear());
        cValues.put(MyOpener.COL_DESCRIPTION, book.getDescription());
        cValues.put(MyOpener.COL_ISBN, book.getISBN());
        cValues.put(MyOpener.COL_PAGE_COUNT, book.getPageCount());
        cValues.put(MyOpener.COL_GENRE, book.getGenre());
        //cValues.put(MyOpener.COL_PICTURE, pictureName);

        long id = db.insert(MyOpener.BOOK_TABLE, "NullColumnName", cValues);
        return id;
    }

    /**
     * When an item is clicked in the NavigationView, the user is sent to the selected Activity.
     * @param item the icon being clicked.
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setOnMenuItemClickListener(item1 -> {
            switch(item.getItemId()) {
                case R.id.menu_bookshelf:
                    Intent bookshelfActivity = new Intent(this, Bookshelf.class);
                    startActivity(bookshelfActivity);
                    return true;
                case R.id.menu_search:
                    Intent mainActivity = new Intent(this, MainActivity.class);
                    startActivity(mainActivity);
                    return true;
                case R.id.menu_add:
                    Intent bookAdding = new Intent(this, BookAddingActivity.class);
                    startActivity(bookAdding);
                    return true;
                case R.id.scan_book:
                    break;
            }
            return true;
        });
        return true;
    }
}
