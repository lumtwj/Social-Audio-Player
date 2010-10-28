package sg.edu.rp.joelum.sapAssignment;


public class Item {
	String id;
	String title;
	String artist;
	String album;
	String albumArt;
	String filedir;
	
	public Item(String id, String title, String artist, String album, String albumArt, String filedir) {
		super();
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.albumArt = albumArt;
		this.filedir = filedir;
	}
	
	public String toString() {
		return title;
	}
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getArtist() {
		return artist;
	}
	public String getAlbum() {
		return album;
	}
	public String getAlbumArt() {
		return albumArt;
	}
	public String getFiledir() {
		return filedir;
	}
}
