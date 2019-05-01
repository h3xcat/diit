package invisibleinktoolkit.algorithms.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class LSBMatchPanel extends JPanel{
	
	//VARIABLES

	/**
	 * Generated Serialisation ID.
	 */
	private static final long serialVersionUID = -5347327864521430768L;
	
	/**
	 * Whether to match LSB or not.
	 */
	private JCheckBox mShouldMatch;

	//CONSTRUCTORS
	
	/**
	 * Creates a new LSB Matching Panel.
	 * 
	 * @param listener The listener for check box change events.
	 * @param match Whether match should be set by default.
	 */
	public LSBMatchPanel(ActionListener listener, boolean match){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Should this algorithm use LSB Matching?"));
		this.setPreferredSize(new Dimension(600,50));
		mShouldMatch = new JCheckBox("Use LSB Matching?", match);
		JPanel spacer = new JPanel();
		this.add(mShouldMatch);
		this.add(spacer);
		mShouldMatch.addActionListener(listener);
	}
	
	//FUNCTIONS
	
	/**
	 * Whether we should match LSB or not.
	 */
	public boolean shouldMatch(){
		return mShouldMatch.isSelected();
	}
	
}
