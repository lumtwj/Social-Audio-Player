package sg.edu.rp.joelum.sapAssignment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class playlist extends Activity {
	Cursor plcursor;
	Cursor slcursor;
	ArrayList<plItem> playlist = new ArrayList<plItem>();
	ArrayList<Item> songlist = new ArrayList<Item>();
	ArrayList<String> temp = new ArrayList<String>();
	ViewFlipper vf;
	ListView lvPl;
	ListView lvSl;
	Button back;
	int playlistId;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pl);
        
        lvPl = (ListView) findViewById(R.id.lvPl);
        lvSl = (ListView) findViewById(R.id.lvSl);
        vf = (ViewFlipper) findViewById(R.id.vf);
        back = (Button) findViewById(R.id.back);
        
        //vf.setInAnimation(AnimationUtils.makeInAnimation(this, true));
        //vf.setOutAnimation(AnimationUtils.makeOutAnimation(this, true));
        
        retrievePlaylist();
        
	    ArrayAdapter<plItem> arrayAdapter = new ArrayAdapter<plItem>
	    (this,android.R.layout.simple_list_item_1, playlist);
	    
	    arrayAdapter.notifyDataSetChanged();

        lvPl.setAdapter(arrayAdapter);
        
        lvPl.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				vf.setHorizontalFadingEdgeEnabled(true);
				vf.setInAnimation(AnimationUtils.makeInAnimation(playlist.this, false));
				vf.setOutAnimation(AnimationUtils.makeOutAnimation(playlist.this, false));
				vf.showNext();
				
				retrieveSonglist(playlist.get(position).getpId());
				
			    ArrayAdapter<Item> aa = new ArrayAdapter<Item>
			    (playlist.this,android.R.layout.simple_list_item_1, songlist);
			    
			    aa.notifyDataSetChanged();

		        lvSl.setAdapter(aa);
			}
        });
        
        back.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		vf.setHorizontalFadingEdgeEnabled(true);
           		vf.setInAnimation(AnimationUtils.makeInAnimation(playlist.this, true));
           		vf.setOutAnimation(AnimationUtils.makeOutAnimation(playlist.this, true));
           		vf.showPrevious();
           	}
        });
        
        lvSl.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
		    	Intent intentNewAct = new Intent(getBaseContext(), mediaPlayer.class);

		    	intentNewAct.putExtra("id", songlist.get(position).getId());
		    	intentNewAct.putExtra("title", songlist.get(position).getTitle());
		    	intentNewAct.putExtra("artist", songlist.get(position).getArtist());
		    	intentNewAct.putExtra("album", songlist.get(position).getAlbum());
		    	intentNewAct.putExtra("albumArt", songlist.get(position).getAlbumArt());
		    	intentNewAct.putExtra("directory", songlist.get(position).getFiledir());
		    	intentNewAct.putExtra("playlist", "playlist");
		    	intentNewAct.putExtra("playlistID", playlistId);
		    	intentNewAct.putExtra("position", position);
		    	
		    	startActivity(intentNewAct);
			}
        });
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
    
    public void retrieveSonglist(int pl) {
    	songlist.clear();
    	
    	playlistId = pl;
    	
    	Uri tracks = Uri.parse("content://media/external/audio/playlists/" + playlistId + "/members");
    	
        String [] audio = {
            	MediaStore.Audio.Playlists.Members.AUDIO_ID,
        		MediaStore.Audio.Media.ALBUM_ID,
        		MediaStore.Audio.Media.TITLE, 
        		MediaStore.Audio.Media.ARTIST,
        		MediaStore.Audio.Media.ALBUM,
        		MediaStore.Audio.Media.DATA};
        
        slcursor = managedQuery(
            	tracks,
            	audio, 
            	null, 
            	null,
            	MediaStore.Audio.Playlists.Members.AUDIO_ID);
        
    	if (slcursor.moveToFirst()) {
        	do {
            	int aIdCol = slcursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            	String aId = slcursor.getString(aIdCol);  
            	
            	int albumIdCol = slcursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            	String albumId = slcursor.getString(albumIdCol);
            	
                int titleCol = slcursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                String title = slcursor.getString(titleCol); 

                int artistCol = slcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                String artist = slcursor.getString(artistCol); 
                
                int albumCol = slcursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                String album = slcursor.getString(albumCol); 
                
                int fileLocCol = slcursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String fileLoc = slcursor.getString(fileLocCol); 
                
                //Get album art
                Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(albumId));
                
                
                songlist.add(new Item(aId, title, artist, album, ""+uri, fileLoc));
	        } 
	        while (slcursor.moveToNext());
    	}
    }
}
