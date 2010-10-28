package sg.edu.rp.joelum.sapAssignment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class widgetService extends Service {
	
	public static final String UPDATEALERT = "edu.rp.sdwidget.APPWIDGET_UPDATE";
	
	Timer timer = new Timer();
	
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		TimerTask createRandom = new TimerTask() {

			@Override
			public void run() {
				Intent bIntent = new Intent(UPDATEALERT);

				bIntent.putExtra("status", mostPopular());
	
				sendBroadcast(bIntent);
			}
		};
		timer.schedule(createRandom, 0, (1000 * 60 * 60)/2);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//done after submission 
	public String mostPopular() {
		URL url;
		String results = "";
		
        try{
          	String StringUrl="http://sit.rp.edu.sg/c345/getrecentpopularsongs.php?apikey=ff9a1a4c828de747dd9b2e4ab931101a2b8eb006&timeframe=86400&results=1";
          	
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
          				
          				results = topSongResult + "\n" + topArtistResult + "\n" + playCountResult + " times";
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
	  	
	  	return results;
	}
}
