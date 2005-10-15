
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class LameArgsDialog {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="157,47"
	private Label label = null;
	private Text text = null;
	private Label label1 = null;
	private Label label2 = null;
	private Label label3 = null;
	private Label label4 = null;
	private Button button = null;
	private Button button1 = null;
	private String args;
	
	public LameArgsDialog(String args) {
		createSShell();
		text.setText(args);
		this.args = args;
	}
	
	public String getArgs() {
		sShell.open();
		 while (!sShell.isDisposed()) {
			 if (!sShell.getDisplay().readAndDispatch ()) sShell.getDisplay().sleep ();
		 }
		 return args;
	}
	
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.heightHint = -1;
		gridData3.widthHint = 80;
		gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.widthHint = 80;
		gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalSpan = 4;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		sShell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.ON_TOP);
		sShell.setText("LAME args");
		sShell.setLayout(gridLayout);
		sShell.setSize(new org.eclipse.swt.graphics.Point(436,131));
		label = new Label(sShell, SWT.WRAP);
		label.setText("Please input the arguments you want LAME to use for encoding");
		label.setLayoutData(gridData);
		label2 = new Label(sShell, SWT.NONE);
		label2.setText("Arguments:");
		label1 = new Label(sShell, SWT.NONE);
		text = new Text(sShell, SWT.BORDER);
		text.setLayoutData(gridData1);
		label3 = new Label(sShell, SWT.NONE);
		label4 = new Label(sShell, SWT.NONE);
		button1 = new Button(sShell, SWT.NONE);
		button1.setText("OK");
		button1.setLayoutData(gridData3);
		button1.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				args = text.getText();
				sShell.dispose();
			}
		});
		button = new Button(sShell, SWT.NONE);
		button.setText("Cancel");
		button.setLayoutData(gridData2);
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				sShell.dispose();
			}
		});
	}

}
