package master;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Keeps track of the number of rounds, the players, the scores, saved dice etc.
 * 
 * @author Lars
 *
 */
public class GameMaster {

	/**
	 * To keep track of players.
	 */
	private ArrayList<Player> players = new ArrayList<Player>();
	private HashMap<Integer, Player> playerMap = new HashMap<Integer, Player>();
	
	/* Default is D6, but maybe in the future..*/
	private DiceType diceType;
	
	/* TODO get from user/resource */
	private final int TOTAL_DICE = 5;
	private final int TOTAL_ROUNDS = 3;
	
	/**
	 * Sets the type of dice to use.
	 * 
	 * @param rounds
	 */
	public GameMaster(DiceType type) {
		
		this.diceType = type;
	}
	
	public Player getPlayerForID(int playerID) {
		return this.playerMap.get(playerID);
	}
	
	public DiceType getDiceType() {
		return this.diceType;
	}
	
	/**
	 * 
	 * @param players checked by Controller to be OK.
	 */
	public void setPlayers(int players) {
		
		/* First id is 1. */
		for (int i=1; i < players+1; i++) {
			this.createPlayer(i);
		}
	}
	
	/**
	 * 
	 * @return the <code>Player</code> in ID ascending order.
	 */
	public ArrayList<Player> getPlayers() {
		return this.players;
	}
	
	/**
	 * 
	 * @return rounds of the game.
	 */
	public int getNrOfRounds() {
		return TOTAL_ROUNDS;
	}
	
	/**
	 * Creates a new player and adds hen to the set of players.
	 * 
	 * @param id	unique identifer.
	 */
	private Player createPlayer(int id) {
		
		Player newPlayer = new Player(id, this.TOTAL_DICE, this.TOTAL_ROUNDS);
		this.playerMap.put(id, newPlayer);
		this.players.add(newPlayer);
		
		return newPlayer;
	}
	
	
	/**
	 * Regular Yatzy combinations.
	 * 
	 * @author Lars
	 *
	 */
	public enum Combination {
		
		ONES(1, "Ones"),
		TWOS(2, "Twos"),
		THREES(3, "Threes"),
		FOURS(4, "Fours"),
		FIVES(5, "Fives"),
		SIXES(6, "Sixes"),
		HOUSE(0, "House");
		
		private int value;
		private String name;
		
		private Combination(final int value, final String name) {
			this.value = value;
			this.name = name;
		}
		
		private static HashMap<Combination, Integer> valueMap = new HashMap<Combination, Integer>();
		private static HashMap<Combination, String> nameMap = new HashMap<Combination, String>();
		
		static {
			for (Combination comb : Combination.values()) {
				valueMap.put(comb, comb.value);
			}
			for (Combination comb : Combination.values()) {
				nameMap.put(comb, comb.name);
			}
		}
		
		public static int getValue(Combination comb) {
			
			return valueMap.get(comb);
		}
		
		public static String getName(Combination comb) {
			return nameMap.get(comb);
		}
	}
	
	/**
	 * If D6 gets too boring...
	 * 
	 * @author Lars
	 *
	 */
	public enum DiceType  {
		
		D6(6),
		D10(10),
		D25(25),
		D100(100);
		
		private int max;
		
		private DiceType(final int max) {
			this.max = max;
		}
		
		private static HashMap<DiceType, Integer> maxMap = new HashMap<DiceType, Integer>();
		
		static {
			for (DiceType dice : DiceType.values()) {
				maxMap.put(dice, dice.max);
			}
		}
		
		public static int getMax(DiceType dice) {
			
			return maxMap.get(dice);	
		}
	}
	
}
