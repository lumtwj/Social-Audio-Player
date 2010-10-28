package sg.edu.rp.joelum.sapAssignment;

import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class customListAdapter extends ArrayAdapter<Item> {
	Context context;
	int textViewResourceId;
	ArrayList<Item> al;
	
	public customListAdapter(Context context, int textViewResourceId,
			ArrayList<Item> al) {
		super(context, textViewResourceId, al);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.al = al;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		if(row == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = li.inflate(textViewResourceId, null);
		}
		
		Item ali = al.get(position);
		
		if(ali != null) {
            TextView title = (TextView)row.findViewById(R.id.lvMetaTitle);
            TextView album = (TextView)row.findViewById(R.id.lvMetaAlbum);
            TextView artist = (TextView)row.findViewById(R.id.lvMetaArtist);
            ImageView image = (ImageView)row.findViewById(R.id.lvAlbumArt);
            
            if (title != null) {
            	title.setText(ali.getTitle());
            }
            if (album != null) {
            	album.setText(ali.getAlbum());
            }
            if (artist != null) {
            	artist.setText(ali.getArtist());
            }
            if (image != null) {
            	image.setImageURI(Uri.parse(ali.getAlbumArt()));
            }
		}
		
		return row;
	}
}
