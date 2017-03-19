package laurenzsoft.com.efficiencymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Laurenz on 21.02.2017.
 */

public class DataBaseHolder extends SQLiteOpenHelper{
    //The database should have 4 Tables: Books, Projects, Comments and LeisureTime
    private static final String DATABASENAME = "EfficiencyDB.db";
    private static final String TABLEBOOKS = "books";
    private static final String TABLECOMMENTS = "comments";
    private static final String TABLEPROJECTS = "projects";
    private static final String TABLELEISURETIME = "leisuretime";
    private static final String TABLESESSIONS = "sessions";

    public static final String ID = "_id";
    public static final String PAGE = "page";
    public static final String TIME = "time";
    public static final String TEXT = "text";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String CONCENTRATION = "concentration";
    public static final String PROJECT = "project";
    public static final String STATE ="state";


    private static final int DATABASEVERSION = 1;

    private static DataBaseHolder instance;
    private DataBaseHolder(Context context) {
       super(context, DATABASENAME, null, DATABASEVERSION);

    }
    public static DataBaseHolder getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseHolder(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createBookTable = "CREATE TABLE " + TABLEBOOKS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + PAGE + " INTEGER, " + TIME + " INTEGER)";
        String createCommentTable = "CREATE TABLE " + TABLECOMMENTS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + TEXT + " TEXT, " + DATE + " INTEGER, " + CONCENTRATION + " INTEGER, " + PROJECT + " INTEGER)";
        String createLeisuretimeTable = "CREATE TABLE " + TABLELEISURETIME + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  + DATE + " INTEGER, " + TIME + " INTEGER, " + STATE + " INTEGER)";
        String createProjectsTable = "CREATE TABLE " + TABLEPROJECTS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + TIME + " INTEGER)";
        String createSessionTable = "CREATE TABLE " + TABLESESSIONS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " INTEGER, " + TIME + " INTEGER, " + PROJECT + " INTEGER)";

        sqLiteDatabase.execSQL(createBookTable);
        sqLiteDatabase.execSQL(createCommentTable);
        sqLiteDatabase.execSQL(createLeisuretimeTable);
        sqLiteDatabase.execSQL(createProjectsTable);
        sqLiteDatabase.execSQL(createSessionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w("DATABASE", "Upgrade auf neue Version, alte Daten werden gelöscht");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLECOMMENTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLEBOOKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLEPROJECTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLELEISURETIME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLESESSIONS);
        onCreate(sqLiteDatabase);
    }

    public long addComment(String title, String text, long date, int concentration, int projectID) {
        long rowId = -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TEXT, text);
        values.put(TITLE, title);
        values.put(DATE, date);
        values.put(CONCENTRATION, concentration);
        values.put(PROJECT, projectID);
        rowId = db.insert(TABLECOMMENTS, null, values);
        return rowId;
    }
    public long addProject(String title) {
        long rowId = -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(TIME, 0);
        rowId = db.insert(TABLEPROJECTS, null, values);
        return rowId;
    }
    public long addLeisureTime(long date, long time, int state) {
        long rowId = -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE, date);
        values.put(TIME, time);
        values.put(STATE, state);
        rowId = db.insert(TABLELEISURETIME, null, values);
        return rowId;
    }
    public long addBook(String title) {
        long rowId = -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(PAGE, 0);
        values.put(TIME, 0);
        rowId = db.insert(TABLEBOOKS, null, values);
        return rowId;
    }
    public int addSession(long date, long time, int projectID) {
        long rowId = -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE, date);
        values.put(TIME, time);
        values.put(PROJECT, projectID);
        rowId = db.insert(TABLESESSIONS, null, values);
        return (int) rowId;
    }
    public void setPage(int bookId, int page) {
        SQLiteDatabase db = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAGE, page);
        db.update(TABLEBOOKS, values, ID + " = ?", new String[] {String.valueOf(bookId)});
    }
    public void addTimeToProject(int projectID, long time) {
        SQLiteDatabase db = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, time);
        db.update(TABLEPROJECTS, values, ID + " = ?", new String[] {String.valueOf(projectID)});
    }
    public boolean containsProject(String name) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEPROJECTS, new String[] {ID}, TITLE + " = ?" , new String[] {name},null,null,null);
        boolean contains = c.getCount() > 0;
        c.close();
        return contains;
    }
    public boolean containsBook(String name) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEBOOKS, new String[] {ID}, TITLE + " = ?" , new String[] {name},null,null,null);
        boolean contains = c.getCount() > 0;
        c.close();
        return contains;
    }
    public Cursor getProjects() {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEPROJECTS, new String[] {ID, TITLE}, null, null,null,null,null);
        return c;
    }
    public Cursor getBooks() {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEBOOKS, new String[] {ID, TITLE}, null, null,null,null,null);
        return c;
    }
    public Cursor getComments(int projectID) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLECOMMENTS, new String[] {ID, TITLE, DATE}, PROJECT + " = ?" , new String[] {String.valueOf(projectID)},null,null,null);
        return c;
    }
    public Cursor getCommentText(int projectID, String title, long date) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLECOMMENTS, new String[] {ID, TEXT, CONCENTRATION}, PROJECT + " = ? AND " + TITLE + " = ? AND " + DATE + " = ?" , new String[] {String.valueOf(projectID), title, String.valueOf(date)},null,null,null);
        return c;
    }
    public String getProjectName(int projectID) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEPROJECTS, new String[] {TITLE}, ID + " = ?" , new String[] {String.valueOf(projectID)},null,null,null);
       c.moveToFirst();
        String name = c.getString(0);
        c.close();
       return name;
    }
    public String getBookName(int projectID) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEBOOKS, new String[] {TITLE}, ID + " = ?" , new String[] {String.valueOf(projectID)},null,null,null);
        c.moveToFirst();
        String name = c.getString(0);
        c.close();
        return name;
    }
    public int getPage(int bookId) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEBOOKS, new String[] {PAGE}, ID + " = ?" , new String[] {String.valueOf(bookId)},null,null,null);
        c.moveToFirst();
        int page = c.getInt(0);
        c.close();
        return page;
    }
    public int getBookId(String name) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLEBOOKS, new String[] {ID}, TITLE + " = ?" , new String[] {name},null,null,null);
        int id = c.getInt(0);
        c.close();
        return  id;
    }
    public long getSessionTime(long startDate, long endDate) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLESESSIONS, new String[] {TIME}, DATE + " < ? AND " + DATE + " > ?" , new String[] {String.valueOf(endDate), String.valueOf(startDate)},null,null,null);
        c.moveToFirst();
        if (c.isAfterLast() || c.isBeforeFirst() || c.getCount() == 0) return 0;
        int sum = 0;
        while (!c.isLast())  {
            sum += c.getInt(0);
            c.moveToNext();
        }
        c.close();
        return sum;
    }
    public int getSessionAmount(long startDate, long endDate) {
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLESESSIONS, new String[] {ID}, DATE + " < ? AND " + DATE + " > ?" , new String[] {String.valueOf(endDate), String.valueOf(startDate)},null,null,null);
        int len = c.getCount();
        c.close();
        return len;
    }
    public int getMinutesLeisure(long startDate, long endDate, int state) { //LeisureTime.BOOKSTATE or LeisureTime.YOUTUBE
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.query(TABLELEISURETIME, new String[] {TIME}, DATE + " < ? AND " + DATE + " > ? AND " + STATE + " = ?" , new String[] {String.valueOf(endDate), String.valueOf(startDate), String.valueOf(state)},null,null,null);
        c.moveToFirst();
        if (c.isAfterLast() || c.isBeforeFirst() || c.getCount() == 0) return 0;
        int sum = 0;
        while (!c.isLast())  {
            sum += c.getInt(0);
            c.moveToNext();
        }
        c.close();
        return sum;
    }

}
