package com.cst2335.androidfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/** Displays the chosen Book's details. */
public class BookshelfDetailsFragment extends Fragment {

    private Bundle dataFromBookList;
    private AppCompatActivity parentActivity;
    private ArrayList<String> bookAuthors;
    private String bookISBN10;
    private String bookTitle;
    private String bookGenre;
    private String bookDescription;
    private String bookThumbnailStr;
    private int bookPageCount;
    private double bookRating;
    private int bookYear;
    private Bitmap bookThumbnail;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    /** {@value} Used in printCursor() to debug **/
    private static final String TAG = "DETAILS_FRAGMENT";
    private SQLiteDatabase db;

    /**
     * Inflates the layout and sets the views to display a Book's details.
     *
     * @param inflater           the view's inflater
     * @param container          the view's container
     * @param savedInstanceState Bundle parameter to be passed to the super-constructor.
     * @return the fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromBookList = getArguments();

        // Inflate the layout for the fragment
        View fragmentView = inflater.inflate(R.layout.fragment_bookshelf_details, container, false);

        // Retrieve the XML views
        TextView bookTitleView = (TextView) fragmentView.findViewById(R.id.display_book_name_bookshelf);
        TextView bookAuthorsView = (TextView) fragmentView.findViewById(R.id.display_book_authors_bookshelf);
        //TextView bookYearView = (TextView) fragmentView.findViewById(R.id.display_book_year);
        TextView bookDescriptionView = (TextView) fragmentView.findViewById(R.id.display_book_description_bookshelf);
        //TextView bookPageCountView = (TextView) fragmentView.findViewById(R.id.display_page_count);
        ImageView bookThumbnailView = (ImageView) fragmentView.findViewById(R.id.book_thumbnail_bookshelf);
        RatingBar ratingBar = fragmentView.findViewById(R.id.user_rating);

        // Retrieve the data from the intent
        bookISBN10 = dataFromBookList.getString(BookSearchList.ITEM_ISBN);
        bookTitle = dataFromBookList.getString(BookSearchList.ITEM_TITLE);
        bookAuthors = dataFromBookList.getStringArrayList(BookSearchList.ITEM_AUTHORS);
        bookYear = dataFromBookList.getInt(BookSearchList.ITEM_YEAR);
        bookDescription = dataFromBookList.getString(BookSearchList.ITEM_DESCRIPTION);
        bookPageCount = dataFromBookList.getInt(BookSearchList.ITEM_PAGE_COUNT);
        bookGenre = dataFromBookList.getString(BookSearchList.ITEM_GENRE);
        bookRating = dataFromBookList.getDouble(BookSearchList.ITEM_RATING);
        bookThumbnailStr = dataFromBookList.getString(BookSearchList.ITEM_PICTURE);

        // Set the views
        bookTitleView.setText(bookTitle);
        String formattedAuthors = formatAuthorsList(bookAuthors);
        bookAuthorsView.setText(formattedAuthors + " (" + bookYear + ")");
        //bookYearView.setText(String.valueOf(bookYear));
        bookDescriptionView.setText(bookDescription);
        ratingBar.setRating((float) bookRating);
        // bookPageCountView.setText(bookPageCount + " " + getText(R.string.fragment_book_pages));

        // Sets the view if the picture is found locally. Learned how to use getActivity() in [2]
        if (fileExists(bookThumbnailStr)) {
            FileInputStream fis = null;
            try {
                fis = getActivity().openFileInput(bookThumbnailStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bookThumbnail = BitmapFactory.decodeStream(fis);
            bookThumbnailView.setImageBitmap(bookThumbnail);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                MyOpener dbOpener = new MyOpener(getActivity());
                SQLiteDatabase db = dbOpener.getWritableDatabase();
                final String BOOK_TABLE = "BOOKS";
                final String WHERE_CLAUSE = "ISBN=?";
                String[] args = { bookISBN10 };

                ContentValues contentValues = new ContentValues();
                contentValues.put("RATING", Double.valueOf(rating));

                db.update(BOOK_TABLE, contentValues, WHERE_CLAUSE, args);
            }
        });
        return fragmentView;
    }

    public String formatAuthorsList(ArrayList<String> authors) {
        StringBuilder authorList = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i < authors.size() - 1) {
                authorList.append(authors.get(i) + ", ");
            } else {
                authorList.append(authors.get(i));
            }
        }
        return authorList.toString();
    }

    /**
     * Attached the fragment to its parent activity, which depends on the device being used.
     *
     * @param context the current Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }

    /**
     * Determines whether a picture is saved in local storage
     *
     * @param fileName the picture's name
     * @return true if picture was found
     */
    // Checks if the file exists in the local storage directory
    public boolean fileExists(String fileName) {
        Log.e(TAG, "Image was found locally");
        File file = parentActivity.getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    public boolean isInDatabase(String isbn10) {
        MyOpener dbOpener = new MyOpener(getActivity());
        db = dbOpener.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyOpener.BOOK_TABLE + " WHERE " + MyOpener.COL_ISBN + " = ?", new String[]{isbn10});
        boolean isInDatabase = cursor.getCount() > 0;
        cursor.close();
        return isInDatabase;
    }

    /**
     * Adds a Book to the local SQLite database.
     *
     * @param title       the Book's title
     *                    authors the Book's authors
     * @param year        the Book's first ingredient
     * @param description the Book's second ingredient
     * @param genre       the Book's third ingredient
     * @param pictureName the Book's picture title
     */
    public long addBookToDatabase(String isbn10, String title, int year, String description, int pageCount, String genre, String pictureName) {
        // Get a database connection:
        MyOpener dbOpener = new MyOpener(getActivity());
        db = dbOpener.getWritableDatabase();

        // Create ContentValues to insert row
        ContentValues cValues = new ContentValues();

        // Insert Book information into the table columns
        cValues.put(MyOpener.COL_ISBN, isbn10);
        cValues.put(MyOpener.COL_TITLE, title);
        cValues.put(MyOpener.COL_YEAR, year);
        cValues.put(MyOpener.COL_DESCRIPTION, description);
        cValues.put(MyOpener.COL_PAGE_COUNT, pageCount);
        cValues.put(MyOpener.COL_GENRE, genre);
        cValues.put(MyOpener.COL_PICTURE, pictureName);

        long id = db.insert(MyOpener.BOOK_TABLE, "NullColumnName", cValues);
        return id;
    }

    public long addAuthorToDatabase(String name, String isbn10) {
        MyOpener dbOpener = new MyOpener(getActivity());
        db = dbOpener.getWritableDatabase();

        ContentValues cValues = new ContentValues();

        // Insert author information into table columns
        cValues.put(MyOpener.COL_NAME, name);
        cValues.put(MyOpener.COL_ISBN, isbn10);
        long id = db.insert(MyOpener.AUTHOR_TABLE, "NullColumnName", cValues);
        return id;
    }
}

// Reference cited:
// [1] AndroidDeveloper, "View," Android Developers. https://developer.android.com/reference/android/view/View (accessed Aug. 06, 2022).
// [2] A. Xattar, "Answer to 'Error with openFileOutput in Fragments,'" Stack Overflow, Nov. 06, 2015. https://stackoverflow.com/a/33561763 (accessed Aug. 03, 2022).
