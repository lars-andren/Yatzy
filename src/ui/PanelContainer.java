package ui;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * Helper class to get references to the grid in a panel.
 * 
 * @author Lars
 *
 */
public class PanelContainer {

	private JPanel panel;
	private JPanel[][] panelMatrix;
	
	/**
	 * PanelContainer creates JPanel grid that is mapped to the panel.
	 * 
	 * @param panel	the actual JPanel. Can not be <code>null</code>.
	 * @param rows	the number of rows in the panel. Must be greater than 0.
	 * @param columns	the number of columns in the panel. Must be greater than 0.
	 */
	public PanelContainer(JPanel panel, int rows, int columns) {
		if (panel == null || rows < 1 || columns < 1)
			throw new IllegalArgumentException();
		
		this.panel = panel;
		this.panel.setLayout(new GridLayout(rows, columns));
		
		this.panelMatrix = new JPanel[rows][columns];
		
		for(int m = 0; m < rows; m++) {
			   for(int n = 0; n < columns; n++) {
			      this.panelMatrix[m][n] = new JPanel();
			      this.panel.add(panelMatrix[m][n]);
			   }
		}
	}
	
	/**
	 * Retrieves the panels reference grid. Only for the view.
	 * 
	 * @return the reference-panels.
	 */
	JPanel[][] getMatrix() {
		return this.panelMatrix;
	}
	
	/**
	 * Retrieves the actual JPanel. Only for the view.
	 * 
	 * @return the actual overview JPanel.
	 */
	JPanel getPanel() {
		return this.panel;
	}
}
