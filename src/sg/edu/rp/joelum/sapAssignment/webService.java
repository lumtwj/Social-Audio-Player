package sg.edu.rp.joelum.sapAssignment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class webService extends ListActivity implements LocationListener {
	ArrayAdapter<String> aa;
	ProgressDialog dialog;
	Location location;
	int p;
	String result;
	
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";
	
	List<Map<String,?>> today = new LinkedList<Map<String,?>>();
	List<Map<String,?>> nearby = new LinkedList<Map<String,?>>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrievefromweb);
        
        ConnectivityManager checkIC =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if(checkIC.getNetworkInfo(0).isConnected() == false) {
        	Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
        
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		
        recentTopTen();
		adapter.addSection("Top Ten Recent", new SimpleAdapter(this, today, R.layout.list_complex,
				new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));
		
		getCurrentLocation();
		adapter.addSection("Top Ten Nearby", new SimpleAdapter(this, nearby, R.layout.list_complex,
				new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));
		
		ListView list = getListView();
		list.setAdapter(adapter);
		
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				if(id <= 11) {
					position = position - 1;
					Toast.makeText(webService.this, "" + today.get(position).get("title"), Toast.LENGTH_SHORT).show();
				}
				else {
					position = position - 2 - 10;
					p = position;
					//Toast.makeText(webService.this, "" + nearby.get(position).get("title"), Toast.LENGTH_SHORT).show();
					
	                dialog = new ProgressDialog(webService.this);
	     
	                dialog.setCancelable(true);
	     
	                dialog.setMessage("Retrieving location...");
	     
	                dialog.show();
					
					//Toast.makeText(webService.this, getSongLocation(""+nearby.get(position).get("title")), Toast.LENGTH_LONG).show();
	                Thread t = new Thread(ws, "XML");
	                t.start();
				}
			}
        });
        
        ImageButton back = (ImageButton) findViewById(R.id.music);
        
        back.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		finish();
           	}
        });
    }
    
	public Map<String,?> createItem(String title, String caption) {
		Map<String,String> item = new HashMap<String,String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}
	
	public void recentTopTen() {
		today.clear();
		URL url;
		
        try{
          	String StringUrl="http://sit.rp.edu.sg/c345/getrecentpopularsongs.php?apikey=ff9a1a4c828de747dd9b2e4ab931101a2b8eb006&timeframe=86400&results=10";
          	
          	url = new URL(StringUrl);
          	
          	HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

          	int responseCode = httpConnection.getResponseCode();
          		
            if (responseCode == HttpURLConnection.HTTP_OK){
            	InputStream in = httpConnection.getInputStream();
          			
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          		DocumentBuilder db = dbf.newDocumentBuilder();

          		Document dom = db.parse(in);

          		Element docEle = dom.getDocumentElement();
          		
          		NodeList nl = docEle.getElementsByTagName("item");

          		if (nl != null && nl.getLength() > 0) {
          			for (int i = 0 ; i < nl.getLength(); i++) {
          				Element entry = (Element) nl.item(i);

          				Element topSong = (Element) entry.getElementsByTagName("song").item(0);
          				String topSongResult = topSong.getFirstChild().getNodeValue();
          				
          				Element topArtist = (Element) entry.getElementsByTagName("artist").item(0);
          				String topArtistResult = topArtist.getFirstChild().getNodeValue();
          				
          				Element playCount = (Element) entry.getElementsByTagName("playcount").item(0);
          				String playCountResult = playCount.getFirstChild().getNodeValue();
          				
          				today.add(createItem(topSongResult, topArtistResult + " (" + playCountResult + " count(s))"));
          			}
          			Log.d("recent song", "ok");
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
	
	private Runnable ws = new Runnable() {
	    @Override
		public void run() {
	    	result = getSongLocation(""+nearby.get(p).get("title"));
	    	
	    	wsHandler.sendEmptyMessage(0);
		}
	};

	private Handler wsHandler = new Handler(){
    	@Override
		public void handleMessage(Message msg) {
    		dialog.dismiss();
    		
    		Toast.makeText(webService.this, result, Toast.LENGTH_LONG).show();
    	}
	};

	
	public void getNearbySong(double lat, double lng) {
		nearby.clear();
		
		URL url;
		
        try{
          	String StringUrl="http://sit.rp.edu.sg/c345/getnearbysongs.php?apikey=ff9a1a4c828de747dd9b2e4ab931101a2b8eb006&lat=" + lat + "&lng=" + lng + "&timeframe=10";
          	
          	url = new URL(StringUrl);
          	
          	HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

          	int responseCode = httpConnection.getResponseCode();
          		
            if (responseCode == HttpURLConnection.HTTP_OK){
            	InputStream in = httpConnection.getInputStream();
          			
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          		DocumentBuilder db = dbf.newDocumentBuilder();

          		Document dom = db.parse(in);

          		Element docEle = dom.getDocumentElement();
          		
          		NodeList nl = docEle.getElementsByTagName("item");

          		if (nl != null && nl.getLength() > 0) {
          			for (int i = 0 ; i < nl.getLength(); i++) {
          				Element entry = (Element) nl.item(i);

          				Element nbSong = (Element) entry.getElementsByTagName("song").item(0);
	    				String nbSongAtt = nbSong.getAttributeNode("data").getValue();
	    				
	    				Element nbPlayTime = (Element) entry.getElementsByTagName("playtime").item(0);
	    				String nbPlayTimeAtt = nbPlayTime.getAttributeNode("data").getValue();
	    				
	    				Element nbDist = (Element) entry.getElementsByTagName("distance").item(0);
	    				String nbDistAtt = nbDist.getAttributeNode("data").getValue();
	    				
	    				nearby.add(createItem(nbSongAtt, "Date/Time: " + nbPlayTimeAtt + "\nDistance: " + nbDistAtt));
          			}
          			Log.d("nearby song", "ok");
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

	public String getSongLocation(String song) {
		URL url;
      	String loc = "";
      	song = song.replace(" ", "%20");
		
        try{
          	String StringUrl="http://sit.rp.edu.sg/c345/getsonglocations.php?apikey=ff9a1a4c828de747dd9b2e4ab931101a2b8eb006&song=" + song;
          	
          	url = new URL(StringUrl);
          	
          	HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

          	int responseCode = httpConnection.getResponseCode();
          		
            if (responseCode == HttpURLConnection.HTTP_OK){
            	InputStream in = httpConnection.getInputStream();
          			
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          		DocumentBuilder db = dbf.newDocumentBuilder();

          		Document dom = db.parse(in);

          		Element docEle = dom.getDocumentElement();
          		
          		NodeList nl = docEle.getElementsByTagName("item");

          		if (nl != null && nl.getLength() > 0) {
          			for (int i = 0 ; i < nl.getLength(); i++) {
          				Element entry = (Element) nl.item(i);

          				Element songLat = (Element) entry.getElementsByTagName("lat").item(0);
	    				String songLatAtt = songLat.getAttributeNode("data").getValue();
	    				
	    				Element songLng = (Element) entry.getElementsByTagName("lng").item(0);
	    				String songLngAtt = songLng.getAttributeNode("data").getValue();
	    				
	          			loc = loc + reverseGeocoding(songLatAtt, songLngAtt) + "\n";
          			}
          			Log.d("Get song location", "ok");
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
	  	
	  	return loc;
	}
	
    public void getCurrentLocation() {
    	//Use LocationManager to get current location from different service (network or gps)
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//Define on how accurate the current location will be
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		//Find out which are the best location provider and get the location
		String provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);
		
		//Define the time interval for updates of the location
		locationManager.requestLocationUpdates(provider, 0, 0, webService.this);
		
		onLocationChanged(location);
    }
	
    public String reverseGeocoding(String lat, String lng) {
		URL url;
		String locNameResult = "";
		String countryNameResult = "";
		
        try{
          	String StringUrl="http://ws.geonames.org/findNearbyPlaceName?lat=" + lat + "&lng=" + lng;
          	
          	url = new URL(StringUrl);
          	
          	HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

          	int responseCode = httpConnection.getResponseCode();
          		
            if (responseCode == HttpURLConnection.HTTP_OK){
            	InputStream in = httpConnection.getInputStream();
          			
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          		DocumentBuilder db = dbf.newDocumentBuilder();

          		Document dom = db.parse(in);

          		Element docEle = dom.getDocumentElement();
          		
          		NodeList nl = docEle.getElementsByTagName("geoname");

          		if (nl != null && nl.getLength() > 0) {
          			for (int i = 0 ; i < nl.getLength(); i++) {
          				Element entry = (Element) nl.item(i);

          				Element locName = (Element) entry.getElementsByTagName("toponymName").item(0);
          				locNameResult = locName.getFirstChild().getNodeValue();
	    				
          				Element locCountry = (Element) entry.getElementsByTagName("countryName").item(0);
          				countryNameResult = locCountry.getFirstChild().getNodeValue();
          			}
          			Log.d("Reverse geocoding", "ok");
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
	  	
    	return locNameResult + ", " + countryNameResult;
    }
    
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			//Obtain the current location latitude and longitude
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			getNearbySong(lat, lng);
			Log.d("Current Location", "Lat: " + lat + "Lng: " + lng);
		}
		else {
			//Message to show when GPS is unavailable
			Toast.makeText(this, "GPS not available", Toast.LENGTH_LONG).show();
			//locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
