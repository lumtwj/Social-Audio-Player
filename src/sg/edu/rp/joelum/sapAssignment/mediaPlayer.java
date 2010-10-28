package sg.edu.rp.joelum.sapAssignment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class mediaPlayer extends Activity implements OnCompletionListener, OnSeekBarChangeListener {
	Notification notification;
	NotificationManager mNotificationManager;
	ImageButton start, library, web, backward, forward;
	TextView tvCurrentPos, tvSongDur, tvSongTitle, tvSongArtist, tvSongAlbum;
	ImageView ivAlbumArt;
	SeekBar nowPlaying;
	MediaPlayer mp;
	int total, CurrentPosition, durationLeft, getPosition;
	Thread thread;
	boolean playing = true;
	String getDirectory, getId, getAlbum, getArtist, getTitle, getAlbumArt;
	ConnectivityManager checkIC;
	Cursor musiccursor;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicplayer);
        
        checkIC =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        
        start = (ImageButton) findViewById(R.id.playPause);
        library = (ImageButton) findViewById(R.id.library);
        web = (ImageButton) findViewById(R.id.webInfo);
        backward = (ImageButton) findViewById(R.id.backward);
        forward = (ImageButton) findViewById(R.id.forward);
        
        tvCurrentPos = (TextView) findViewById(R.id.currentPos);
        tvSongDur = (TextView) findViewById(R.id.songDuration);
        tvSongTitle = (TextView) findViewById(R.id.songTitle);
        tvSongArtist = (TextView) findViewById(R.id.songArtist);
        tvSongAlbum = (TextView) findViewById(R.id.songAlbum);
        ivAlbumArt = (ImageView) findViewById(R.id.albumArt);
                
        mp = new MediaPlayer();
		
        nowPlaying = (SeekBar)findViewById(R.id.playingSeek);
        nowPlaying.setOnSeekBarChangeListener(this);
        nowPlaying.setEnabled(false);
        
        getFromLibrary();
        initialiseMusicPlayer(getDirectory);
        startPlaying();
        
        start.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		if(playing == false) {
           			startPlaying();
           		}
           		else {
           			pausePlaying();
           		}
           	}
        });
        
        mp.setOnCompletionListener(this);
        
        library.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		stopPlaying();
           		finish();
           	}
        });
        
        web.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		Intent intentNewAct = new Intent(getBaseContext(), webService.class);
           		startActivity(intentNewAct);
           	}
        });
        
        backward.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		getPosition = getPosition - 1;
           		stopUpdateSongPosition();
        		getNextSong(getPosition);
        		startPlaying();
           	}
        });
        
        forward.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
				getPosition = getPosition + 1;
				stopUpdateSongPosition();
				getNextSong(getPosition);
				startPlaying();
           	}
        });
    }

	@Override
	public void onCompletion(MediaPlayer arg0) {
		playing = false;
		stopUpdateSongPosition();
		nowPlaying.setProgress(0);
		tvCurrentPos.setText("0:00");
		tvSongDur.setText("-" + convertMillis(total));
		
		getPosition = getPosition + 1;
		getNextSong(getPosition);
		startPlaying();
	}
	
	public String convertMillis(int Millis){
		int minutes = (Millis%(1000*60*60))/(1000*60);
		int seconds = ((Millis%(1000*60*60))%(1000*60))/1000;
		String convert = String.format("%d:%02d", minutes, seconds);
	
		return convert;
	}
	
	private Runnable updatePlayerInfo = new Runnable() {
	    @Override
	    public void run() {
	        CurrentPosition= 0;
	        total = mp.getDuration();
	        nowPlaying.setMax(total);
	        while(mp != null && CurrentPosition<total){
	            try {
	                Thread.sleep(900);
	                CurrentPosition= mp.getCurrentPosition();
                	durationLeft = total - CurrentPosition;
                	
	            } catch (InterruptedException e) {
	                return;
	            } catch (Exception e){
	                return;
	            }            
	            nowPlaying.setProgress(CurrentPosition);
		        mpHandler.sendEmptyMessage(0);
	        }
	    }
	};
	
	private Handler mpHandler = new Handler(){
    	@Override
		public void handleMessage(Message msg) {
    		songLocation(CurrentPosition, durationLeft);
    	}
	};

	
    public void updateSongPosition() {
		thread = new Thread(updatePlayerInfo, "updatePlayerInfo");
		thread.start();
    }
    
	public void stopUpdateSongPosition() {
		thread.interrupt();
	}
	
	public void songLocation(int position, int durationLeft) {
   		tvCurrentPos.setText(convertMillis(position));
   		tvSongDur.setText("-" + convertMillis(durationLeft));
	}
	
	public void getFromLibrary() {
		getId = getIntent().getStringExtra("id");
    	getTitle = getIntent().getStringExtra("title");
    	getArtist = getIntent().getStringExtra("artist");
    	getAlbum = getIntent().getStringExtra("album");
    	getDirectory = getIntent().getStringExtra("directory");
    	getAlbumArt = getIntent().getStringExtra("albumArt");
    	getPosition = getIntent().getIntExtra("position", 0);
    	
    	setMeta(getTitle, getArtist, getAlbum, getAlbumArt);
    	
       	DBAdapter dbconn = new DBAdapter(getApplicationContext());
       	
    	dbconn.open();
    	dbconn.insertMusic(getTitle, getArtist, getAlbum, getAlbumArt, getDirectory);
    	dbconn.close();
    	
        ConnectivityManager checkIC =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if(checkIC.getNetworkInfo(0).isAvailable() == false) {
        	Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
        else {
        	submitSongToServer(getTitle, getArtist, getAlbum);
        }
	}
	// TODO Auto-generated constructor stub
	public void setMeta(String title, String artist, String album, String albumart) {
    	tvSongTitle.setText(title);
    	tvSongArtist.setText(artist);
    	tvSongAlbum.setText(album);
    	ivAlbumArt.setImageURI(Uri.parse(albumart));
    	
    	notifyBar(title, artist);
	}
	
	public void startPlaying() {
		playing = true;
		mp.start();
		nowPlaying.setEnabled(true);
		updateSongPosition();
		start.setImageResource(R.drawable.ic_pause);
	}
	
	public void pausePlaying() {
			playing = false;
   			mp.pause();
   			stopUpdateSongPosition();
   			start.setImageResource(R.drawable.ic_play);
	}
	
	public void stopPlaying() {
   		mp.stop();
   		mp.reset();
	}
	
	public void initialiseMusicPlayer(String dir) {
        try {
        	mp.reset();
			mp.setDataSource(dir);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int position, boolean arg2) {
		if(seekBar.isPressed()) {
			int durationLeft = mp.getDuration() - position; 
			songLocation(position, durationLeft);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		stopUpdateSongPosition();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mp.seekTo(seekBar.getProgress());
		if(mp.isPlaying()) {
			updateSongPosition();
		}
	}
	
	public void submitSongToServer(String song, String artist, String album) {
		URL url;
		
		song = song.replace(" ", "%20");
		artist = artist.replace(" ", "%20");
		album = album.replace(" ", "%20");
		
        try{
          	String StringUrl="http://sit.rp.edu.sg/c345/submitsong.php?apikey=ff9a1a4c828de747dd9b2e4ab931101a2b8eb006&song=" + song + "&artist=" + artist + "&album=" + album;
          	
          	url = new URL(StringUrl);
          	
          	HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

          	int responseCode = httpConnection.getResponseCode();
          		
            if (responseCode == HttpURLConnection.HTTP_OK){
            	InputStream in = httpConnection.getInputStream();
          			
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          		DocumentBuilder db = dbf.newDocumentBuilder();
          		
          		Document dom = db.parse(in);

          		Element docEle = dom.getDocumentElement();
          		
          		NodeList nl = docEle.getElementsByTagName("channel");

          		if (nl != null && nl.getLength() > 0) {
          			for (int i = 0 ; i < nl.getLength(); i++) {
          				Element entry = (Element) nl.item(i);

          				Element result = (Element) entry.getElementsByTagName("result").item(0);

          				String dispResult = result.getFirstChild().getNodeValue();
          				Log.d("Result: ", dispResult);
          			}
          		}
          	}
	  	}
		catch (MalformedURLException e) {
	  		e.printStackTrace();
	  	} 
		catch (IOException e) {
	  		e.printStackTrace();
	  	}
		catch (ParserConfigurationException e) {
	  		e.printStackTrace();
	  	}
		catch (SAXException e) {
	  		e.printStackTrace();
	  	}
	  	finally {					
	  						
	  	}
	}
	// TODO Auto-generated constructor stub
    public void getNextSong(int position) {
    	String determinePlayList = "is ";
    	determinePlayList += getIntent().getStringExtra("playlist");
    	int pl = getIntent().getIntExtra("playlistID", 0);

    	Uri tracks = Uri.parse("content://media/external/audio/playlists/" + pl + "/members");
    	
        String [] audio = {
        		MediaStore.Audio.Media.ALBUM_ID,
        		MediaStore.Audio.Media._ID, 
        		MediaStore.Audio.Media.TITLE, 
        		MediaStore.Audio.Media.ARTIST,
        		MediaStore.Audio.Media.ALBUM,
        		MediaStore.Audio.Media.DATA};
        
        System.gc();
        
        if (determinePlayList.equals("is playlist")) {
            musiccursor = managedQuery(
                	tracks,
                	audio, 
                	null, 
                	null,
                	MediaStore.Audio.Playlists.Members.AUDIO_ID);
        } else {
            musiccursor = managedQuery(
    	        	MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    	        	audio, 
    	        	null, 
    	        	null,
    	        	MediaStore.Audio.Media.TITLE);
        }

        if(position >= musiccursor.getCount()) {
        	position = 0;
        	getPosition = 0;
        }
        
        if(position < 0) {
        	position = musiccursor.getCount() - 1;
        	getPosition = musiccursor.getCount() - 1;
        }
        
        if (musiccursor.moveToPosition(position)) {
        	getFromCursor();
        } 

        musiccursor.close();
    }
	// TODO Auto-generated constructor stub
    public void getFromCursor() {
    	int albumIdCol = musiccursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
    	String albumId = musiccursor.getString(albumIdCol);
    	
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
        
        setMeta(title, artist, album, ""+uri);
        initialiseMusicPlayer(fileLoc);
        
        if(checkIC.getNetworkInfo(0).isAvailable() == true) {
        	submitSongToServer(title, artist, album);
        }
        
        /*
        //Miss out before submission date
        DBAdapter dbconn = new DBAdapter(getApplicationContext());
        
    	dbconn.open();
    	dbconn.insertMusic(title, artist, album, ""+uri, fileLoc);
    	dbconn.close();
        */
    	
        Log.d("Music directory", fileLoc);
        Log.d("Cursor Position", "" + musiccursor.getPosition());
    }
    
    public void notifyBar(String title, String artist) {
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        
    	if(mNotificationManager.equals(null)) {
    		
    	}
    	else {
    		mNotificationManager.cancel(1);
    	}
        
        int icon = R.drawable.disc;        // icon from resources
        CharSequence tickerText = "Now Playing.. " + title;              // ticker-text
        long when = System.currentTimeMillis();         // notification time
        Context context = getApplicationContext();      // application Context
        CharSequence contentTitle = "SAP Now Playing: ";  // expanded message title
        CharSequence contentText = title + " by " + artist;      // expanded message text

        Intent notificationIntent = new Intent(this, mediaPlayer.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // the next two lines initialize the Notification, using the configurations above
        notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        
        mNotificationManager.notify(1, notification);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	mNotificationManager.cancel(1);
    	if(mp.isPlaying()) {
    		stopPlaying();
    	}
    	finish();
    }
}