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
	
	public void run() {
		
		this.frame.initialize();
		this.queryPlayers();
		
		/* Setup players. */
		for (Player p : master.getPlayers()) {
			frame.addPlayer(p.getID(), master.getNrOfRounds());
		}
		
		this.frame.update();
	}
	
	private void queryPlayers() {
		
		String players = frame.askForPlayersNumber();
		
		try {
			Integer playerNr = new Integer(players);
			if (playerNr < 2) {
				this.queryPlayers();
			}
			else {
				master.setPlayers(playerNr);
			}
			
		} catch(IllegalArgumentException e) {
			this.queryPlayers();
		}	
	}
	
	public void saveDice(int playerID, int result) {
		master.getPlayerForID(playerID).saveDice(result);
	}
	
	public int getNrOfRounds() {
		return master.getNrOfRounds();
	}
	
	public void rollDice(int playerID) {
		
		Player p = master.getPlayerForID(playerID);
		if (!p.canPlayerRoll())
			return;

        checkAndIncRound(p);

		int[] diceRolled = new int[p.getDiceLeft()];
		for (int i=0; i < diceRolled.length; i++) {
			diceRolled[i] = (int)(DiceType.getMax(this.master.getDiceType()) * Math.random()) + 1;
		}
		
		p.setDiceInHand(diceRolled);
		
		frame.showDiceResults(playerID, p.getDiceInHand(), p.getSavedDice(), p.getRound(), p.canPlayerRoll());
		
	}

    private void checkAndIncRound(Player p) {

        int currentRound = p.getRound();
        finalizeRound(currentRound, p.getID());

        p.incrementRound();
    }

    private void finalizeRound(int currentRound, int playerID) {

        if (currentRound > 0)
            frame.roundDone(playerID, currentRound);
    }

	private boolean endGame() {
		
		boolean endGame = true;
		for (Player p : master.getPlayers())
			endGame = endGame && p.isDonePlaying();
		
		return endGame;
	}
	
	private Player whoLeads() {
		
		Player leader = master.getPlayerForID(1);
		for (Player p : master.getPlayers())
			leader = p.getScore() > leader.getScore() ? p : leader;
			
		return leader;
	}
	
	public void useCombination(int playerID, Combination comb) {
		
		Player p = master.getPlayerForID(playerID);
		
		/* Quite ugly, if no rolls have been made, no score can be claimed. TODO disable combination button. */
		if (p.getDiceInHand() == null)
			return;

		if (calculateScore(p, comb) > 0) {
			frame.showScore(playerID, p.getScore());
		}
	}

	private int calculateScore(Player p, Combination comb) {
				
		int score = p.getScore();
		
		/* Get all dice of the player in one place (saved and in hand). */
		ArrayList<Integer> totalDice = new ArrayList<Integer>();
		totalDice.addAll(p.getSavedDice());
		for (int inHand : p.getDiceInHand())
			totalDice.add(inHand);
		
		if (comb.equals(Combination.HOUSE)) {
			score += this.calculateHouseScore(totalDice);
		}
		else {	
			for (int dice : totalDice) {
				if (dice == Combination.getValue(comb))
					score += dice;
			}
		}

		return setScoreandUseCombination(score, comb, p);
	}

	private int setScoreandUseCombination(int score, Combination comb, Player p) {

		if (score == p.getScore()) {
			return 0;
		}
		else {
			p.setScore(score);
			p.useCombination(comb);

			return score;
		}
	}

	private int calculateHouseScore(List<Integer> totalDice) {
		
		int score = 0;

		HashSet<Integer> setOfDice = new HashSet<Integer>(totalDice);
		int diceTypes = setOfDice.size();
		
		boolean validHouse = checkValidHouse(diceTypes, totalDice);

		if (validHouse) {
			return computeHouseScore(setOfDice, totalDice);
		}
		
		return score;
	}

	private boolean checkValidHouse(int diceTypes, List<Integer> totalDice) {

		boolean validHouse = false;

		if (diceTypes == 2) {
			int pairOrTriss = Collections.frequency(totalDice, totalDice.get(0));
			validHouse = !validHouse && (pairOrTriss == 2 || pairOrTriss == 3);
		}

		return validHouse;
	}

	private int computeHouseScore(HashSet<Integer> setOfDice, List<Integer> totalDice) {

		int score = 0;

		for (int type : setOfDice) {
			score += (Collections.frequency(totalDice, type) * type);
		}

		return score;
	}

	public Set<Combination> getAvailableCombinations(int playerID) {
			
		return master.getPlayerForID(playerID).getCombinationsLeft();
	}

	public void playerDone(int playerID) {
		
		Player p = master.getPlayerForID(playerID);
		p.setDonePlaying();
		
		int round = p.getRound();
		if(round > 0) 
			frame.roundDone(playerID, p.getRound());
	}

	public void checkEndGame() {
		
		Player leader = this.whoLeads();
		if (endGame())
			frame.endGame(leader.getID(), leader.getScore());
	}
}
