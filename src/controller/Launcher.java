package controller;

import master.GameMaster;
import master.GameMaster.DiceType;
import ui.MainFrame;

public class Launcher {

	public static void main(String[] args) {
		
		MainFrame mainFrame = new MainFrame();
		GameMaster master = new GameMaster(DiceType.D6);
		Controller controller = new Controller(master, mainFrame);
		mainFrame.setController(controller);
		
		new Thread(controller).start();
		
	}
	
}
