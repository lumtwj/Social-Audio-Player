package sg.edu.rp.joelum.sapAssignment;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Playlists;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


public class sapAssignment extends TabActivity {
	static final private int NEW_PLAYLIST = Menu.FIRST;
	EditText et;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
	    TextView tabDeco = new TextView(this);
	    tabDeco.setText("All songs");
	    tabDeco.setTextSize(16);
	    tabDeco.setTypeface(Typeface.DEFAULT_BOLD);
	    tabDeco.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
	    tabDeco.setBackgroundResource(R.drawable.tab_type);
	    tabDeco.setMinHeight(45);
        
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent().setClass(this, libListView.class);

        spec = tabHost.newTabSpec("1").setIndicator(tabDeco).setContent(intent);
        tabHost.addTab(spec);
        
	    tabDeco = new TextView(this);
	    tabDeco.setText("Playlist");
	    tabDeco.setTextSize(16);
	    tabDeco.setTypeface(Typeface.DEFAULT_BOLD);
	    tabDeco.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
	    tabDeco.setBackgroundResource(R.drawable.tab_type);
	    tabDeco.setMinHeight(45);
        
        intent = new Intent().setClass(this, playlist.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        spec = tabHost.newTabSpec("2").setIndicator(tabDeco).setContent(intent);
        tabHost.addTab(spec);
        
	    tabDeco = new TextView(this);
	    tabDeco.setText("Recently Played");
	    tabDeco.setTextSize(16);
	    tabDeco.setTypeface(Typeface.DEFAULT_BOLD);
	    tabDeco.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
	    tabDeco.setBackgroundResource(R.drawable.tab_type);
	    tabDeco.setMinHeight(45);
        
        intent = new Intent().setClass(this, recentlyPlayedSong.class);

        spec = tabHost.newTabSpec("3").setIndicator(tabDeco).setContent(intent);
        tabHost.addTab(spec);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	// Create and add new menu items.
    	MenuItem restore = menu.add(0, NEW_PLAYLIST, Menu.NONE,
    	R.string.create_playlist);
    	restore.setIcon(R.drawable.ic_add);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
	    switch (item.getItemId()) {
		    case (NEW_PLAYLIST): {
	            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

	            alertbox.setTitle("Create Playlist");
	            
	            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	            View layout = inflater.inflate(R.layout.dialog,
	                                           null);

	            alertbox.setView(layout);
	            
	            et = (EditText)layout.findViewById(R.id.playlistName);
	            
	            alertbox.setNeutralButton("Create", new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface arg0, int arg1) {
	            		String playlistName = "" + et.getText();
	            		createPlaylist(playlistName);
	            		
	            		Toast.makeText(sapAssignment.this, "Playlist sucessfully created", Toast.LENGTH_SHORT).show();
	                }
	            });
	            
	            alertbox.show();
	            
			    return true;
		    }
	    }
    	return false;
    }
    
    public void createPlaylist(String name) {
    	ContentValues values = new ContentValues();
    	
    	values.put(Playlists.NAME, name);
    	
    	getContentResolver().insert(Playlists.EXTERNAL_CONTENT_URI, values);
    }
}