package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import master.GameMaster;
import master.GameMaster.Combination;
import master.GameMaster.DiceType;
import master.Player;
import ui.MainFrame;

/**
 * Controls model<->view.
 * 
 * @author Lars
 *
 */
public class Controller implements Runnable {
	
	private GameMaster master;
	private MainFrame frame;
	
	public Controller(GameMaster master, MainFrame frame) {
		
		this.master = master;
		this.frame = frame;
	}
	
	/**
	 * Thread start.
	 */
	public void run() {
		
		this.frame.initialize();
		this.queryPlayers();
		
		/* Setup players. */
		for (Player p : this.master.getPlayers()) {
			this.frame.addPlayer(p.getID(), this.master.getNrOfRounds());
		}
		
		this.frame.update();
	}
	
	/**
	 * Retrieves the number of players given by the user. Keeps asking if answer not OK.
	 */
	private void queryPlayers() {
		
		String players = this.frame.queryPlayers();
		
		try {
			Integer playerNr = new Integer(players);
			if (playerNr < 2) {
				this.queryPlayers();
			}
			else {
				this.master.setPlayers(playerNr);
			}
			
		} catch(IllegalArgumentException e) {
			this.queryPlayers();
		}	
	}
	
	/**
	 * Stores the results of the dice that the player has selected.
	 * 
	 * @param playerID	player.
	 * @param result	dice result.
	 */
	public void saveDice(int playerID, int result) {
		if (playerID < 0 || result < 0)
			throw new IllegalArgumentException();
		
		this.master.getPlayerForID(playerID).saveDice(result);
	}
	
	/**
	 * Gets the rounds from the GameMaster.
	 * 
	 * @return the rounds from the GameMaster.
	 */
	public int getNrOfRounds() {
		return this.master.getNrOfRounds();
	}
	
	/**
	 * Call from "Roll Dice"-button in the UI.
	 * 
	 * @param playerID	the <code>Player</code>, must be 0 or larger.
	 */
	public void rollDice(int playerID) {
		if (playerID < 0)
			throw new IllegalArgumentException();
		
		Player p = this.master.getPlayerForID(playerID);
		if (!p.canRoll())
			return;
		
		int currentRound = p.getRound();
		if (currentRound > 0)
			this.frame.roundDone(playerID, currentRound);
		
		p.incRound();
		
		int[] diceRolled = new int[p.getDiceLeft()];
		for (int i=0; i < diceRolled.length; i++) {
			diceRolled[i] = (int)(DiceType.getMax(this.master.getDiceType()) * Math.random()) + 1;
		}
		
		p.setDiceInHand(diceRolled);
		
		this.frame.showDiceResults(playerID, p.getDiceInHand(), p.getSavedDice(), p.getRound(), p.canRoll());
		
	}
	
	/**
	 * 
	 * @return if this game is over or not.
	 */
	private boolean endGame() {
		
		boolean endGame = true;
		for (Player p : this.master.getPlayers())
			endGame = endGame && p.isDonePlaying();
		
		return endGame;
	}
	
	/**
	 * 
	 * @return the <code>Player</code> with the highest score.
	 */
	private Player whoLeads() {
		
		Player leader = this.master.getPlayerForID(1);
		for (Player p : this.master.getPlayers())
			leader = p.getScore() > leader.getScore() ? p : leader;
			
		return leader;
	}
	
	/**
	 * The player uses a combination, but it is only spent if it can grant higher score.
	 * 
	 * @param playerID	the player. Must be 0 or higher.
	 * @param comb		the <code>Combination</code> the player wishes to spend. Can not be <code>null</code>.
	 */
	public void useCombination(int playerID, Combination comb) {
		if (playerID < 0 || comb == null)
			throw new IllegalArgumentException();
		
		Player p = this.master.getPlayerForID(playerID);
		
		/* Quite ugly, if no rolls have been made, no score can be claimed. TODO disable combination button. */
		if (p.getDiceInHand() == null)
			return;

		if (this.calculateScore(p, comb) > 0) {
			this.frame.showScore(playerID, p.getScore());
		}
	}
	
	
	/**
	 * Calculates the score given by a specific combination. However, if score hasn't incremented, the combination is not spent.
	 * Specific to HOUSE, but could be considered OK for the other combinations as well.
	 * 
	 * @param p		the <code>Player</code> who is trying to raise his/her score. Can not be <code>null</code>.
	 * @param comb	the <code>Combination</code> spent (if possible to spend). Can not be <code>null</code>.
	 * 
	 * @return	-1 if combination isn't spent. New score otherwise.
	 */
	private int calculateScore(Player p, Combination comb) {
		if (comb == null || comb == null)
			throw new IllegalArgumentException();
		
		int score = p.getScore();
		
		/* Get all dice of the player in one place (saved and in hand). */
		ArrayList<Integer> totalDice = new ArrayList<Integer>();
		totalDice.addAll(p.getSavedDice());
		for (int inHand : p.getDiceInHand())
			totalDice.add(inHand);
		
		if (comb.equals(Combination.HOUSE)) {
			score += this.calculateHouseScore(p, totalDice);
		}
		else {	
			for (int dice : totalDice) {
				if (dice == Combination.getValue(comb))
					score += dice;
			}
		}
		
		if (score == p.getScore()) {
			return 0;
		}
		else {
			p.setScore(score);
			p.useCombination(comb);
			
			return score;
		}
	}
	
	/**
	 * Trickier than simple combination.
	 * 
	 * @param p	the <code>Player</code> that is trying to claim a HOUSE.
	 * @param totalDice	the sum of the current dice in hand and the saved dice.
	 * 
	 * @return	the <code>Player</code>'s previous score added to that of the HOUSE combination.
	 */
	private int calculateHouseScore(Player p, List<Integer> totalDice) {
		
		int score = 0;
		HashSet<Integer> setOfDice = new HashSet<Integer>(totalDice);
		
		boolean validHouse = false;
		int diceTypes = setOfDice.size();
		
		/* Only two types of dice in a house, and one of these types needs to be 2 (2nd type is 3) or 3 (2nd type is 2). */
		if (diceTypes == 2) {
			int pairOrTriss = Collections.frequency(totalDice, totalDice.get(0));
				validHouse = !validHouse && (pairOrTriss == 2 || pairOrTriss == 3);
		}
		
		/* Yes, valid house, calculate score. */
		if (validHouse) {
			for (int type : setOfDice) {
				score += (Collections.frequency(totalDice, type) * type); 
			}
		}
		
		return score;
	}

	/**
	 * Retrieves the available <code>Combination</code>s for a specific <code>Player</code>.
	 * 
	 * @param playerID	the player. Must be 0 or larger.
	 * 
	 * @return	
	 */
	public Set<Combination> getCombinations(int playerID) {
		if (playerID < 0)
			throw new IllegalArgumentException();
		
		return this.master.getPlayerForID(playerID).getCombinationsLeft();
	}

	/**
	 * The player is done. Disables last round of dice buttons.
	 * 
	 * @param playerID	the player. Must be greater than 0.
	 */
	public void playerDone(int playerID) {
		if (playerID < 0)
			throw new IllegalArgumentException();
		
		Player p = this.master.getPlayerForID(playerID);
		p.setDonePlaying();
		
		int round = p.getRound();
		if(round > 0) 
			this.frame.roundDone(playerID, p.getRound());
	}

	/**
	 * Is the game over? If so, end it.
	 */
	public void checkEndGame() {
		
		Player leader = this.whoLeads();
		if (this.endGame())
			this.frame.endGame(leader.getID(), leader.getScore());
	}
}
