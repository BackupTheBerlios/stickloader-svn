/* Stickloader - http://stickloader.berlios.de
 * 
 * Created by Alexander Kaiser <groer@users.berlios.de>
 *
 * Copyright (C) 2006 Alexander Kaiser, All rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.berlios.stickloader;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Scale;

import sun.security.krb5.internal.s;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Combo;

public class LameArgsDialog {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="198,42"
	private Button OKbutton = null;
	private Button radioPresets = null;
	private Button radioABR = null;
	private Button radioExpert = null;
	private Text textArgs = null;
	private String args = "";  //  @jve:decl-index=0:
	private Button checkBoxFast = null;
	private Scale scaleBitRate = null;
	private Label labelBitRate = null;
	private Button radioCbr = null;
	private CLabel cLabel2 = null;
	private Label label = null;
	private Label label1 = null;
	private Combo comboQuality = null;
	
	/** Presets */
	public final String HIGH_QUALITY = "High quality, but slow";
	public final String STANDARD_QUALITY = "Default";
	public final String FAST_QUALITY = "Fast, but low quality";
	
	public final String PRESET_MEDIUM = "Medium quality (approx. 165 kbps)";
	public final String PRESET_STANDARD = "Standard quality (approx. 190 kbps)";
	public final String PRESET_EXTREME = "Very high quality (extreme, ~245 kbps)";  //  @jve:decl-index=0:
	public final String PRESET_INSANE = "Highest quality (insane, 320 kbps)";
	
	private Combo comboPresets = null;
	private Button radioVbr = null;
	public LameArgsDialog(String args, int x, int y) {
		createSShell();
		this.args = args;
		if (!args.equals("")) {
			//TODO
			radioExpert.setSelection(true);
		}
		textArgs.setText(args);
		sShell.setLocation(x, y);
		updateUI();		
	}
	
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.horizontalAlignment = GridData.FILL;
		GridData gridData12 = new GridData();
		gridData12.horizontalSpan = 3;
		GridData gridData13 = new GridData();
		gridData13.verticalAlignment = GridData.CENTER;
		gridData13.grabExcessHorizontalSpace = true;
		gridData13.horizontalSpan = 3;
		gridData13.horizontalAlignment = GridData.FILL;
		GridData gridData11 = new GridData();
		gridData11.horizontalSpan = 2;
		gridData11.verticalAlignment = GridData.END;
		gridData11.grabExcessVerticalSpace = true;
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.horizontalAlignment = GridData.BEGINNING;
		GridData gridData31 = new GridData();
		gridData31.horizontalSpan = 3;
		GridData gridData21 = new GridData();
		gridData21.horizontalSpan = 3;
		GridData gridData27 = new GridData();
		gridData27.verticalAlignment = GridData.CENTER;
		gridData27.heightHint = -1;
		gridData27.horizontalAlignment = GridData.FILL;
		GridData gridData8 = new GridData();
		gridData8.horizontalSpan = 3;
		GridData gridData5 = new GridData();
		gridData5.horizontalSpan = 3;
		GridData gridData4 = new GridData();
		gridData4.horizontalSpan = 3;
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 3;
		GridData gridData25 = new GridData();
		gridData25.horizontalAlignment = GridData.FILL;
		gridData25.horizontalSpan = 2;
		gridData25.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.verticalAlignment = GridData.END;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.widthHint = 70;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		int shellProperties = SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM;
		if (Utils.isWindows()) shellProperties = shellProperties | SWT.ON_TOP;

		sShell = new Shell(shellProperties);
		sShell.setText("Select Encoding Parameters");
		sShell.setLayout(gridLayout);
		radioPresets = new Button(sShell, SWT.RADIO);
		radioPresets.setText("Use LAME presets");
		radioPresets.setLayoutData(gridData1);
		Label filler = new Label(sShell, SWT.NONE);
		createComboPresets();
		radioPresets
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						updateUI();
					}
				});
		cLabel2 = new CLabel(sShell, SWT.NONE);
		cLabel2.setText("Specify quality/bit rate:");
		cLabel2.setLayoutData(gridData31);
		radioVbr = new Button(sShell, SWT.RADIO);
		radioVbr.setText("Variable Bit Rate (VBR)");
		radioVbr.setLayoutData(gridData12);
		radioVbr.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (!radioVbr.getSelection()) return;
				scaleBitRate.setMaximum(90);
				updateUI();
			}
		});
		radioABR = new Button(sShell, SWT.RADIO);
		radioABR.setText("Average Bit Rate (ABR)");
		radioABR.setLayoutData(gridData4);
		radioABR.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (!radioABR.getSelection()) return;
				if (scaleBitRate.getMaximum() < 130) {
					scaleBitRate.setMaximum(130);
					scaleBitRate.setSelection(scaleBitRate.getSelection() + 50);
				}
				
				updateUI();
			}
		});

		radioCbr = new Button(sShell, SWT.RADIO);
		radioCbr.setText("Constant Bit Rate (CBR)");
		radioCbr.setLayoutData(gridData21);
		radioCbr.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (!radioCbr.getSelection()) return;
				if (scaleBitRate.getMaximum() > 80) scaleBitRate.setSelection(scaleBitRate.getSelection() - 50);
				scaleBitRate.setMaximum(80);
				updateUI();
			}
		});
		Label filler25 = new Label(sShell, SWT.NONE);
		scaleBitRate = new Scale(sShell, SWT.NONE);
		scaleBitRate.setIncrement(50);
		scaleBitRate.setSelection(5);
		scaleBitRate.setMinimum(0);
		scaleBitRate.setMaximum(130);
		scaleBitRate.setLayoutData(gridData27);
		scaleBitRate
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						updateUI();
					}
				});
		labelBitRate = new Label(sShell, SWT.NONE);
		labelBitRate.setText("128 kbps");

		labelBitRate.setLayoutData(gridData2);
		checkBoxFast = new Button(sShell, SWT.CHECK);
		checkBoxFast.setText("Faster encoding (only for VBR)");
		checkBoxFast.setLayoutData(gridData8);
		label1 = new Label(sShell, SWT.NONE);
		Label filler2 = new Label(sShell, SWT.NONE);
		createComboQuality();
		radioExpert = new Button(sShell, SWT.RADIO);
		radioExpert.setText("Manual (for experts)");
		radioExpert.setLayoutData(gridData5);
		radioExpert.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateUI();
			}
		});
		Label filler123 = new Label(sShell, SWT.NONE);
		textArgs = new Text(sShell, SWT.BORDER);
		textArgs.setLayoutData(gridData25);
		label1.setLayoutData(gridData13);
		label1.setText("Noise shaping and psycho acoustic algorithms:");
		label1.setSize(new Point(144, 13));
		label = new Label(sShell, SWT.NONE);
		label.setText("");
		label.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/de/berlios/stickloader/resources/lamelogo.png")));
		label.setLayoutData(gridData11);
		OKbutton = new Button(sShell, SWT.NONE);
		OKbutton.setText("OK");
		OKbutton.setLayoutData(gridData);
		OKbutton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateUI();
				//sShell.dispose();
				sShell.setVisible(false);
			}
		});
		sShell.setDefaultButton(OKbutton);
		sShell.pack();
		sShell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				e.doit = false;
				sShell.setVisible(false);
			}
		});
	}
	
	/**
	 * Update the scale and bitrate/quality value
	 * @return the currently selected bitrate/quality 
	 */
	private int updateScale() {
		if (radioVbr.getSelection()) {
			int [] bitrates = new int [] {245, 225, 190, 175, 165, 130, 115, 100, 85, 65};
			int quality = scaleBitRate.getSelection()/10;
			String labelText = quality+ (" (~" + bitrates[quality] + " kbps)");
			
			//if (quality == 0) labelText += " (best)";
			//if (quality == 9) labelText += " (fastest)";			
			
			labelBitRate.setText(labelText);
			return quality;
		} else {
			int [] bitrates;
			if (radioABR.getSelection()) bitrates = new int [] {32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320};
				else if (radioCbr.getSelection()) bitrates = new int [] {80, 96, 112, 128, 160, 192, 224, 256, 320};
					else return 0;
			
			int bitrate = bitrates[scaleBitRate.getSelection()/10];
	
			labelBitRate.setText(bitrate + " kbps");
			
			return bitrate;
		}
	}
	
	private void updateUI() {
		String args = "";
		
		// Disabled by default
		checkBoxFast.setEnabled(false);
			
		if (!radioABR.getSelection() && !radioCbr.getSelection() && !radioVbr.getSelection()) scaleBitRate.setEnabled(false);
			else {
				if (!radioVbr.getSelection()) args += "--preset ";
					else {
						args += "-V ";
						checkBoxFast.setEnabled(true);
					}
				scaleBitRate.setEnabled(true);
				if (radioCbr.getSelection()) args += "cbr ";
				args += updateScale() + " ";
			}
		
		if (radioVbr.getSelection() && checkBoxFast.isEnabled()) {
			args += "--vbr-new ";
		}
		
		if (radioPresets.getSelection()) {
			args += "--preset ";
			comboPresets.setEnabled(true);
			checkBoxFast.setEnabled(true);
			if (!comboPresets.getText().equals(PRESET_INSANE) && checkBoxFast.getSelection()) args += "fast ";
			if (comboPresets.getText().equals(PRESET_MEDIUM)) args += "medium ";
			if (comboPresets.getText().equals(PRESET_STANDARD)) args += "standard ";
			if (comboPresets.getText().equals(PRESET_EXTREME)) args += "extreme ";
			if (comboPresets.getText().equals(PRESET_INSANE)) args += "insane ";			
		} else {
			comboPresets.setEnabled(false);
		}
			
		if (comboQuality.getText().equals(HIGH_QUALITY)) args += "-h ";
		if (comboQuality.getText().equals(FAST_QUALITY)) args += "-f ";
		
		textArgs.setEnabled(radioExpert.getSelection());
		comboQuality.setEnabled(!radioExpert.getSelection());
		
		if (!radioExpert.getSelection()) {
			textArgs.setText(args);
		}
		
		this.args = textArgs.getText().trim();
	}
	
	public void show() {
		sShell.open();
		 while (sShell.isVisible()) {
			 if (!sShell.getDisplay().readAndDispatch ()) sShell.getDisplay().sleep ();
		 }
	}
	
	public String getArgs() {
		return args;
	}


	/**
	 * This method initializes comboQuality	
	 *
	 */
	private void createComboQuality() {
		GridData gridData6 = new GridData();
		gridData6.verticalAlignment = GridData.CENTER;
		gridData6.horizontalSpan = 2;
		gridData6.horizontalAlignment = GridData.FILL;
		comboQuality = new Combo(sShell, SWT.READ_ONLY);
		comboQuality.add(HIGH_QUALITY);
		comboQuality.add(STANDARD_QUALITY);
		comboQuality.add(FAST_QUALITY);
		comboQuality.setText(STANDARD_QUALITY);
		comboQuality.setLayoutData(gridData6);
		comboQuality
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						updateUI();
					}
					public void widgetDefaultSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						//...
					}				
				});
		
	}


	/**
	 * This method initializes comboPresets	
	 *
	 */
	private void createComboPresets() {
		GridData gridData7 = new GridData();
		gridData7.horizontalSpan = 2;
		gridData7.verticalAlignment = GridData.CENTER;
		gridData7.horizontalAlignment = GridData.FILL;
		comboPresets = new Combo(sShell, SWT.READ_ONLY);
		comboPresets.add(PRESET_MEDIUM);
		comboPresets.add(PRESET_STANDARD);
		comboPresets.add(PRESET_EXTREME);
		comboPresets.add(PRESET_INSANE);
		comboPresets.setText(PRESET_STANDARD);
		comboPresets.setLayoutData(gridData7);
		comboPresets
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						updateUI();
					}
					public void widgetDefaultSelected(
							org.eclipse.swt.events.SelectionEvent e) {
					}
				});
	}
}
