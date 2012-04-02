package iitm.apl.player.ui;

import iitm.apl.bktree.BKTree;
import iitm.apl.player.Song;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for a library
 * 
 */
public class LibraryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 8230354699902953693L;

	// TODO: Change to your implementation of Trie/BK-Tree
	public final static int thresholdDistance = 7;
	private Vector<Song> songListing;
	private int songIteratorIdx;
	private Song currentSong;
	private Iterator<Song> songIterator;

	LibraryTableModel() {
		songListing = new Vector<Song>();
		songIterator = songListing.iterator();
	}

	public void add(Song song) {
		songListing.add(song);
		resetIdx();
		fireTableDataChanged();
	}

	public void add(Vector<Song> songs) {
		songListing.addAll(songs);
		resetIdx();
		fireTableDataChanged();
	}

	public void filter(String searchTerm, BKTree<String> songTree, Hashtable wordToSong) {
		// TODO: Connect the searchText keyPressed handler to update the filter
		// here.
		
		searchTerm = searchTerm.toLowerCase();
		/* Suppose the user has not typed any text, display all the songs and return */
		if(searchTerm.compareTo("") == 0)
		{
			HashMap<String, Integer> filteredSongs = songTree.makeQuery("", 100);
			Set<String> fsl = filteredSongs.keySet();
			Vector<String> filteredSongsList = new Vector<String>(fsl);
			songListing.removeAllElements();
			for(String string : filteredSongsList)
			{
				Vector<Song> vSong = (Vector<Song>) wordToSong.get(string);
				for( Song song : vSong )
				{
					if(!songListing.contains(song)) songListing.add(song);
				}
			}
			resetIdx();
			fireTableDataChanged();
			return;
		}
		
		
		/* Split the searchTerm in to its words */
		String[] searchTermWords = searchTerm.split(" ");
		
		/*Initial checking with only one word - later implement loop and extend to multiple words */
		HashMap<String, Integer> filteredSongs = songTree.makeQuery(searchTermWords[0], thresholdDistance);
		Set<String> fsl = filteredSongs.keySet();
		Vector<String> filteredSongsList = new Vector<String>(fsl);
		songListing.removeAllElements();
		for(String string : filteredSongsList)
		{
			Vector<Song> vSong = (Vector<Song>) wordToSong.get(string);
			for( Song song : vSong )
			{
				if(!songListing.contains(song)) songListing.add(song);
			}
		}
		resetIdx();
		fireTableDataChanged();
		System.out.println(filteredSongsList);
		System.out.println("Song : " + songListing);
		
	}
	
	public void resetIdx()
	{
		songIteratorIdx = -1;
		currentSong = null;
		songIterator = songListing.iterator();
	}
	// Gets the song at the currently visible index
	public Song get(int idx) {
		if( songIteratorIdx == idx )
			return currentSong;
		
		if(songIteratorIdx > idx)
		{
			resetIdx();
		}
		while( songIteratorIdx < idx && songIterator.hasNext() )
		{
			currentSong = songIterator.next();
			songIteratorIdx++;
		}
		return currentSong;
	}

	@Override
	public int getColumnCount() {
		// Title, Album, Artist, Duration.
		return 4;
	}

	@Override
	public int getRowCount() {
		// TODO: Changes if you've filtered the list
		return songListing.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		// TODO: Get the appropriate row
		Song song = get(row);
		if(song == null) return null;

		switch (col) {
		case 0: // Title
			return song.getTitle();
		case 1: // Album
			return song.getAlbum();
		case 2: // Artist
			return song.getArtist();
		case 3: // Duration
			int duration = song.getDuration();
			int mins = duration / 60;
			int secs = duration % 60;
			return String.format("%d:%2d", mins, secs);
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0: // Title
			return "Title";
		case 1: // Album
			return "Album";
		case 2: // Artist
			return "Artist";
		case 3: // Duration
			return "Duration";
		default:
			return super.getColumnName(column);
		}
	}

}
