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

	private MainFrame main;

	public PlayerPanelConstructor(int playerID, int rounds, MainFrame main) {
		if (playerID < 0 || rounds < 1 || main == null) 
			throw new IllegalArgumentException();
		
		this.playerID = playerID;
		this.rounds = rounds;
		this.main = main;
		
	}

	private PanelContainer constructPlayerPanel() {
		
		int totalRows = this.rounds + nrOfPlayerPanelSubPanels;
		PanelContainer playerPanel = new PanelContainer(new JPanel(), totalRows, 1);
		
		JPanel panel = playerPanel.getJPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
//		panel.setLayout(new GridLayout(totalRows, 1));
		
		playerPanel.getPanelMatrix()[0][0].add(this.constructPlayerIDPanel());
		playerPanel.getPanelMatrix()[totalRows-3][0].add(this.constructPlayerScorePanel());
		playerPanel.getPanelMatrix()[totalRows-5][0].add(this.constructSavedDicePanel());
		playerPanel.getPanelMatrix()[totalRows-1][0].add(this.constructDonePanel());
		
		return playerPanel;
	}

	private JPanel constructSavedDicePanel() {
		
		JPanel savedDicePanel = new JPanel();
//		savedDicePanel.setLayout(new GridLayout(2, 1, 1, 1));
		JLabel diceLabel = new JLabel(TextLabels.SAVED_DICE);
		savedDicePanel.add(diceLabel);
		
		return savedDicePanel;
	}

	private JPanel constructPlayerScorePanel() {
		
		JPanel playerScorePanel = new JPanel();
//		playerScorePanel.setLayout(new GridLayout(2, 1, 1, 1));
		JLabel scoreLabel = new JLabel(TextLabels.SCORE);
		playerScorePanel.add(scoreLabel);
		
		return playerScorePanel;
	}

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

	private JPanel constructPlayerIDPanel() {
		
		JPanel playerIDPanel = new JPanel();
		playerIDPanel.setPreferredSize(PLAYER_ID_PANE_DIMENSION);
		
		JLabel label = new JLabel(TextLabels.NAME_SUBSTITUTE + " " + this.playerID);
		playerIDPanel.add(label);

		JButton rollButton = createDiceRollButton();
		JButton combinationButton = createCombinationButton();

		playerIDPanel.add(rollButton);
		playerIDPanel.add(combinationButton);
		
		return playerIDPanel;
	}

	private JButton createDiceRollButton() {
		JButton rollButton = new JButton(TextLabels.ROLL_DICE);
		rollButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlayerPanelConstructor.this.main.rollDice(PlayerPanelConstructor.this.playerID);
			}
		});
		rollButton.setEnabled(true);
		return rollButton;
	}

	private JButton createCombinationButton() {
		JButton combinationButton = new JButton(TextLabels.USE_COMBINATION);
		combinationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlayerPanelConstructor.this.main.showAvailableCombinations(PlayerPanelConstructor.this.playerID);
			}
		});
		combinationButton.setEnabled(true);
		return combinationButton;
	}

	@Override
	public PanelContainer getPanelContainer() {
		
		return this.constructPlayerPanel();
	}
}
