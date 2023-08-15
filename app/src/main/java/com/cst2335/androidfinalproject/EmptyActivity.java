package com.cst2335.androidfinalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Used to display the Fragment when device is a tablet
 */
public class EmptyActivity extends AppCompatActivity {

    /**
     * Retrieves data passed from previous activity and loads the layout
     * @param savedInstanceState Bundle parameter to be passed to the super-constructor
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras();

        if (dataToPass.containsKey("bookshelfLayout")) {
            BookshelfDetailsFragment bookshelfDetailsFragment = new BookshelfDetailsFragment();
            bookshelfDetailsFragment.setArguments(dataToPass);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout_book_search, bookshelfDetailsFragment)
                    .commit();
        } else {
            Log.e("SEARCH", "HERE");
            SearchDetailsFragment searchDetailsFragment = new SearchDetailsFragment();
            searchDetailsFragment.setArguments(dataToPass);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout_book_search, searchDetailsFragment)
                    .commit();
        }
    }
}