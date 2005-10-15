import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TestClass extends JFrame {

	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JLabel encodingFileLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	/**
	 * This is the default constructor
	 */
	public TestClass() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
		this.setSize(new java.awt.Dimension(289,134));
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.gridy = 3;
			jLabel4 = new JLabel();
			jLabel4.setText("JLabel");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 2;
			jLabel3 = new JLabel();
			jLabel3.setText("JLabel");
			jLabel3.setMinimumSize(new java.awt.Dimension(80,16));
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			jLabel2 = new JLabel();
			jLabel2.setText("Now Copying:");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("JLabel");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			encodingFileLabel = new JLabel();
			encodingFileLabel.setText("JLabel");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			jContentPane.add(jLabel, gridBagConstraints);
			jLabel = new JLabel();
			jLabel.setText("Now Encoding:");
			jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBorder(null);
			jContentPane.add(encodingFileLabel, gridBagConstraints1);
			jContentPane.add(jLabel3, gridBagConstraints4);
			jContentPane.add(jLabel1, gridBagConstraints2);
			jContentPane.add(jLabel2, gridBagConstraints3);
			jContentPane.add(jLabel4, gridBagConstraints5);
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="108,67"
