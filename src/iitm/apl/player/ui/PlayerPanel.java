package iitm.apl.player.ui;

import iitm.apl.player.Song;
import iitm.apl.player.ThreadedPlayer;
import iitm.apl.player.ThreadedPlayer.State;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * PlayerPanel
 * Contains player controls, and raises appropriate events   
 * @author Arun Tejasvi Chaganty <arunchaganty@gmail.com>
 *
 */
public class PlayerPanel extends JPanel {
	private static final long serialVersionUID = -5264313656161958408L;
	
	private JLabel songLabel;
	private Song currentSong;
	
	private JButton prevButton;
	public final ImageIcon play = new ImageIcon("play.png");
	public final ImageIcon pause = new ImageIcon("pause.png");
	public JButton playPauseButton;
	private JButton stopButton;
	private JButton nextButton;
	
	
	private ThreadedPlayer player;
	
	public PlayerPanel(ThreadedPlayer player_)
	{
		// Call the parent constructor
		super();
		this.player = player_;

		// Set layout manager
		setLayout( new FlowLayout( FlowLayout.CENTER));
		
		songLabel = new JLabel("");
		songLabel.setMinimumSize( new Dimension( 60, 30));
		
		// Add buttons
		prevButton = new JButton(new ImageIcon("previous.png"));
		prevButton.setEnabled( true );
		prevButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
			}
		});
		add(prevButton);
		
		
		  
		playPauseButton = new JButton();
		playPauseButton.setIcon(play);
		playPauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( player.getState() == State.PLAY )
					{
					player.setState(State.PAUSE);
					playPauseButton.setIcon(play);
					}
				else if( player.getState() == State.PAUSE )
				{
					player.setState(State.PLAY);
					playPauseButton.setIcon(pause);
					
				}
			}
		});
		
		if( player.getState() == State.PAUSE )
			playPauseButton.setIcon(play);
		else if(player.getState()==State.PLAY)
			playPauseButton.setIcon(pause);
		add(playPauseButton);
		
		stopButton = new JButton(new ImageIcon("stop.png"));
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.setState(State.STOP);
			}
		});
		add(stopButton);
		
		nextButton = new JButton(new ImageIcon("next.png"));
		nextButton.setEnabled( true );
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		add(nextButton);
		/*
		songLabel = new JLabel("Now Playing : ");
		songLabel.setMaximumSize(new Dimension(30,20));
		Font f = new Font("Helvetica", Font.ITALIC, 15);
		songLabel.setFont(f);
		add(songLabel,2);
		*/
	}

	public void setSong(Song song)
	{
		currentSong = song;
		String lbl = currentSong.toString();
		songLabel.setText( lbl );
	}
	
}
