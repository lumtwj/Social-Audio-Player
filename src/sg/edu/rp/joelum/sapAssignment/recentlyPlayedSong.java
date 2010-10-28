package sg.edu.rp.joelum.sapAssignment;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class recentlyPlayedSong extends ListActivity {
	ListView lv;
	ArrayList<Item> recentPlayedSong;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recentplayed);
        
    	lv = getListView();
    	
    	recentPlayedSong = new ArrayList<Item>();
    	
    	getRecentPlayedSong();
	    
	    customListAdapter arrayAdapter = new customListAdapter(this, R.layout.listview, recentPlayedSong);
	    
	    arrayAdapter.notifyDataSetChanged();
	    
        lv.setAdapter(arrayAdapter);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
		    	Intent intentNewAct = new Intent(getBaseContext(), mediaPlayer.class);

		    	intentNewAct.putExtra("id", recentPlayedSong.get(position).getId());
		    	intentNewAct.putExtra("title", recentPlayedSong.get(position).getTitle());
		    	intentNewAct.putExtra("artist", recentPlayedSong.get(position).getArtist());
		    	intentNewAct.putExtra("album", recentPlayedSong.get(position).getAlbum());
		    	intentNewAct.putExtra("albumArt", recentPlayedSong.get(position).getAlbumArt());
		    	intentNewAct.putExtra("directory", recentPlayedSong.get(position).getFiledir());
		    	
		    	startActivity(intentNewAct);
			}
        });
    }
    
	public void getRecentPlayedSong() {
		recentPlayedSong.clear();
		
    	DBAdapter dbconn = new DBAdapter(getApplicationContext());
    	
    	dbconn.open();
    	
    	Cursor c = dbconn.getRecentMusic();
        
    	if (c.moveToFirst()){
    	do{
        	int colId = c.getColumnIndex(DBAdapter.KEY_SONG_ID);
        	String id = c.getString(colId);
        	
        	int colTitle = c.getColumnIndex(DBAdapter.KEY_SONG_TITLE);
        	String title = c.getString(colTitle);
            
        	int colArtist = c.getColumnIndex(DBAdapter.KEY_SONG_ARTIST);
        	String artist = c.getString(colArtist);
        	
        	int colAlbum = c.getColumnIndex(DBAdapter.KEY_SONG_ALBUM);
        	String album = c.getString(colAlbum);
        	
        	int colAlbumArt = c.getColumnIndex(DBAdapter.KEY_SONG_ALBUMART);
        	String albumArt = c.getString(colAlbumArt);
        	
        	int colDir = c.getColumnIndex(DBAdapter.KEY_SONG_DIRECTORY);
        	String dir = c.getString(colDir);
        	
        	recentPlayedSong.add(new Item(id, title, artist, album, albumArt, dir));    		
    	}
    	while(c.moveToNext());
    	}
    	
    	c.close();
    	
    	dbconn.close();
	}
}
