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
	
	/**
	 * Accumulated score.
	 */
	private int score;
	
	/**
	 * Dice stored.
	 */
	private List<Integer> savedDice;
	
	/**
	 * Dice just recently thrown.
	 */
	private int[] diceInHand;
	
	/**
	 * Dice that can be thrown again.
	 */
	private int diceLeft;
	
	/**
	 * When no more combinations can be chosen, user is done.
	 */
	private boolean donePlaying;
	
	/**
	 * The remaining combinations for claiming points.
	 */
	private EnumSet<Combination> combinations;
	
	/**
	 * Single constructor.
	 * TODO get name as well?
	 * 
	 * @param id		unique to this player. Must be 0 or larger.
	 * @param diceLeft	total amount of dice to throw. Must be 1 or larger.
	 * @param rounds	rounds this <code>Player</code> can participate in. Must be 1 or larger.
	 */
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

	/**
	 * A combination is spent.
	 * 
	 * @param comb	the <code>Combination</code> to spend. Can not be <code>null</code>.
	 */
	public void useCombination(Combination comb) {
		if (comb == null)
			throw new IllegalArgumentException();
		
		this.combinations.remove(comb);
	}
	
	/**
	 * The player is done.
	 */
	public void setDonePlaying() {
		this.donePlaying = true;
		this.clearDice();
	}
	
	/**
	 * Whether or not the player is done.
	 * 
	 * @return	<code>true</code> if the <code>Player</code> is done.
	 * 			<code>false</code> otherwise.
	 */
	public boolean isDonePlaying() {
		return this.donePlaying;
	}
	
	/**
	 * Determines whether or not the player is allowed to roll again.
	 * 
	 * @return <code>true</code> if this player has another round to participate in.<br>
	 * 			<code>false</code> if not.
	 */
	public boolean canRoll() {
		return !(this.currentRound >= this.rounds);
	}
	
	/**
	 * Current round player is joining.
	 * 
	 * @return	the round in which to throw dice the next time.
	 */
	public int getRound() {
		return this.currentRound;
	}
	
	/**
	 * A round is played, positioning for the next one.
	 */
	public void incRound() {
		this.currentRound++;
	}
	
	/**
	 * How many dice can be thrown? i.e. not yet saved dice.
	 * 
	 * @return	number of dice to roll.
	 */
	public int getDiceLeft() {
		return diceLeft;
	}
	
	/**
	 * Save values of rolled dice, decrement dice left.
	 * 
	 * @param result	result of the dice to save.
	 * @throws IllegalStateException	if all dice already saved.
	 */
	public void saveDice(int result) throws IllegalStateException {
		if (diceLeft < 0)
			throw new IllegalStateException();
		
		this.savedDice.add(result);
		this.diceLeft--;
	}
	
	/**
	 * Which combinations can be used to claim points?
	 * 
	 * @return	the <code>Combination</code>s that haven't been spent yet.
	 */
	public EnumSet<Combination> getCombinationsLeft() {
		return this.combinations;
	}
	
	/**
	 * 
	 * @return	a list of the saved dice.
	 */
	public List<Integer> getSavedDice() {
		return this.savedDice;
	}
	
	/**
	 * Sets dice left, dice in hand and saved dice to 0/empty.
	 */
	private void clearDice() {
		
		this.diceInHand = new int[]{0};
		this.savedDice.clear();
		this.diceLeft = 0;
	}

	/**
	 * 
	 * @return the unique identifier of this <code>Player</code>.
	 */
	public int getID() {
		return id;
	}

	/**
	 * 
	 * @return the total accumulated score of this <code>Player</code>.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Updates the score for this <code>Player</code>.
	 * 
	 * @param score new score. Must be larger than 0.
	 */
	public void setScore(int score) {
		if (score < 0)
			throw new IllegalArgumentException();
		
		this.score = score;
	}

	/**
	 * 
	 * @return the dice recently thrown by this <code>Player</code>.
	 */
	public int[] getDiceInHand() {
		return diceInHand;
	}

	/**
	 * New dice thrown, these are the recent ones.
	 * 
	 * @param diceInHand	dice thrown in the latest round.
	 */
	public void setDiceInHand(int[] diceInHand) {
		this.diceInHand = diceInHand;
	}
}
