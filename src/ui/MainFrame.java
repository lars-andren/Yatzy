package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.Controller;
import master.GameMaster.Combination;
import resources.TextLabels;

/**
 * MainUI view. A JFrame window.
 * 
 * @author Lars
 *
 */
public class MainFrame extends JFrame {
                   
	private static final long serialVersionUID = 1L;

	private PanelContainer mainPanel;
	
	private Dimension mainSize;
	
	private Controller c;
	
	/**
	 * Player ID -> PlayerPanel
	 */
	private HashMap<Integer, PanelContainer> playerPanelMap;
	
	public MainFrame() {}
	
	Controller getController() {
		return this.c;
	}
	
	public void setController(Controller c) {
		if (c == null)
			throw new IllegalArgumentException();
		
		this.c = c;
	}
	
	public void update() {
	
		this.resize(this.mainSize);
		this.setVisible(true);
		this.pack();
		
	}
	
	public void initialize() {

		this.setTitle(TextLabels.GAME_TITLE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		
		this.mainSize = new Dimension(200,300);
		this.mainPanel = new PanelContainer(new JPanel(), 2, 1);
		JPanel mpRef = this.mainPanel.getPanel();
		JLabel winnerLabel = new JLabel(TextLabels.WINNER);
		this.mainPanel.getMatrix()[1][0].add(winnerLabel);
		
		this.add(mpRef);
		
		this.playerPanelMap = new HashMap<Integer, PanelContainer>();
	}
	
	/**
	 * Adds a player's panel to the MainFrame, stores a reference from Player to this panel.
	 * 
	 * @param playerID	the player. Must be 0 or larger.
	 * @param rounds	the number of rounds to play. Must be 1 or larger.
	 */
	public void addPlayer(int playerID, int rounds) {
		if (playerID < 0 || rounds < 1)
			throw new IllegalArgumentException();
		
		/* Create a panel, add to the mainpanel*/
		PlayerPanelConstructor ppc = new PlayerPanelConstructor(playerID, rounds, this);
		PanelContainer playerPanel = ppc.getPC();
		
		this.mainPanel.getMatrix()[0][0].add(playerPanel.getPanel());
		
		this.playerPanelMap.put(playerID, playerPanel);
		
		this.update();
	}
	
	/**
	 * For roll dice button.
	 * 
	 * @param playerID	player rolling dice. Must be 0 or larger.
	 */
	protected void rollDice(int playerID) {
		if (playerID < 0)
			throw new IllegalArgumentException();
		
		this.c.rollDice(playerID);
	}
	
	/**
	 * Shows the point claiming combinations left to a player.
	 * 
	 * @param playerID the player.
	 */
	protected void showCombinations(int playerID) {

		JDialog popUp = new JDialog();
		JPanel popUpPanel = new JPanel();
		Set<Combination> combinationsLeft = this.c.getCombinations(playerID);
		
		/* Create a button for each Combination left for the player. */
		for (Combination comb : combinationsLeft) {
			JButton combButton = new JButton(Combination.getName(comb));	
			combButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.c.useCombination(playerID, comb);
					popUp.dispose();
				}
			});
			combButton.setEnabled(true);
			popUpPanel.add(combButton);
		}
		
		popUp.add(popUpPanel);
		popUp.setVisible(true);
		popUp.pack();
	}

	/**
	 * Ask for number of players.
	 * 
	 * @return	whatever the user gave as input.
	 */
	public String queryPlayers() {
		return JOptionPane.showInputDialog(this, TextLabels.PLAYER_NUMBER);
		
	}

	/**
	 * Updates the score of a player.
	 * 
	 * @param playerID	the player. Must be 0 or larger.
	 * @param score		the new score of the player. Must be larger than 0.
	 */
	public void showScore(int playerID, int score) {
		if (playerID < 0 || score < 0)
			throw new IllegalArgumentException();
		
		PanelContainer panelContainer = this.playerPanelMap.get(playerID);
		JPanel[][] panelMatrix = panelContainer.getMatrix();
	
		/* Cleanup old score. */
		JPanel scorePanel = panelMatrix[this.c.getNrOfRounds()+4][0];
		scorePanel.removeAll();
		
		/* Add new score. */ 
		JLabel scoreLabel = new JLabel("" + score);
		scorePanel.add(scoreLabel);
		
		this.update();
	}
	
	/**
	 * Show the dice results of the dice thrown, and those saved.
	 * TODO one pretty hacky reference to which JPanel to show results (rounds + x).
	 * 
	 * @param playerID		the player. Must be non-negtive.
	 * @param diceInHand	the results of the recently thrown dice. Can not be <code>null</code>.
	 * @param savedDice		the dice saved from previous rounds. Can not be <code>null</code>.
	 * @param round			current round. Must be greater than 0.
	 * @param canRoll		is the player allowed to roll again?
	 */
	public void showDiceResults(int playerID, int[] diceInHand, List<Integer> savedDice, int round, boolean canRoll) {
		if (playerID < 0 || diceInHand == null || savedDice == null || round < 1) 
			throw new IllegalArgumentException();
		
		PanelContainer panelContainer = this.playerPanelMap.get(playerID);
		JPanel[][] panelMatrix = panelContainer.getMatrix();
		
		/* Newly rolled dice. */
		JPanel newResult = new JPanel();
		
		for (int dice : diceInHand) {
			
			/* Auto-save results if last round reached. */
			JButton rolledDice = new JButton("" + dice);
			if(canRoll) {
				rolledDice.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MainFrame.this.c.saveDice(playerID, dice);
						rolledDice.setEnabled(false);
					}          
					});
			} else {
				this.c.saveDice(playerID, dice);
			}
			
			newResult.add(rolledDice);
		}

		panelMatrix[round][0].add(newResult);
		
		/* Stored dice. */
		JPanel savedResult = panelMatrix[this.c.getNrOfRounds()+2][0];
		savedResult.removeAll();
		
		for (int saved : savedDice) {
			JLabel savedLabel = new JLabel("" + saved);
			savedLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			savedLabel.setPreferredSize(new Dimension(30,30));
			savedResult.add(savedLabel);
		}
		
		this.update();
	}

	/**
	 * 
	 * @param playerID
	 * @param round
	 */
	public void roundDone(int playerID, int round) {
		
		for (Component panelComp : this.playerPanelMap.get(playerID).getMatrix()[round][0].getComponents()) {
				JPanel panel = (JPanel) panelComp;
				for (Component button : panel.getComponents()) {
					button.setEnabled(false);
				}
		}
	}
	
	/**
	 * No more rolls allowed, the game is over.
	 * 
	 * @param playerID	the player who won. Must be greater than 0.
	 * @param score	the winning score. Must be less than or greater than 0.
	 */
	public void endGame(int playerID, int score) {
		if (playerID < 1 || score < 0)
			throw new IllegalArgumentException();
		
		JLabel winnerLabel = new JLabel("Player " + playerID + ", with a total score of " + score + "!");
		this.mainPanel.getMatrix()[1][0].add(winnerLabel);
		this.update();
	}

	/**
	 * Disables dice & combinations button.
	 * Passes on the "Im done" message from the player to the <code>Controller</code>.
	 * 
	 * @param playerID	the player.
	 */
	public void playerDone(int playerID) {
		if (playerID < 1)
			throw new IllegalArgumentException();
		
		for (Component panelComp : this.playerPanelMap.get(playerID).getMatrix()[0][0].getComponents()) {
			JPanel panel = (JPanel) panelComp;
			for (Component button : panel.getComponents()) {
				button.setEnabled(false);
			}
	}
		
		this.c.playerDone(playerID);
		
	}

	/**
	 * Is the game over?
	 */
	public void checkEndGame() {
		this.c.checkEndGame();
	}
}
