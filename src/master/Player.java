package master;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import master.GameMaster.Combination;

/**
 * A participant in the game. Part of the Model concept.
 * 
 * @author Lars
 *
 */
public class Player {
	
	/* ID is unique identifier. */
	private int id;
	private String name;
	
	/* Each player's rounds left to play. */
	private int currentRound;
	private int rounds;

	private int score;

	private List<Integer> savedDice;

	private int[] diceInHand;

	private int diceLeft;

	private boolean donePlaying;

	private EnumSet<Combination> combinations;

	public Player(int id, int diceLeft, int rounds) {
		if (id < 0 || diceLeft < 1 || rounds < 1)
			throw new IllegalArgumentException();
		
		this.id = id;
		this.savedDice = new ArrayList<Integer>();
		this.diceLeft = diceLeft;
		this.donePlaying = false;
		
		this.currentRound = 0;
		this.rounds = rounds;
		this.setScore(0);
		
		this.combinations = EnumSet.allOf(Combination.class);
	}

	public void useCombination(Combination comb) {
		
		this.combinations.remove(comb);
	}

	public void setDonePlaying() {
		this.donePlaying = true;
		this.clearDice();
	}

	public boolean isDonePlaying() {
		return this.donePlaying;
	}

	public boolean canPlayerRoll() {
		return !(this.currentRound >= this.rounds);
	}

	public int getRound() {
		return this.currentRound;
	}

	public void incrementRound() {
		this.currentRound++;
	}

	public int getDiceLeft() {
		return diceLeft;
	}

	public void saveDice(int result) throws IllegalStateException {
		
		this.savedDice.add(result);
		this.diceLeft--;
	}

	public EnumSet<Combination> getCombinationsLeft() {
		return this.combinations;
	}

	public List<Integer> getSavedDice() {
		return this.savedDice;
	}

	private void clearDice() {
		
		this.diceInHand = new int[]{0};
		this.savedDice.clear();
		this.diceLeft = 0;
	}

	public int getID() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		if (score < 0)
			throw new IllegalArgumentException();
		
		this.score = score;
	}

	public int[] getDiceInHand() {
		return diceInHand;
	}

	public void setDiceInHand(int[] diceInHand) {
		this.diceInHand = diceInHand;
	}
}
