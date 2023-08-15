package com.cst2335.androidfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * Application landing page. Contains a Toolbar, a NavigationView, and a DrawerLayout. Used [1],
 * [2], and [3] to understand how Toolbar, DrawerLayout, and NavigationView work together.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /** {@value} The Book's name */
    public static final String BOOK_NAME = "title";
    /** {@value} Flag sent to by BookSearchList */
    public static final String LOAD_FAVOURITES = "favourites";
    private EditText userBook;
    private Button searchButton;
    private Button favouritesButton;

    /**
     * Loads the View, Toolbar, DrawerLayout, and NavigationView.
     * @param savedInstanceState Bundle parameter to be passed to the super-constructor
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle hamburger = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(hamburger);
        hamburger.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener( v -> {
            Intent cocktailActivity = new Intent(MainActivity.this, Bookshelf.class);
            startActivity(cocktailActivity);
        });

        searchButton = findViewById(R.id.search_button);
        userBook = findViewById(R.id.user_book);

        SharedPreferences sharedPrefs = getSharedPreferences("com.cst2335.androidfinalproject.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        String savedBookKey = getString(R.string.saved_user_book);
        String retrievedBook = sharedPrefs.getString(savedBookKey, "");
        userBook.setText(String.valueOf(retrievedBook));

        // Send the user's search terms to another activity
        searchButton.setOnClickListener( v-> {
            // Application cannot search for blank user input. Forces user to enter something into the search box.
            // Learned how to verify if input is blank using trim() and length() in [1].
            if (userBook.getText().toString().trim().length() == 0) {
                Toast.makeText(this, R.string.search_toast, Toast.LENGTH_LONG).show();
            } else {
                Intent nextActivity = new Intent(MainActivity.this, BookSearchList.class);
                nextActivity.putExtra(BOOK_NAME, userBook.getText().toString());
                startActivity(nextActivity);
            }
        });
    }

//    /**
//     * Inflates the menu
//     * @param menu the menu where layout is inflated
//     * @return true
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_activity_menu, menu);
//        return true;
//    }

//    /**
//     * When menu icon is clicked, user is sent to CocktailSearch activity
//     * @param item the icon being clicked
//     * @return true
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case R.id.menu_bookshelf:
//                Intent bookshelfActivity = new Intent(MainActivity.this, Bookshelf.class);
//                startActivity(bookshelfActivity);
//                return true;
//            case R.id.menu_search:
//                Intent mainActivity = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(mainActivity);
//                return true;
//            case R.id.menu_add:
//                break;
//            case R.id.scan_book:
//                break;
//        }
//        return true;
//    }

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

    /**
     * Saves the user's inputs when the Activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPrefs = getSharedPreferences("com.cst2335.androidfinalproject.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(getString(R.string.saved_user_book), userBook.getText().toString());
        editor.apply();
    }
}

// Reference cited:
// [1] E. Torunski, "Week8_Toolbar - 22S_CST2335_010 Mobile Graphical Interface Prog.," Algonquin College, Jul. 28, 2022. https://brightspace.algonquincollege.com/d2l/le/content/444410/viewContent/7030236/View (accessed Aug. 06, 2022).
// [2] E. Torunski, "etorunski/InClassExamples_W21 at week8_NavigationDrawer," Github, 2021. https://github.com/etorunski/InClassExamples_W21/tree/week8_NavigationDrawer (accessed Aug. 06, 2022).
// [3] E. Torunski, "etorunski/InClassExamples_W21 at week8_toolbar," Github, 2021. https://github.com/etorunski/InClassExamples_W21/tree/week8_toolbar (accessed Aug. 06, 2022).
// [4] S. Dozor, "Answer to 'How can you get the build/version number of your Android application?,'" Stack Overflow, Jan. 14, 2014. https://stackoverflow.com/a/21119027 (accessed Aug. 06, 2022).