package iitm.apl.player.ui;

import iitm.apl.bktree.BKTree;
import iitm.apl.bktree.LevenshteinDistance;
import iitm.apl.player.Song;
import iitm.apl.player.ThreadedPlayer;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

/**
 * The JamPlayer Main Class Sets up the UI, and stores a reference to a threaded
 * player that actually plays a song.
 * 
 * TODO: a) Implement the search functionality b) Implement a play-list
 * generation feature
 */
public class JamPlayer {

	// UI Items
	private JFrame mainFrame;
	private PlayerPanel pPanel;

	private JTable libraryTable;
	private LibraryTableModel libraryModel;

	private Thread playerThread = null;
	private ThreadedPlayer player = null;

	public JamPlayer() {
		// Create the player
		player = new ThreadedPlayer();
		playerThread = new Thread(player);
		playerThread.start();
	}
	
	/* My declarations */
	public BKTree<String> songTree = new BKTree<String>(new LevenshteinDistance());
	public String searchTerm = new String();
	

	/**
	 * Create a file dialog to choose MP3 files to add
	 */
	private Vector<Song> addFileDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;

		File selectedFile = chooser.getSelectedFile();
		// Read files as songs
		Vector<Song> songs = new Vector<Song>();
		if (selectedFile.isFile()
				&& selectedFile.getName().toLowerCase().endsWith(".mp3")) {
			songs.add(new Song(selectedFile));
			return songs;
		} else if (selectedFile.isDirectory()) {
			for (File file : selectedFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".mp3");
				}
			}))
			{
				songs.add(new Song(file));
			}
				
		}

		return songs;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		mainFrame = new JFrame("JamPlayer");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setMinimumSize(new Dimension(300, 400));

		// Create and set up the content pane.
		Container pane = mainFrame.getContentPane();
		pane.add(createMenuBar(), BorderLayout.NORTH);
		pane.add(Box.createHorizontalStrut(30), BorderLayout.EAST);
		pane.add(Box.createHorizontalStrut(30), BorderLayout.WEST);
		pane.add(Box.createVerticalStrut(30), BorderLayout.SOUTH);

		JPanel mainPanel = new JPanel();
		GroupLayout layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);

		pPanel = new PlayerPanel(player);
		JLabel searchLabel = new JLabel("Search: ");
		JTextField searchText = new JTextField(200);
		searchText.setMaximumSize(new Dimension(200, 20));
		searchText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) 
			{
				// TODO: Handle the case when the text field has been modified.
				// Optional: Can you update the search "incrementally" i.e. as
				// and when the user changes the search text?
				char key = arg0.getKeyChar();
				/* If the character is a backspace, cut the last character in the search string */
				if(key == '\b')
				{
					if(searchTerm.length() > 0)
					{
						searchTerm = searchTerm.substring(0, searchTerm.length()-1);
					}
				}
				/* Else concatenate the entered key to the searchTerm */
				else
				{
					searchTerm = searchTerm.concat(Character.toString(key));
				}
				/* Pass control to the filter function in the libraryDataModel */
				libraryModel.filter(searchTerm, songTree, wordToSong);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});

		libraryModel = new LibraryTableModel();
		libraryTable = new JTable(libraryModel);
		libraryTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() > 1) {
					Song song = libraryModel.get(libraryTable.getSelectedRow());
					if (song != null) {
						player.setSong(song);
						pPanel.setSong(song);
					}
				}
			}
		});

		libraryTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane libraryPane = new JScrollPane(libraryTable);

		layout.setHorizontalGroup(layout
				.createParallelGroup(Alignment.CENTER)
				.addComponent(pPanel)
				.addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addComponent(searchLabel)
								.addComponent(searchText).addContainerGap())
				.addComponent(libraryPane));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(pPanel)
				.addContainerGap()
				.addGroup(
						layout.createParallelGroup(Alignment.CENTER)
								.addComponent(searchLabel)
								.addComponent(searchText))
				.addComponent(libraryPane));

		pane.add(mainPanel, BorderLayout.CENTER);

		// Display the window.
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		JMenuBar mbar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem addSongs = new JMenuItem("Add new files to library");
		addSongs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Vector<Song> songs = addFileDialog();
				if (songs != null)
				{
					/* Adds the elements to a BKTree */
					songTree = VectorToBKTree(songs);
					libraryModel.add(songs);
				}
			}
		});
		file.add(addSongs);

		JMenuItem createPlaylist = new JMenuItem("Create playlist");
		createPlaylist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createPlayListHandler();
			}
		});
		file.add(createPlaylist);

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
			}
		});
		file.add(quitItem);

		mbar.add(file);

		return mbar;
	}

	protected void createPlayListHandler() {
		// TODO: Create a dialog window allowing the user to choose length of
		// play list, and a play list you create that best fits the time
		// specified
		PlayListMakerDialog dialog = new PlayListMakerDialog(this);
		dialog.setVisible(true);
	}
	
	public Vector<Song> getSongList()
	{
		Vector<Song> songs = new Vector<Song>();
		for(  int i = 0; i< libraryModel.getRowCount(); i++)
			songs.add(libraryModel.get(i));
		return songs;
	}
	
	/* Method to convert Vector of Songs to a BKTree of Strings with the song "title" - NOTE TITLE */
	Hashtable wordToSong = new Hashtable();
	public BKTree<String> VectorToBKTree(Vector<Song> songs)
	{
		BKTree<String> bktree = new BKTree<String>(new LevenshteinDistance());
		for(Song song : songs)
		{
			String title = song.getTitle().toLowerCase();
			String[] titleWords = title.split(" ");
			for(String string : titleWords)
			{
				Vector<Song> list = new Vector<Song>();
				/* If the hashtable contains the string already, add the song to the list that is hashed by the string*/
				if(wordToSong.containsKey(string))
				{
					list = (Vector<Song>) wordToSong.get(string);
				}
				/* If not, or either way, just add the song to the list, and the add the set to the hashtable */
				list.add(song);
				wordToSong.remove(string);
				wordToSong.put(string, list);
				bktree.add(string);
				System.out.println(wordToSong);
			}
		}
		System.out.println(wordToSong.get("For"));
		return bktree;
	}
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JamPlayer player = new JamPlayer();
				player.createAndShowGUI();
			}
		});
	}
}
