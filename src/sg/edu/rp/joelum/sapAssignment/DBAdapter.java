package sg.edu.rp.joelum.sapAssignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBAdapter {
	public static final String KEY_SONG_ID = "_id";
	public static final String KEY_SONG_TITLE = "title";
	public static final String KEY_SONG_ARTIST = "artist";
	public static final String KEY_SONG_ALBUM = "album";
	public static final String KEY_SONG_ALBUMART = "albumart";
	public static final String KEY_SONG_DIRECTORY = "directory";
	
    private static final String DATABASE_NAME = "sap.db";
    private static final String DATABASE_TABLE = "musicitem";
    private static final int DATABASE_VERSION = 1;
    
	private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + 
		" (" + KEY_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ KEY_SONG_TITLE + " text not null, " 
		+ KEY_SONG_ARTIST + " text not null, " 
		+ KEY_SONG_ALBUM + " text not null, " 
		+ KEY_SONG_ALBUMART + " text not null, " 
		+ KEY_SONG_DIRECTORY + " text not null);";  
    
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    
	public DBAdapter(Context _context) {
		context = _context;
		DBHelper = new DatabaseHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			// TODO Auto-generated method stub
			_db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE);
			onCreate(_db);
		}
	}
	
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		db.close();
	}
	
	public long insertMusic(String title, String artist, String album, String albumart, String directory) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_SONG_TITLE, title);
		cv.put(KEY_SONG_ARTIST, artist);
		cv.put(KEY_SONG_ALBUM, album);
		cv.put(KEY_SONG_ALBUMART, albumart);
		cv.put(KEY_SONG_DIRECTORY, directory);
		
		return db.insert(DATABASE_TABLE, null, cv);
	}
	
    public boolean deleteTitle(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_SONG_ID + "=" + rowId, null) > 0;
    }
    
	public Cursor getAllMusic() {
		return db.query(DATABASE_TABLE, new String[] { 
				KEY_SONG_ID,
				KEY_SONG_TITLE, 	
				KEY_SONG_ARTIST,
				KEY_SONG_ALBUM,
				KEY_SONG_ALBUMART,
				KEY_SONG_DIRECTORY}, null, null, null,
				null, null);
	}
	
	public boolean updateTitle(long rowId, String title, String artist, String album, String albumart, String directory) {
	    ContentValues cv = new ContentValues();
	    cv.put(KEY_SONG_TITLE, title);
	    cv.put(KEY_SONG_ARTIST, artist);
	    cv.put(KEY_SONG_ALBUM, album);
	    cv.put(KEY_SONG_ALBUMART, albumart);
	    cv.put(KEY_SONG_DIRECTORY, directory);
	    return db.update(DATABASE_TABLE, cv, 
	                     KEY_SONG_ID + "=" + rowId, null) > 0;
	}
	
	public Cursor getRecentMusic() {
		return db.query(DATABASE_TABLE, new String[] { 
				KEY_SONG_ID,
				KEY_SONG_TITLE, 	
				KEY_SONG_ARTIST,
				KEY_SONG_ALBUM,
				KEY_SONG_ALBUMART,
				KEY_SONG_DIRECTORY}, null, null, KEY_SONG_TITLE,
				null, "1 DESC", "25");
	}
}
