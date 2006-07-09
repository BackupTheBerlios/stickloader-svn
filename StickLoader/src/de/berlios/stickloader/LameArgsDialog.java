/* Stickloader - http://stickloader.berlios.de
 * 
 * Created by Alexander Kaiser <mail@alexkaiser.de>
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

public class LameArgsDialog {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="197,15"
	private Button OKbutton = null;
	private Button radioVbrMedium = null;
	private Button radioVbrStandard = null;
	private Button radioVbrExtreme = null;
	private Button radioInsane = null;
	private Button radioABR = null;
	private Button radioExpert = null;
	private Text textArgs = null;
	private String args = "";
	private CLabel cLabel = null;
	private Button checkBoxFast = null;
	private CLabel cLabel1 = null;
	private Scale scaleBitRate = null;
	private Label labelBitRate = null;
	private Button radioCbr = null;
	private CLabel cLabel2 = null;
	private Label label = null;
	public LameArgsDialog(String args) {
		createSShell();
		this.args = args;
		if (args == "") {
			radioVbrStandard.setSelection(true);
		} else {
			//TODO
			radioExpert.setSelection(true);
		}
		textArgs.setText(args);
		updateUI();
	}
	
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData11 = new GridData();
		gridData11.horizontalSpan = 2;
		gridData11.verticalAlignment = GridData.END;
		gridData11.grabExcessVerticalSpace = true;
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.horizontalAlignment = GridData.BEGINNING;
		GridData gridData31 = new GridData();
		gridData31.horizontalSpan = 3;
		GridData gridData21 = new GridData();
		gridData21.horizontalSpan = 2;
		GridData gridData27 = new GridData();
		gridData27.verticalAlignment = GridData.CENTER;
		gridData27.heightHint = -1;
		gridData27.horizontalAlignment = GridData.FILL;
		GridData gridData12 = new GridData();
		gridData12.horizontalSpan = 3;
		GridData gridData9 = new GridData();
		gridData9.horizontalSpan = 3;
		GridData gridData8 = new GridData();
		gridData8.horizontalSpan = 2;
		GridData gridData61 = new GridData();
		gridData61.widthHint = 20;
		GridData gridData5 = new GridData();
		gridData5.horizontalSpan = 3;
		GridData gridData4 = new GridData();
		gridData4.horizontalSpan = 2;
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 3;
		GridData gridData2 = new GridData();
		gridData2.horizontalSpan = 3;
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
		sShell = new Shell();
		sShell.setText("Select Encoding Parameters");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(279, 374));
		cLabel1 = new CLabel(sShell, SWT.NONE);
		cLabel1.setText("Presets");
		cLabel1.setLayoutData(gridData12);
		radioVbrMedium = new Button(sShell, SWT.RADIO);
		radioVbrMedium.setText("VBR Medium");
		radioVbrMedium.setLayoutData(gridData1);
		radioVbrMedium
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						updateUI();
					}
				});
		radioVbrStandard = new Button(sShell, SWT.RADIO);
		radioVbrStandard.setText("VBR Standard");
		radioVbrStandard.setLayoutData(gridData2);
		radioVbrStandard
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						updateUI();
					}
				});
		radioVbrExtreme = new Button(sShell, SWT.RADIO);
		radioVbrExtreme.setText("VBR Extreme");
		radioVbrExtreme.setLayoutData(gridData9);
		radioVbrExtreme
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						updateUI();
					}
				});
		cLabel = new CLabel(sShell, SWT.NONE);
		cLabel.setText(" ");
		cLabel.setLayoutData(gridData61);
		checkBoxFast = new Button(sShell, SWT.CHECK);
		checkBoxFast.setText("Faster encoding (only for VBR presets)");
		checkBoxFast.setLayoutData(gridData8);
		radioInsane = new Button(sShell, SWT.RADIO);
		radioInsane.setText("CBR Insane (320 kbps)");
		radioInsane.setLayoutData(gridData3);
		cLabel2 = new CLabel(sShell, SWT.NONE);
		cLabel2.setText("Specify bit rate");
		cLabel2.setLayoutData(gridData31);
		radioInsane.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
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

		Label filler28 = new Label(sShell, SWT.NONE);
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
		Label filler3 = new Label(sShell, SWT.NONE);
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
				sShell.dispose();
			}
		});
	}
	
	private int updateScale() {
		int [] bitrates;
		if (radioABR.getSelection()) bitrates = new int [] {32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320};
			else if (radioCbr.getSelection()) bitrates = new int [] {80, 96, 112, 128, 160, 192, 224, 256, 320};
				else return 0;
		
		int bitrate = bitrates[scaleBitRate.getSelection()/10];

		labelBitRate.setText(bitrate + " kbps");
		
		return bitrate;
	}
	
	private void updateUI() {
		String args = "--preset ";
		
		if (!radioVbrExtreme.getSelection() && !radioVbrMedium.getSelection() && !radioVbrStandard.getSelection()) {
			checkBoxFast.setEnabled(false);
		} else {
			checkBoxFast.setEnabled(true);
			if (checkBoxFast.getSelection()) {
				args += "fast ";
			}
		} 
		
		if (!radioABR.getSelection() && !radioCbr.getSelection()) scaleBitRate.setEnabled(false);
			else {
				scaleBitRate.setEnabled(true);
				if (radioCbr.getSelection()) args += "cbr ";
				args += updateScale() + " ";
			}
		
		if (radioVbrExtreme.getSelection()) args += "extreme ";
		if (radioVbrStandard.getSelection()) args += "standard ";
		if (radioVbrMedium.getSelection()) args += "medium ";
		if (radioInsane.getSelection()) args += "insane";
		//if (radioABR.getSelection()) args += textAbrBitrate.getText();
		
		//textAbrBitrate.setEnabled(radioABR.getSelection());
		
		textArgs.setEnabled(radioExpert.getSelection());		
		
		if (!radioExpert.getSelection()) {
			textArgs.setText(args);
		}
		
		this.args = textArgs.getText().trim();
	}
	
	public String getArgs(int x, int y) {
		
		sShell.open();
		 while (!sShell.isDisposed()) {
			 if (!sShell.getDisplay().readAndDispatch ()) sShell.getDisplay().sleep ();
		 }
		 return args;
	}
}
