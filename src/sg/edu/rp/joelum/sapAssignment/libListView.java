package sg.edu.rp.joelum.sapAssignment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class libListView extends ListActivity {
	static final private int ADD_TO_PLAYLIST = Menu.FIRST;
	
	ListView listView;
	ArrayList<Item> musicItem;
	ArrayList<Item> searchSort;
	ArrayList<plItem> playlist;
	EditText find;
	int textLength = 0;
	Cursor musiccursor;
	Cursor plcursor;
	boolean search;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musiclib);
        
        listView = getListView();
        registerForContextMenu(listView);
        
        find = (EditText) findViewById(R.id.search);
        
        musicItem = new ArrayList<Item>();
        searchSort = new ArrayList<Item>();
        playlist = new ArrayList<plItem>();

        if (android.os.Environment.getExternalStorageState().equals  
                (android.os.Environment.MEDIA_MOUNTED)) {
        	retrieveLibrary();
        	
            customListAdapter aa = new customListAdapter(this, R.layout.listview, musicItem);
    	    
    	    aa.notifyDataSetChanged();
    	    
            listView.setAdapter(aa);
        } 
        else {
        	Toast.makeText(this, "No sdcard found", Toast.LENGTH_LONG).show();
        }

        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
		    	Intent intentNewAct = new Intent(getBaseContext(), mediaPlayer.class);
		    	
		    	if (search == false) {
			    	intentNewAct.putExtra("id", musicItem.get(position).getId());
			    	intentNewAct.putExtra("title", musicItem.get(position).getTitle());
			    	intentNewAct.putExtra("artist", musicItem.get(position).getArtist());
			    	intentNewAct.putExtra("album", musicItem.get(position).getAlbum());
			    	intentNewAct.putExtra("albumArt", musicItem.get(position).getAlbumArt());
			    	intentNewAct.putExtra("directory", musicItem.get(position).getFiledir());
			    	intentNewAct.putExtra("position", position);
			    	intentNewAct.putExtra("playlistID", "none");
		    	}
		    	else if(search == true) {
			    	intentNewAct.putExtra("id", searchSort.get(position).getId());
			    	intentNewAct.putExtra("title", searchSort.get(position).getTitle());
			    	intentNewAct.putExtra("artist", searchSort.get(position).getArtist());
			    	intentNewAct.putExtra("album", searchSort.get(position).getAlbum());
			    	intentNewAct.putExtra("albumArt", searchSort.get(position).getAlbumArt());
			    	intentNewAct.putExtra("directory", searchSort.get(position).getFiledir());
			    	intentNewAct.putExtra("position", position);
		    	}
		    	
		    	startActivity(intentNewAct);
			}
        });
        
        find.addTextChangedListener(new TextWatcher() {
        	 public void afterTextChanged(Editable s) {
        	 }

        	 public void beforeTextChanged(CharSequence s, int start, int count,
        	 int after) {
        	 }

        	 public void onTextChanged(CharSequence s, int start, int before,
        	 int count) {
        		 textLength = find.getText().length();
        		 searchSort.clear();
        		 
        		 for (int i = 0; i < musicItem.size(); i++) {
        			 if (textLength <= musicItem.get(i).getTitle().length()) {
        				 if (find.getText().toString().equalsIgnoreCase((String) musicItem.get(i).getTitle().subSequence(0, textLength))) {
        					 searchSort.add(musicItem.get(i));
        				 }
        			 }
        		 }
        		 
        		 	customListAdapter aa2 = new customListAdapter(libListView.this, R.layout.listview, searchSort);
        		    
        		    aa2.notifyDataSetChanged();
        		    
        	        listView.setAdapter(aa2);
        	        
        	        if(find.getText().toString().equals("")) {
        	        	search = false;
        	        }
        	        else {
        	        	search = true;
        	        }
        	 }
        	 });
    }
    
    public void retrieveLibrary() {
        String [] audio = {
        		MediaStore.Audio.Media.ALBUM_ID,
        		MediaStore.Audio.Media._ID, 
        		MediaStore.Audio.Media.TITLE, 
        		MediaStore.Audio.Media.ARTIST,
        		MediaStore.Audio.Media.ALBUM,
        		MediaStore.Audio.Media.DATA};
        
        System.gc();
        
        musiccursor = managedQuery(
        	MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        	audio, 
        	null, 
        	null,
        	MediaStore.Audio.Media.TITLE);
        
        if (musiccursor.moveToFirst()) {
        	addToArrayList();
        } 
        while (musiccursor.moveToNext()) {
        	addToArrayList();
        }

        musiccursor.close();
    }
    
    public void addToArrayList() {
    	int albumIdCol = musiccursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
    	String albumId = musiccursor.getString(albumIdCol);
    	
    	int idCol = musiccursor.getColumnIndex(MediaStore.Audio.Media._ID);
    	String id = musiccursor.getString(idCol);
    	
        int titleCol = musiccursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        String title = musiccursor.getString(titleCol); 

        int artistCol = musiccursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        String artist = musiccursor.getString(artistCol); 
        
        int albumCol = musiccursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        String album = musiccursor.getString(albumCol); 
        
        int fileLocCol = musiccursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        String fileLoc = musiccursor.getString(fileLocCol); 
        
        //Get album art
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(albumId));
        
        
        musicItem.add(new Item(id, title, artist, album, ""+uri, fileLoc));
        
        Log.d("Cursor Position", "" + musiccursor.getPosition());
    }
    
    public void retrievePlaylist() {
        String [] audio = {
        		MediaStore.Audio.Playlists._ID,
        		MediaStore.Audio.Playlists.NAME};
        
        System.gc();
        
        plcursor = managedQuery(
        	MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
        	audio, 
        	null, 
        	null,
        	MediaStore.Audio.Playlists._ID);
        
    	if (plcursor.moveToFirst()) {
        	do {
            	int plIdCol = plcursor.getColumnIndex(MediaStore.Audio.Playlists._ID);
            	String plId = plcursor.getString(plIdCol);
            	
            	int plNameCol = plcursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
            	String plName = plcursor.getString(plNameCol);
            	
            	playlist.add(new plItem(Integer.parseInt(plId), plName));
	        } 
	        while (plcursor.moveToNext());
    	}

        plcursor.close();
    }
    
    
    public void addMusicToPlaylist(String sId, String pId) {
    	Uri tracks = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, Integer.parseInt(pId));
    	
    	ContentValues values = new ContentValues();
    	
    	values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, sId);
    	values.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID, pId);
    	values.put(MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER, "play_order");
    	
    	getContentResolver().insert(tracks, values);  
    	
    	Log.d("tracks uri", ""+tracks);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,
    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Song Option");
        menu.add(0, ADD_TO_PLAYLIST, Menu.NONE, R.string.addSong);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    super.onContextItemSelected(item);
		switch (item.getItemId()) {
			case (ADD_TO_PLAYLIST): {
				AdapterView.AdapterContextMenuInfo menuInfo;
				menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
				final int index = menuInfo.position;
	            
				playlist.clear();
				
				retrievePlaylist();
				
				final CharSequence[] items = new CharSequence[playlist.size()];
				
				for(int i = 0; i < playlist.size(); i++) {
					items[i] = playlist.get(i).getpName();
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Select a playlist");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	addMusicToPlaylist(""+musicItem.get(index).getId(), ""+playlist.get(item).getpId());
				    	
				    	Toast.makeText(libListView.this, "'" + musicItem.get(index).getTitle() + "' added to '" + playlist.get(item).getpName() + "'", Toast.LENGTH_SHORT).show();
				    }
				});
				AlertDialog alert = builder.create();
				
				alert.show();
	            
				return true;
			}
		}
		return false;
    }
}
