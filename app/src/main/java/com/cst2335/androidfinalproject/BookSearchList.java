package com.cst2335.androidfinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/** Queries Google Books API to search for Books */
public class BookSearchList extends AppCompatActivity {

    public static final String ITEM_TITLE = "BOOK TITLE";
    public static final String ITEM_ISBN = "ISBN";
    public static final String ITEM_AUTHORS = "AUTHORS";
    public static final String ITEM_YEAR = "YEAR";
    public static final String ITEM_DESCRIPTION = "DESCRIPTION";
    public static final String ITEM_PAGE_COUNT = "PAGE_COUNT";
    public static final String ITEM_GENRE = "GENRE";
    public static final String ITEM_RATING = "RATING";
    public static final String ITEM_PICTURE = "PICTURE";
    public static final double DEFAULT_RATING = 0.0;
    private String bookName;
    private ArrayList<Book> bookArrayList = new ArrayList<>();
    private ListAdapter bookListAdapter;
    private SQLiteDatabase db;
    private AppCompatActivity parentActivity;

    /**
     * Loads the ArrayList containing Book names
     *
     * @param savedInstanceState Bundle parameter to be passed to the super-constructor
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search_result_list);
        //
        parentActivity = (AppCompatActivity)this;

        // Instantiate ListView and its Adapter
        ListView bookListView = findViewById(R.id.bookshelf_listview);
        bookListView.setAdapter(bookListAdapter = new ListAdapter());

        // Set the Book's name and format it for the URL
        bookName = getIntent().getStringExtra(MainActivity.BOOK_NAME);
        bookName = formatBookName(bookName);

        BookQuery query = new BookQuery();
        query.execute();

        boolean isTablet = findViewById(R.id.frame_layout_book_search) != null;

        bookListView.setOnItemClickListener((list, item, position, id) -> {
            Bundle bundle = new Bundle();

            bundle.putString(ITEM_TITLE, bookArrayList.get(position).getTitle());
            bundle.putString(ITEM_ISBN, bookArrayList.get(position).getISBN());
            bundle.putStringArrayList(ITEM_AUTHORS, bookArrayList.get(position).getAuthors());
            bundle.putString(ITEM_YEAR, bookArrayList.get(position).getYear());
            bundle.putString(ITEM_DESCRIPTION, bookArrayList.get(position).getDescription());
            bundle.putString(ITEM_PAGE_COUNT, bookArrayList.get(position).getPageCount());
            bundle.putDouble(ITEM_RATING, bookArrayList.get(position).getRating());
            bundle.putString(ITEM_GENRE, bookArrayList.get(position).getGenre());
            bundle.putString(ITEM_PICTURE, bookArrayList.get(position).getPictureName());

            if (isTablet) {
                SearchDetailsFragment searchDetailsFragment = new SearchDetailsFragment();
                searchDetailsFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout_book_search, searchDetailsFragment)
                        .commit();
            } else {
                //will transition to EmptyActivity class
                Intent nextActivity = new Intent(BookSearchList.this, EmptyActivity.class);
                nextActivity.putExtras(bundle);
                startActivity(nextActivity);
            }
        });
    }

    /**
     * Formats the user's input for use in query's URL. Replaces spaces with '+'.
     *
     * @param userInput the name of the Book searched by the user
     * @return formatted String that can be used in a URL
     */
    public String formatBookName(String userInput) {
        StringBuilder sb = new StringBuilder(userInput);
        for (int i = 0; i < userInput.length(); i++) {
            if (sb.charAt(i) == ' ') {
                sb.replace(i, i + 1, "+");
            }
        }
        return sb.toString();
    }

    /** Queries Google Books API to retrieve books and add them to an Arraylist */
    public class BookQuery extends AsyncTask<String, Integer, String> {

        private String id = null;
        private String isbn10 = null;
        private String title = null;
        private String year = null;
        private String description = null;
        private String pageCount = null;
        private String genre = null;
        private String bookThumbnailName = null;
        private Bitmap bookThumbnail = null;

        /**
         * Queries the Google Book API, retrieves JSON objects, parses information, and adds a
         * new Book to the ArrayList.
         *
         * @param strings doInBackground() arguments
         * @return a String that is passed to onPostExecute() [3]
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                String apiKey = getResources().getString(R.string.api_key);
                URL bookURL = new URL("https://www.googleapis.com/books/v1/volumes?q=" + bookName + apiKey);

                Log.e("URL IS", String.valueOf(bookURL));
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) bookURL.openConnection();

                //wait for the data
                InputStream response = urlConnection.getInputStream();

                // Learned how to use BufferedReader and use JSONObjects in [1]
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String stringResult = sb.toString();

                JSONObject bookReport = new JSONObject(stringResult);
                JSONArray bookArray = bookReport.getJSONArray("items");

                for (int i = 0; i < bookArray.length(); i++) {
                    JSONObject objectFromArray = bookArray.getJSONObject(i);
                    JSONObject volumeInfo = objectFromArray.getJSONObject("volumeInfo");
                    ArrayList<String> authors = new ArrayList<>();

                    JSONArray isbnArray = volumeInfo.getJSONArray("industryIdentifiers");
                    id = getIsbn10(isbnArray);
                    id = formatIsbn10(id);

                    isbn10 = getIsbn10(isbnArray);
                    publishProgress(25);

                    title = volumeInfo.getString("title");

                    try {
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authorsArray.length(); j++) {
                            String author = authorsArray.getString(j);
                            Log.e("AUTHOR IS ", author);
                            authors.add(author);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    StringBuilder unformattedYear = new StringBuilder(volumeInfo.getString("publishedDate"));
                    year = String.valueOf(unformattedYear.subSequence(0, 4));

                    description = volumeInfo.getString("description");
                    publishProgress(50);

                    try {
                        JSONArray genresArray = volumeInfo.getJSONArray("categories");
                        genre = genresArray.getString(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Log.e("PAGECOUNT", volumeInfo.getString("pageCount"));
                        pageCount = volumeInfo.getString("pageCount");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONObject thumbnail = volumeInfo.getJSONObject("imageLinks");
                    bookThumbnailName = thumbnail.getString("smallThumbnail");
                    downloadThumbnail(bookThumbnailName);
                    publishProgress(75);
                    bookThumbnailName = formatThumbnailName(bookThumbnailName);
                    saveThumbnailLocally(bookThumbnailName);
                    // Add book to Arraylist
                    Book book = new ConcreteBookBuilder().create()
                            .id(Long.valueOf(id))
                            .isbn10(isbn10)
                            .title(title)
                            .authors(authors)
                            .year(year)
                            .description(description)
                            .genre(genre)
                            .pageCount(pageCount)
                            .pictureName(bookThumbnailName)
                            .build();

                    bookArrayList.add(book);
                    publishProgress(100);
                }
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Done";
        }

        /**
         * Downloads the Book image that will be used in the SearchDetailsFragment class.
         *
         * @param thumbnailStr the image's name
         */
        public void downloadThumbnail(String thumbnailStr) {
            try {
                URL url = new URL(thumbnailStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    bookThumbnail = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        /**
         * Saves the image to the local application storage
         *
         * @param bookThumbnailStr the image's name
         */
        public void saveThumbnailLocally(String bookThumbnailStr) {
            try {
                FileOutputStream outputStream = openFileOutput(bookThumbnailStr, Context.MODE_PRIVATE);
                bookThumbnail.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Removes path separators from the image name. Iterates through the name, starting from
         * the last character, keeping all characters until a path separator is reached.
         *
         * @param thumbnail the image's name
         * @return the image name without path separators
         */
        public String formatThumbnailName(String thumbnail) {
            StringBuilder sb = new StringBuilder();
            int index = thumbnail.length() - 1;
            while (thumbnail.charAt(index) != '/') {
                sb.append(thumbnail.charAt(index));
                index--;
            }
            return sb.reverse().toString();
        }

        public String getIsbn10(JSONArray isbnArray) {
            String isbn10 = null;
            for(int i = 0; i < isbnArray.length(); i++) {
                try {
                    JSONObject isbnObject = isbnArray.getJSONObject(i);
                    String isbn = isbnObject.getString("identifier");
                    if(isbn.length() == 10) {
                        isbn10 = isbn;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return isbn10;
        }

        public String formatIsbn10(String isbn10) {
            StringBuilder newIsbn = new StringBuilder();
            for(int i = 0; i < isbn10.length(); i++) {
                if (Character.isDigit(isbn10.charAt(i))) {
                    newIsbn.append(isbn10.charAt(i));
                }
            }
            Log.e("ISBN IS ", newIsbn.toString());
            return newIsbn.toString();
        }

        /**
         * Makes the progress bar visible. Informed by the code and information provided
         * in [1], [2], and [3].
         *
         * @param value the program's current progress
         */
        @Override
        protected void onProgressUpdate(Integer... value) {
            ProgressBar progressBar = findViewById(R.id.progress_bar_2);
            // set progress bar to visible
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        /**
         * Sets the view's contents once doInBackground() is finished. Progress bar becomes
         * invisible and view is updated to display the results.
         *
         * @param fromDoInBackground the String returned by doInBackground()
         */
        protected void onPostExecute(String fromDoInBackground) {
            ProgressBar progressBar = findViewById(R.id.progress_bar_2);
            progressBar.setVisibility(View.INVISIBLE);
            // Update the view
            bookListAdapter.notifyDataSetChanged();
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
            if(fileExists(pictureName)) {
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
         * Determines whether a picture is saved in local storage
         * @param fileName the picture's name
         * @return true if picture was found
         */
        // Checks if the file exists in the local storage directory
        public boolean fileExists(String fileName) {
            Log.e("CUSTOM ADAPTER: ", "Image was found locally");
            File file = parentActivity.getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }
    }
}

// References cited:
// [1] E. Torunski, "Week 11- Toolbar and Navigation Drawer," Algonquin College, Jul. 28, 2022.
// [2] F. Azzola, "Android Listview with multiple row layout," Java Code Geeks, Aug. 12, 2014. https://www.javacodegeeks.com/2014/08/android-listview-with-multiple-row-layout.html (accessed Jun. 25, 2022).
// [3] E. Torunski, "Week6_AsyncTask_Files_XML - 22S_CST2335_010 Mobile Graphical Interface Prog.," Algonquin College, Jul. 14, 2022. https://brightspace.algonquincollege.com/d2l/le/content/444410/viewContent/7030232/View (accessed Jul. 17, 2022).