package com.cst2335.androidfinalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/** An activity in which the user can view their saved books. */
public class Bookshelf extends AppCompatActivity {
    private ArrayList<Book> bookArrayList = new ArrayList<>();
    private AppCompatActivity parentActivity;
    private ListAdapter bookListAdapter = new ListAdapter();
    private SQLiteDatabase db;
    public static final String ITEM_TITLE = "BOOK TITLE";
    public static final String ITEM_ISBN = "ISBN";
    public static final String ITEM_AUTHORS = "AUTHORS";
    public static final String ITEM_YEAR = "YEAR";
    public static final String ITEM_DESCRIPTION = "DESCRIPTION";
    public static final String ITEM_PAGE_COUNT = "PAGE_COUNT";
    public static final String ITEM_GENRE = "GENRE";
    public static final String ITEM_RATING = "RATING";
    public static final String ITEM_PICTURE = "PICTURE";
    public static final String BOOKSHELF = "bookshelfLayout";

    /**
     * Loads the view and sets listeners to the buttons
     *
     * @param savedInstanceState Bundle parameter to be passed to the super-constructor
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        parentActivity = (AppCompatActivity)this;

        // Instantiate ListView and its Adapter
        ListView bookListView = findViewById(R.id.bookshelf_listview);
        bookListView.setAdapter(bookListAdapter);

        boolean isTablet = findViewById(R.id.bookshelf_frame_layout) != null;

        loadDataFromDatabase();

        // View Book details
        bookListView.setOnItemClickListener((list, position, item, id) -> {
            Bundle bundle = new Bundle();

            bundle.putString(ITEM_TITLE, bookArrayList.get(item).getTitle());
            bundle.putString(ITEM_ISBN, bookArrayList.get(item).getISBN());
            bundle.putStringArrayList(ITEM_AUTHORS, bookArrayList.get(item).getAuthors());
            bundle.putString(ITEM_YEAR, bookArrayList.get(item).getYear());
            bundle.putString(ITEM_DESCRIPTION, bookArrayList.get(item).getDescription());
            bundle.putString(ITEM_PAGE_COUNT, bookArrayList.get(item).getPageCount());
            bundle.putString(ITEM_GENRE, bookArrayList.get(item).getGenre());
            bundle.putDouble(ITEM_RATING, bookArrayList.get(item).getRating());
            if (!bookArrayList.get(item).getPictureName().isEmpty()) {
                bundle.putString(ITEM_PICTURE, bookArrayList.get(item).getPictureName());
            }
            bundle.putString(BOOKSHELF, BOOKSHELF);

            if (isTablet) {
                BookshelfDetailsFragment bookshelfDetailsFragment = new BookshelfDetailsFragment();
                bookshelfDetailsFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.bookshelf_frame_layout, bookshelfDetailsFragment)
                        .commit();
            } else {
                //will transition to EmptyActivity class
                Intent nextActivity = new Intent(Bookshelf.this, EmptyActivity.class);
                nextActivity.putExtras(bundle);
                startActivity(nextActivity);
            }
        });

        // Remove selected Book from the database
        bookListView.setOnItemLongClickListener((list, item, position, id) -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(R.string.list_alert_delete_title)
                    .setMessage(getText(R.string.list_alert_delete_message).toString())
                    .setPositiveButton(R.string.list_prompt_yes, (click, arg) -> {
                        String bookName = bookArrayList.get(position).getTitle();
                        // Remove the Book from the database
                        deleteBook(bookArrayList.get(position));
                        // Remove from ArrayList
                        bookArrayList.remove(position);
                        // Update the view
                        bookListAdapter.notifyDataSetChanged();
                        Snackbar.make(findViewById(R.id.bookshelf_listview), bookName + " " + getText(R.string.toast_db_removed), Snackbar.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.list_prompt_no, (click, arg) -> {
                    })
                    .create().show();
            return true;
        });

        // Refine displayed books
        SearchView search = findViewById(R.id.bookshelf_search);

    }

    public void onResume() {
        super.onResume();
        bookArrayList.clear();
        loadDataFromDatabase();
        bookListAdapter.notifyDataSetChanged();
    }


    /**
     * Inflate the menu items for use in the action bar
     *
     * @param menu the menu to be inflated
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cocktail_menu, menu);
        return true;
    }

    /**
     * Display an alert explaining how this application functions
     *
     * @param menuItem the selected menu item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        AlertDialog.Builder helpAlertBuilder = new AlertDialog.Builder(this);
        helpAlertBuilder.setMessage(R.string.help_menu_message)
                .setTitle(R.string.help_menu_title)
                .setNeutralButton(R.string.alert_message_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
        return true;
    }

    /**
     * Delete a Book from the database. Called upon confirmation that a message should be
     * deleted. Informed by [1] and [2].
     *
     * @param book the Book to be deleted from the database
     */
    protected void deleteBook(Book book) {
        db.delete(MyOpener.BOOK_TABLE, MyOpener.COL_ID + "= ?", new String[]{Long.toString(book.getId())});
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyOpener.BOOK_TABLE, null);
        //printCursor(cursor, db.getVersion());
    }

    /** Loads favourite Books from the local SQLite database */
    private void loadDataFromDatabase() {
        // Get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        // Gets all columns from Book and Author tables
        String[] bookColumns = {MyOpener.COL_ID, MyOpener.COL_ISBN, MyOpener.COL_TITLE, MyOpener.COL_YEAR, MyOpener.COL_DESCRIPTION, MyOpener.COL_PAGE_COUNT, MyOpener.COL_GENRE, MyOpener.COL_RATING, MyOpener.COL_PICTURE};
        String[] authorColumns = {MyOpener.COL_ID, MyOpener.COL_NAME, MyOpener.COL_ISBN};
        // Queries all the results from the database:
        Cursor bookResults = db.query(false, MyOpener.BOOK_TABLE, bookColumns, null, null, null, null, null, null);

        // Now the results object has rows of results that match the query.
        // Find the column indices:
        int idColIndex = bookResults.getColumnIndex(MyOpener.COL_ID);
        int isbnColIndex = bookResults.getColumnIndex(MyOpener.COL_ISBN);
        int titleColumnIndex = bookResults.getColumnIndex(MyOpener.COL_TITLE);
        int yearColumnIndex = bookResults.getColumnIndex(MyOpener.COL_YEAR);
        int descriptionColumnIndex = bookResults.getColumnIndex(MyOpener.COL_DESCRIPTION);
        int pageCountColumnIndex = bookResults.getColumnIndex(MyOpener.COL_PAGE_COUNT);
        int genreColumnIndex = bookResults.getColumnIndex(MyOpener.COL_GENRE);
        int ratingColumnIndex = bookResults.getColumnIndex(MyOpener.COL_RATING);
        int pictureNameColIndex = bookResults.getColumnIndex(MyOpener.COL_PICTURE);

        // Iterate over the results.
        while (bookResults.moveToNext()) {
            long id = bookResults.getLong(idColIndex);
            String isbn10 = bookResults.getString(isbnColIndex);
            String title = bookResults.getString(titleColumnIndex);
            String year = bookResults.getString(yearColumnIndex);
            String description = bookResults.getString(descriptionColumnIndex);
            String pageCount = bookResults.getString(pageCountColumnIndex);
            String genre = bookResults.getString(genreColumnIndex);
            double rating = bookResults.getDouble(ratingColumnIndex);
            String pictureName = bookResults.getString(pictureNameColIndex);

            Cursor authorsCursor = db.rawQuery("SELECT * FROM " + MyOpener.AUTHOR_TABLE + " WHERE " + MyOpener.COL_ISBN + " = ?", new String[] {isbn10});
            ArrayList<String> authorsArray = new ArrayList<>();

            int index = 1;
            authorsCursor.moveToFirst();
            while(!authorsCursor.isAfterLast()) {
                authorsArray.add(authorsCursor.getString(index));
                authorsCursor.moveToNext();
            }

            // Add the new Book to the ArrayList.
            Book book = new ConcreteBookBuilder().create()
                    .id(id)
                    .isbn10(isbn10)
                    .title(title)
                    .authors(authorsArray)
                    .year(year)
                    .description(description)
                    .genre(genre)
                    .pageCount(pageCount)
                    .rating(rating)
                    .pictureName(pictureName)
                    .build();

            bookArrayList.add(book);
        }
    }

    /**
     * Custom Adapter for the Book Searching Application
     */
    private class ListAdapter extends BaseAdapter {
        /**
         * Returns the number of Books in the ArrayList
         *
         * @return the number of Books in the ArrayList
         */
        @Override
        public int getCount() {
            return bookArrayList.size();
        }

        /**
         * Returns the selected row index
         *
         * @return the selected row index
         */
        @Override
        public Book getItem(int position) {
            return bookArrayList.get(position);
        }

        /**
         * Returns the database id
         *
         * @param position the item's position in the ArrayList
         * @return the item's position in the databse
         */
        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        /**
         * Inflates the view. Consulted [2] to gain a better understanding of how the getView() method functions.
         *
         * @return the inflated view.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            Book book = getItem(position);
            TextView layoutBook;
            TextView layoutAuthors;
            ImageView layoutThumbnail;

            convertView = inflater.inflate(R.layout.book_title, parent, false);
            layoutBook = convertView.findViewById(R.id.book_result_name);
            layoutBook.setText(book.getTitle());

            layoutAuthors = convertView.findViewById(R.id.book_author_names);
            layoutAuthors.setText(book.formattedAuthors());

            layoutThumbnail = convertView.findViewById(R.id.book_result_thumbnail);
            String bookThumbnailName = book.getPictureName();
            Bitmap bookThumbnail = null;

            String pictureName = book.getPictureName();
            if (fileExists(pictureName)) {
                FileInputStream fis = null;
                try {
                    fis = openFileInput(pictureName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bookBitmap = BitmapFactory.decodeStream(fis);
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.book_result_thumbnail);
                thumbnail.setImageBitmap(bookBitmap);
            }

            return convertView;
        }

        /**
         * Determines whether a picture is currently saved in local storage
         * @param fileName the picture's name
         * @return true if picture was found
         */
        // Checks if the file exists in the local storage directory
        public boolean fileExists(String fileName) {
            File file = parentActivity.getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }
    }
}

// References cited:
// [1] C. Smotricz, "Answer to 'How do I check that a Java String is not all whitespaces?,'" Stack Overflow, Jul. 14, 2010. https://stackoverflow.com/a/3247081 (accessed Aug. 03, 2022).