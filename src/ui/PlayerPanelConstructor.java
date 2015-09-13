package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import resources.TextLabels;

/**
 * Helper class that constructs a JPanel with all the player relevant items inside.
 * 
 * @author Lars
 *
 */
public class PlayerPanelConstructor implements PanelConstructor {
	
	/* TODO very hacky, but wtf..*/
	private final int nrOfPlayerPanelSubPanels = 6;
	private static final Dimension PLAYER_ID_PANE_DIMENSION = new Dimension(100, 100);
	
	private int playerID;
	private int rounds;
	
	/**
	 * Main window.
	 */
	private MainFrame main;
	
	/**
	 * Single constructor.
	 * 
	 * @param playerID	the player.	Must be non-negative.
	 * @param rounds	how many rounds to play. Must be greater than 0.
	 * @param main		main window. Can not be <code>null</code>.
	 */
	public PlayerPanelConstructor(int playerID, int rounds, MainFrame main) {
		if (playerID < 0 || rounds < 1 || main == null) 
			throw new IllegalArgumentException();
		
		this.playerID = playerID;
		this.rounds = rounds;
		this.main = main;
		
	}
	
	/**
	 * Main method of private constructing methods.
	 * 
	 * @return the complete panel with references.
	 */
	private PanelContainer constructPlayerPanel() {
		
		int totalRows = this.rounds + nrOfPlayerPanelSubPanels;
		PanelContainer playerPanel = new PanelContainer(new JPanel(), totalRows, 1);
		
		JPanel panel = playerPanel.getPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
//		panel.setLayout(new GridLayout(totalRows, 1));
		
		playerPanel.getMatrix()[0][0].add(this.constructPlayerIDPanel());
		playerPanel.getMatrix()[totalRows-3][0].add(this.constructPlayerScorePanel());
		playerPanel.getMatrix()[totalRows-5][0].add(this.constructSavedDicePanel());
		playerPanel.getMatrix()[totalRows-1][0].add(this.constructDonePanel());
		
		return playerPanel;
	}
	
	/**
	 * Helper method to do saved dice.
	 * 
	 * @return	the panel above the saved dice display.
	 */
	private JPanel constructSavedDicePanel() {
		
		JPanel savedDicePanel = new JPanel();
//		savedDicePanel.setLayout(new GridLayout(2, 1, 1, 1));
		JLabel diceLabel = new JLabel(TextLabels.SAVED_DICE);
		savedDicePanel.add(diceLabel);
		
		return savedDicePanel;
	}
	
	/**
	 * Helper method to do score panel.
	 * 
	 * @return	the panel above the score number.
	 */
	private JPanel constructPlayerScorePanel() {
		
		JPanel playerScorePanel = new JPanel();
//		playerScorePanel.setLayout(new GridLayout(2, 1, 1, 1));
		JLabel scoreLabel = new JLabel(TextLabels.SCORE);
		playerScorePanel.add(scoreLabel);
		
		return playerScorePanel;
	}
	
	/**
	 * Done button panel.
	 * 
	 * @return a panel with the "done"-button.
	 */
	private JPanel constructDonePanel() {
		
		JPanel donePanel = new JPanel();
		JButton doneButton = new JButton(TextLabels.DONE);
		doneButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	            PlayerPanelConstructor.this.main.playerIsDone(PlayerPanelConstructor.this.playerID);
	            doneButton.setEnabled(false);
	            PlayerPanelConstructor.this.main.checkEndGame();
	         }          
	      });
		doneButton.setEnabled(true);
		donePanel.add(doneButton);
		
		return donePanel;
	}
	
	/**
	 * The player ID and two buttons.
	 * 
	 * @return	the Panel with player ID and two buttons.
	 */
	private JPanel constructPlayerIDPanel() {
		
		JPanel playerIDPanel = new JPanel();
		playerIDPanel.setPreferredSize(PLAYER_ID_PANE_DIMENSION);
		
		JLabel label = new JLabel(TextLabels.NAME_SUBSTITUTE + " " + this.playerID);
		playerIDPanel.add(label);
		
		/* Button for rolling dice. */
		JButton rollButton = new JButton(TextLabels.ROLL_DICE);
		rollButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	            PlayerPanelConstructor.this.main.rollDice(PlayerPanelConstructor.this.playerID);
	         }          
	      });
		rollButton.setEnabled(true);
		
		/* Button for spending a combination. */
		JButton combButton = new JButton(TextLabels.USE_COMBINATION);
		combButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlayerPanelConstructor.this.main.showAvailableCombinations(PlayerPanelConstructor.this.playerID);
			}
		});
		combButton.setEnabled(true);
		
		playerIDPanel.add(rollButton);
		playerIDPanel.add(combButton);
		
		return playerIDPanel;
	}

	/**
	 * Constructs and returns the panel, along with references.
	 */
	@Override
	public PanelContainer getPC() {
		
		return this.constructPlayerPanel();
	}
}
