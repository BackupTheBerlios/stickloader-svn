/* Stickloader - http://stickloader.berlios.de
 * Created by Alexander Kaiser <groer@users.berlios.de>
 *
 * Copyright (C) 2005 Alexander Kaiser, All rights Reserved
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

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Point;

public class AboutDialog {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="84,16"
	private Label icon = null;
	private Label name = null;
	private Text textArea = null;
	private Button button = null;
	private Label label = null;
	private Label link = null;
	private Label labelLame = null;

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.BEGINNING;
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.grabExcessVerticalSpace = true;
		gridData11.verticalAlignment = GridData.END;
		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		gridData2.horizontalAlignment = GridData.END;
		gridData2.widthHint = 100;
		gridData2.verticalAlignment = GridData.END;
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.verticalSpan = 2;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 350;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		sShell = new Shell(SWT.DIALOG_TRIM);
		sShell.setText("About Stickloader");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(533, 381));
		icon = new Label(sShell, SWT.NONE);
		icon.setText("Label");
		icon.setLayoutData(gridData1);
		icon.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/de/berlios/stickloader/resources/icon.png")));
		name = new Label(sShell, SWT.NONE);
		name.setText(StickLoader.NAME + " v" + StickLoader.VERSION);
		name.setFont(new Font(Display.getDefault(), "Tahoma", 18, SWT.BOLD));
		textArea = new Text(sShell, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		textArea.setText("Copyright (C) 2005, 2006 Alexander Kaiser\n\n" +
				"This program includes software developed by the Eclipse Foundation (http://www.eclipse.org) (c) 2000-2005 Eclipse contributors and others.\n" +
				"The icon is taken from the Nuvola Icon Theme (c) 2004-2005 VIGNONI DAVID (http://www.icon-king.com)\n" +
				"For ID3 functionality mp3info by Florian Heer (http://www.ueberdosis.de/java/id3.html) is used.\n" +
				"LAME (http://lame.sf.net) is used for MP3 file encoding.\n"+
				"\n" +
				"This program is free software; you can redistribute " +
				"it and/or modify it under the terms of the GNU General " +
				"Public License as published by the Free Software Foundation; " +
				"either version 2 of the License, or (at your option) any " +
				"later version. This program is distributed in the hope that " +
				"it will be useful, but WITHOUT ANY WARRANTY; without even the " +
				"implied warranty of MERCHANTABILITY or FITNESS FOR A " +
				"PARTICULAR PURPOSE. See the GNU General Public License " +
				"for more details.");
		textArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		textArea.setLayoutData(gridData);
		label = new Label(sShell, SWT.NONE);
		link = new Label(sShell, SWT.NONE);
		link.setText("Project homepage: http://stickloader.berlios.de");
		labelLame = new Label(sShell, SWT.NONE);
		labelLame.setText("");
		labelLame.setLayoutData(gridData11);
		labelLame.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/de/berlios/stickloader/resources/lamelogo.png")));
		button = new Button(sShell, SWT.NONE);
		button.setText("OK");
		button.setLayoutData(gridData2);
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				sShell.close();
			}
		});
		sShell.setDefaultButton(button);
	}
	
	public void show() {
		createSShell();
		sShell.pack();
		sShell.open();
	}

}
