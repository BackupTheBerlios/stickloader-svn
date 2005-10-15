
import java.io.File;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.part.DrillDownComposite;

public class JMp3 {
	
	private static JMp3 theApp;

	private FileQueue encodingQueue;
	private FileQueue copyQueue;
	
	private FileProcessor encoder;
	private FileProcessor copier;
	
	final static public File TMP_DIR = new File("D:\\temp");
	final static public File DEST_DIR = new File("D:\\target");
	
	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="155,27"
	private Label label = null;
	private Label encodedFileNameLabel = null;
	private Label encodedFilesLabel = null;
	private Label label2 = null;
	private Label copyFileNameLabel = null;
	private Label copyFilesLabel = null;
	
	boolean running;
	private ProgressBar encodingProgress = null;
	private ProgressBar copyingProgress = null;
	private ExpandableComposite composite = null;

	private Label label3 = null;

	private Label label1 = null;

	private List debugList = null;

	public JMp3() {
		theApp = this;
		
		encodingQueue = new FileQueue();
		copyQueue = new FileQueue();
		

		encoder = new Mp3Encoder(TMP_DIR);
		copier = new FileCopier(TMP_DIR, DEST_DIR);
		
		createSShell();
		sShell.open();
		
		addDrop();

		getEncodeThread().start();
		getCopyThread().start();
		getUIThread().start();
		
		 while (!sShell.isDisposed()) {
			 if (!sShell.getDisplay().readAndDispatch ()) sShell.getDisplay().sleep ();
		 }
	}
	
	private void addDrop() {
		DropTarget dropTarget = new DropTarget(sShell, DND.DROP_MOVE);
	 	final FileTransfer fileTransfer = FileTransfer.getInstance();	 	
	 	dropTarget.setTransfer(new Transfer [] {fileTransfer});
	 	
	 	dropTarget.addDropListener(new DropTargetListener() {
		
			public void dropAccept(DropTargetEvent event) {
				// TODO Auto-generated method stub
		
			}
		
		    public void drop(DropTargetEvent event) {   
				if (fileTransfer.isSupportedType(event.currentDataType)){
			    	   String[] files = (String[])event.data;
			    	     for (String file : files) {
			    	    	 addFile(new File(file));
			    	     }
			    }
		    }
			
			public void dragOver(DropTargetEvent event) {
				// TODO Auto-generated method stub
		
			}
		
			public void dragOperationChanged(DropTargetEvent event) {
				// TODO Auto-generated method stub
		
			}
		
			public void dragLeave(DropTargetEvent event) {
				// TODO Auto-generated method stub
		
			}
		
			public void dragEnter(DropTargetEvent event) {
				// TODO Auto-generated method stub
		
			}
		
		});
	}
	
	private Thread getUIThread() {
		return new Thread() {
			public void run() {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while (!sShell.isDisposed()) {
					sShell.getDisplay().syncExec(new Runnable() {
					
						public void run() {
							encodedFileNameLabel.setText(encoder.getCurrentFilename());
							copyFileNameLabel.setText(copier.getCurrentFilename());
							encodedFilesLabel.setText(encoder.getTotalFiles() + " files encoded, " + encodingQueue.size() + " files waiting");
							copyFilesLabel.setText(copier.getTotalFiles() + " files copied, " + copyQueue.size() + " files waiting");
							encodingProgress.setSelection(encoder.getProgress());
							copyingProgress.setSelection(copier.getProgress());
						}
					});

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	private Thread getCopyThread() {
		return new Thread() {
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while (!sShell.isDisposed()) {
					if (copyQueue.isEmpty())
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else {
						debug("copyQueue not empty");
						Mp3File mp3 = copyQueue.poll();
						if (copier.processFile(mp3)) info("File copied: \"" + mp3.getSrcFile().getName() + "\"");
						else info("File failed: \"" + mp3.getSrcFile().getName() + "\"");
					}
				}
			};
		};
	}
	
	private Thread getEncodeThread() {
		return new Thread() {
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				while (!sShell.isDisposed()) {
					if (encodingQueue.isEmpty())
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else {
						Mp3File mp3 = encodingQueue.poll();
						if (encoder.processFile(mp3)) {
							copyQueue.addFile(mp3);
							info("File encoded: \"" + mp3.getSrcFile().getName() + "\"");
						} else {
							info("File failed: " + mp3.getSrcFile().getName() + "\"");
						}
					}
				}
			}
		};
	}
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData22 = new org.eclipse.swt.layout.GridData();
		gridData22.verticalSpan = 1;
		gridData22.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData22.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData22.horizontalSpan = 2;
		gridData22.grabExcessHorizontalSpace = true;
		gridData22.heightHint = 5;
		gridData22.grabExcessVerticalSpace = false;
		GridData gridData12 = new org.eclipse.swt.layout.GridData();
		gridData12.verticalSpan = 1;
		gridData12.grabExcessVerticalSpace = false;
		gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData12.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData12.horizontalSpan = 2;
		gridData12.heightHint = 5;
		gridData12.grabExcessHorizontalSpace = true;
		GridData gridData21 = new GridData();
		gridData21.grabExcessHorizontalSpace = true;
		gridData21.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData21.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridData11 = new GridData();
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		gridData2.horizontalSpan = 2;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		sShell = new Shell(SWT.ON_TOP | SWT.SHELL_TRIM);
		sShell.setText("JMp3");
		sShell.setSize(new org.eclipse.swt.graphics.Point(217,159));
		sShell.setLayout(gridLayout);
		label = new Label(sShell, SWT.NONE);
		label.setText("Encoding:");
		label.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD | SWT.ITALIC));
		label.setLayoutData(gridData1);
		encodedFileNameLabel = new Label(sShell, SWT.NONE);
		encodedFileNameLabel.setText("");
		encodedFileNameLabel.setLayoutData(gridData11);
		encodedFilesLabel = new Label(sShell, SWT.CENTER);
		encodedFilesLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		encodedFilesLabel.setLayoutData(gridData);
		encodingProgress = new ProgressBar(sShell, SWT.HORIZONTAL | SWT.SMOOTH);
		encodingProgress.setLayoutData(gridData12);
		label2 = new Label(sShell, SWT.NONE);
		copyFileNameLabel = new Label(sShell, SWT.NONE);
		copyFileNameLabel.setLayoutData(gridData21);
		label2.setLayoutData(gridData3);
		label2.setText("Copying:");
		label2.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD | SWT.ITALIC));
		copyFilesLabel = new Label(sShell, SWT.CENTER);
		copyFilesLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		copyFilesLabel.setLayoutData(gridData2);
		copyingProgress = new ProgressBar(sShell, SWT.HORIZONTAL | SWT.SMOOTH);
		copyingProgress.setLayoutData(gridData22);
		createComposite();
	}
	
	public static void info(final String s) {
		if (!theApp.sShell.isDisposed())
		theApp.sShell.getDisplay().syncExec(new Runnable() {
			public void run() {
				theApp.debugList.add(s);
			}
		});
	}
	
	public static void debug(final String s) {
		if (!theApp.sShell.isDisposed())
		theApp.sShell.getDisplay().syncExec(new Runnable() {
			public void run() {
				//Color oldColor = theApp.debugList.getForeground();
				//theApp.debugList.setForeground(theApp.sShell.getDisplay().getSystemColor(SWT.COLOR_RED));
				theApp.debugList.add(s);
				//theApp.debugList.setForeground(oldColor);
			}
		});
	}
	
	public void addFile(File file) {
		addFile(file, ".");
	}
	
	public void addFile(File file, String path) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) addFile(f, path + "\\" + file.getName());
		} else {
			if (file.getName().toLowerCase().endsWith(".mp3")) {
				Mp3File mp3 = new Mp3File(file, path);
				encodingQueue.add(mp3);
				debug(path + "\\" + file.getName());
			}
		}
	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		GridData gridData4 = new org.eclipse.swt.layout.GridData();
		gridData4.horizontalSpan = 2;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData4.grabExcessVerticalSpace = false;
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		composite = new ExpandableComposite(sShell, SWT.NONE, ExpandableComposite.TWISTIE);
		composite.setExpanded(false);
		composite.setText("Log");
		composite.setLayoutData(gridData4);
		debugList = new List(composite, SWT.V_SCROLL | SWT.BORDER);
		debugList.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		composite.setExpanded(true);
		composite.setClient(debugList);
		composite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent arg0) {
				sShell.pack();
			}
		});
	}

	public static void main(String[] args) {
		new JMp3();
	}
}
