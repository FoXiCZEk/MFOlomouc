package cz.foxiczek.mfolomouc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "MyTeam";
    private static final String TABLE_NAME = "TeamStats";
    private static final String KEY_ID = "ID";
    private static final String KEY_GOLY = "GOLY";
    private static final String KEY_NAME = "NAME";
    private static final String KEY_DRES = "DRES";
    private static final String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME +"(" + KEY_ID + " INTEGER PRIMARY KEY ASC," + KEY_NAME + " TEXT NOT NULL," + KEY_DRES + " INTEGER," + KEY_GOLY + " INTEGER)";
    private static final String CREATE_INDEX = "CREATE INDEX TeamStatsID on "+ TABLE_NAME+"(ID)";
    private static final String TABLE_ZAPASY = "Zapasy";
    private static final String KEY_ZAPASY_ID = "ID_ZAPASU";
    private static final String KEY_ZAPASY_ZAPAS = "ZAPAS";
    private static final String KEY_ZAPAS_DATUM = "DATUM";
    private static final String KEY_ZAPAS_OBDOBI = "OBDOBI";
    private static final String CREATE_TABLE_ZAPASY = "CREATE TABLE " + TABLE_ZAPASY +"(" + KEY_ZAPASY_ID + " INTEGER PRIMARY KEY ASC," + KEY_ZAPASY_ZAPAS +" TEXT NOT NULL, "+ KEY_ZAPAS_DATUM +" TEXT, "+ KEY_ZAPAS_OBDOBI+" TEXT)";
    private static final String CREATE_INDEX_ZAPASY = "CREATE INDEX ZapasyID on " + TABLE_ZAPASY+"("+  KEY_ZAPASY_ID +")";
    private static final String TABLE_SCORE = "SCORE";
    private static final String KEY_SCORE_ID_ZAPASU = "ID_ZAPASU";
    private static final String KEY_SCORE_ID_HRACE = "ID_HRACE";
    private static final String KEY_SCORE_GOLY = "GOLY";
    private static final String CREATE_TABLE_SCORE = "CREATE TABLE " + TABLE_SCORE + "(" + KEY_SCORE_ID_ZAPASU + " INTEGER NOT NULL," + KEY_SCORE_ID_HRACE + " INTEGER NOT NULL," + KEY_SCORE_GOLY + " INTEGER, FOREIGN KEY (" + KEY_SCORE_ID_ZAPASU + ") REFERENCES " + TABLE_ZAPASY + "(" + KEY_ZAPASY_ID + "),FOREIGN KEY (" + KEY_SCORE_ID_HRACE + ") REFERENCES " + TABLE_NAME + "(" + KEY_ID + "))";
    private static final String VIEW_STATS_NAME = "myStats";
    private static final String CREATE_VIEW_STATS = "CREATE VIEW " + VIEW_STATS_NAME + " AS SELECT " + TABLE_NAME + "." + KEY_NAME + " AS NAME, " + TABLE_ZAPASY + "." + KEY_ZAPASY_ZAPAS + " AS ZAPAS, " + TABLE_SCORE + "." + KEY_SCORE_GOLY + " AS GOLY FROM " + TABLE_SCORE + " INNER JOIN " + TABLE_ZAPASY + " ON " + TABLE_ZAPASY + "." + KEY_ZAPASY_ID + " = " + TABLE_SCORE + "." + KEY_SCORE_ID_ZAPASU + " INNER JOIN " + TABLE_NAME + " ON " + TABLE_NAME + "." + KEY_ID + " = " + TABLE_SCORE + "." + KEY_SCORE_ID_HRACE;


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_INDEX);
        db.execSQL(CREATE_TABLE_ZAPASY);
        db.execSQL(CREATE_INDEX_ZAPASY);
        db.execSQL(CREATE_TABLE_SCORE);
        db.execSQL(CREATE_VIEW_STATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZAPASY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        db.execSQL("DROP VIEW IF EXISTS " + VIEW_STATS_NAME);
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_INDEX);
        db.execSQL(CREATE_TABLE_ZAPASY);
        db.execSQL(CREATE_INDEX_ZAPASY);
        db.execSQL(CREATE_TABLE_SCORE);
        db.execSQL(CREATE_VIEW_STATS);
    }

    public Cursor getAllPlayers() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void addPlayer(String name, int dres) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DRES, dres);
        values.put(KEY_GOLY, 0);
        db.insert(TABLE_NAME, null, values);
        System.err.println("DEBUG : SQLITE insert = " + name + " - " + dres);
    }

    public void removePlayer(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] {String.valueOf(id)});
        db.delete(TABLE_SCORE, KEY_SCORE_ID_HRACE + " = ?", new String[] {String.valueOf(id)});
        db.close();
    }

    public void updatePlayer(int id,String newName,int newDres){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,newName);
        values.put(KEY_DRES,newDres);
        db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] {String.valueOf(id)});

    }

    public void updateGoal(int id, int newValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GOLY, newValue);
        db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] {String.valueOf(id)});
    }

    public void insertZapas(String zapas, String datum, String obdobi){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ZAPASY_ZAPAS, zapas);
        values.put(KEY_ZAPAS_DATUM, datum);
        values.put(KEY_ZAPAS_OBDOBI, obdobi);
        db.insert(TABLE_ZAPASY, null, values);
        db.close();
    }

    public Cursor getZapasy(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ZAPASY;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public int getCountZapasy(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor count = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ZAPASY, null);
        count.moveToFirst();
        return Integer.valueOf(count.getString(0));

    }


    public void clearZapasy(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZAPASY);
        db.execSQL(CREATE_TABLE_ZAPASY);
        db.execSQL(CREATE_INDEX_ZAPASY);

    }

    public void updateGoly(int ID_Hrace, int ID_Zapasu, int goly){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SCORE_ID_ZAPASU, ID_Zapasu);
        values.put(KEY_SCORE_ID_HRACE, ID_Hrace);
        values.put(KEY_SCORE_GOLY, goly);
        Cursor check = db.rawQuery("SELECT * FROM SCORE WHERE ID_HRACE = " + ID_Hrace + " AND ID_ZAPASU = " + ID_Zapasu, null);
        if(check.getCount() == 0){
            db.insert(TABLE_SCORE,null,values);
        }else {
            db.update(TABLE_SCORE, values, KEY_SCORE_ID_ZAPASU + " = ? AND " + KEY_SCORE_ID_HRACE + " = ?", new String[]{String.valueOf(ID_Zapasu), String.valueOf(ID_Hrace)});
        }
    }

    public Cursor getHracZapas(int ID_HRACE, int ID_ZAPASU){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SCORE_ID_HRACE, ID_HRACE);
        values.put(KEY_SCORE_ID_ZAPASU, ID_ZAPASU);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORE + " WHERE ID_ZAPASU = ? AND ID_HRACE = ?", new String[]{String.valueOf(ID_ZAPASU), String.valueOf(ID_HRACE)});
        return cursor;
    }

    public Cursor getHracTotalGoly(int ID_HRACE){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(GOLY) AS GOLY FROM " + TABLE_SCORE + " WHERE ID_HRACE = ?", new String[]{String.valueOf(ID_HRACE)});
        return cursor;
    }

    public void clearScoreTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        db.execSQL(CREATE_TABLE_SCORE);
    }

    public boolean checkFavTeam(String team){
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        if(getCountZapasy() > 0) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ZAPASY + " WHERE ZAPAS LIKE '%" + team + "%'", null);
            while(cursor.moveToNext()){
                System.err.println("DEBUG : zapas = " + cursor.getString(1));
                if(cursor.getString(1).contains(team)){
                    count = count + 1;
                }
            }
            cursor.close();
            if(count >= 12){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }

    }


    public int getPlayedMatchesCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DATUM FROM " + TABLE_ZAPASY,null);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        Date matchDate, ted;
        int count = 0;
        while(cursor.moveToNext()) {
            try {
                matchDate = df.parse(cursor.getString(0));
                ted = df.parse(timeStamp);
                if (matchDate.compareTo(ted) < 0 || matchDate.compareTo(ted) == 0) {
                    count++;
                }
            } catch (ParseException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return count;

    }


}
