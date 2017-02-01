package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import master.GameMaster;
import master.GameMaster.Combination;
import master.GameMaster.DiceType;
import master.Player;
import ui.MainFrame;

public class ControllerTest {

	GameMaster master;
	MainFrame view;
	Controller c;
	
	Player p1;
	Player p2;
	
	@Before
	public void setUp() throws Exception {
		this.master = new GameMaster(DiceType.D6);
		this.view = new MainFrame();
		this.c = new Controller(master, view);
		this.master.setPlayers(2);
		
		this.p1 = this.master.getPlayerForID(1);
		this.p2 = this.master.getPlayerForID(2);
				
		this.view.setController(this.c);
		this.view.initialize();
		this.view.addPlayer(1, 3);
		this.view.addPlayer(2, 3);		
	}

	@Test
	public void testGeneral() {
		
		assertTrue(c.getNrOfRounds() > 0);
		assertNotNull(c.getAvailableCombinations(1));
		assertTrue(c.getAvailableCombinations(1).size() == 7);
	}
	
	/**
	 * One player with a valid house, another one without.
	 */
	@Test
	public void testHouse() {
		
		/* Has a house. */
		this.p1.setDiceInHand(new int[]{2,2,2,3,3});
		
		/* Does not have a house. */
		this.p2.setDiceInHand(new int[]{1,2,3,4,5});
		
		this.c.useCombination(p1.getID(), Combination.HOUSE);
		this.c.useCombination(p2.getID(), Combination.HOUSE);
		
		assertTrue(p1.getScore() == 12);
		assertTrue(p2.getScore() == 0);
	}
	
	@Test
	public void testDiceRoll() {
		
		this.c.rollDice(1);
		
		assertNotNull(p1.getDiceInHand());
		assertTrue(p1.getRound() == 1);	
	}

}
