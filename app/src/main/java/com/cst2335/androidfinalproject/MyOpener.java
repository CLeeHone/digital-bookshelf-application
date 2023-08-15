package com.cst2335.androidfinalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Accesses the database containing a user's favourite Cocktail recipes.
 * Taken from [1] and informed by [2].
 */
public class MyOpener extends SQLiteOpenHelper {

    // Instance variables representing the database's column names and information
    /** {@value} The SQLite database's name **/
    protected final static String DATABASE_NAME = "BooksDB";
    /** {@value} The database's version number **/
    protected final static int VERSION_NUM = 1;
    /** {@value} The Book table's name **/
    public final static String BOOK_TABLE = "BOOKS";
    /** {@value} A Book instance's unique database id (primary key) **/
    public final static String COL_ID = "_id";
    /** {@value} A Book instance's unique ISBN-10 code */
    public final static String COL_ISBN = "ISBN";
    /** {@value} A Book instance's name **/
    public final static String COL_TITLE = "BOOK_TITLE";
    /** {@value} A Book instance's instructions **/
    public final static String COL_AUTHOR = "AUTHOR";
    /** {@value} A Book instance's first ingredient **/
    public final static String COL_YEAR = "YEAR";
    /** {@value} A Book instance's second ingredient **/
    public final static String COL_DESCRIPTION = "DESCRIPTION";
    /** {@value} A Book instance total page count */
    public final static String COL_PAGE_COUNT = "PAGE_COUNT";
    /** {@value} A Book instance's third ingredient **/
    public final static String COL_GENRE = "GENRE";
    /** {@value} A Book instance's picture name. Used to retrieve picture from local storage **/
    public final static String COL_PICTURE = "PICTURE";
    /** {@value} The Author table's name */
    public final static String AUTHOR_TABLE = "AUTHOR";
    public final static String COL_NAME = "NAME";
    public final static String BOOK_AUTHOR_TABLE = "BOOK_AUTHOR";
    public final static String COL_AUTHOR_FK = "_id";
    public final static String COL_BOOK_FK = "_id";
    /** {@value} The default rating for a book **/
    public final static String COL_RATING = "RATING";

    /**
     * Parameterized constructor passes parameters to its super-constructor
     * @param ctx the context passed to the super-constructor
     */
    public MyOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * Gets called if no database file exists.
     * @param db the local SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BOOK_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ISBN + " text,"
                + COL_TITLE + " text,"
                + COL_YEAR + " text,"
                + COL_DESCRIPTION + " text,"
                + COL_PAGE_COUNT + " text,"
                + COL_GENRE + " text,"
                + COL_RATING + " text,"
                + COL_PICTURE + " text);"); // add or remove columns

        //Can call SELECT * FROM AUTHOR_TABLE WHERE COL_AUTHOR_ =
        db.execSQL("CREATE TABLE " + AUTHOR_TABLE + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " text,"
                + COL_ISBN + " text)");

        // Associative table to resolve m:m relationship
//        db.execSQL("CREATE TABLE " + BOOK_AUTHOR_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + "FOREIGN KEY (" + COL_AUTHOR_FK + ") REFERENCES " + AUTHOR_TABLE + "(_id),"
//                + "FOREIGN KEY (" + COL_BOOK_FK + ") REFERENCES " + BOOK_TABLE + "(ISBN));");
    }

    /**
     * Gets called if the database version on device is lower than VERSION_NUM
     * @param db the local SQLite database
     * @param oldVersion the database's old version
     * @param newVersion the database's new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + BOOK_TABLE);
        //Create the new table:
        onCreate(db);
    }

    /**
     * Gets called if the database version on device is higher than VERSION_NUM.
     * @param db the local SQLite database
     * @param oldVersion the database's old version
     * @param newVersion the database's new version
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + BOOK_TABLE);
        // Create the new table:
        onCreate(db);
    }
}

// Reference cited:
// [1] E. Torunski, "etorunski/InClassExamples_W21 at week5_database," Github, 2020. https://github.com/etorunski/InClassExamples_W21/tree/week5_database (accessed Jul. 13, 2022).
// [2] E. Torunski, "Week5_Android_SQLLite - 22S_CST2335_010 Mobile Graphical Interface Prog." Algonquin College, Jul. 05, 2022. Accessed: Jul. 13, 2022. [Online]. Available: https://brightspace.algonquincollege.com/d2l/le/content/444410/viewContent/7030230/View
