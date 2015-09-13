package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import master.GameMaster;
import master.GameMaster.DiceType;
import ui.MainFrame;

public class TestGameMaster {

	GameMaster master;
	Controller c;

	@Before
	public void setUp() throws Exception {
		this.master = new GameMaster(DiceType.D6);
		MainFrame view = new MainFrame();
		this.c = new Controller(this.master, view);
		this.master.setPlayers(2);
	}
	
	@Test
	public void test() {
		
		assertNull(master.getPlayerForID(1).getDiceInHand());
		assertTrue(master.getPlayerForID(2).getSavedDice().size() == 0);
		
	}

}
