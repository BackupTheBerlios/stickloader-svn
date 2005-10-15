
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;


import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.swt.widgets.Table;

public class JMp3 {
	private static JMp3 theApp;
	
	final static public String NAME = "StickLoader";
	final static public String VERSION = "0.1";

	private FileQueue encodingQueue;
	private FileQueue copyQueue;
	
	private Mp3Encoder encoder;
	private FileCopier copier;
	
	private String lamePath;
	private File tempDir;// = new File("D:\\temp");
	private File destDir;// = new File("D:\\target");
	private String lameArgs = ""; // Defaultvalue
	
	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="93,43"
	private Label label = null;
	private CLabel encodedFileNameLabel = null;
	private CLabel encodedFilesLabel = null;
	private Label label2 = null;
	private CLabel copyFileNameLabel = null;
	private CLabel copyFilesLabel = null;
	private Menu menu = null;
	
	boolean running;
	private ProgressBar encodingProgress = null;
	private ProgressBar copyingProgress = null;
	private ExpandableComposite composite = null;

	private Table debugTable = null;
	
	private int backupTableSize = 100;

	public JMp3() {
		theApp = this;
		
		encodingQueue = new FileQueue();
		copyQueue = new FileQueue();

		createSShell();
		
		readProperties();
		verifySettings();
		
		encoder = new Mp3Encoder(tempDir, lamePath, lameArgs);
		copier = new FileCopier(tempDir, destDir);
		
		menu = getPopupMenu();
		
		sShell.open();
		
		addDrop();

		getEncodeThread().start();
		getCopyThread().start();
		getUIThread().start();
		
		 while (!sShell.isDisposed()) {
			 if (!sShell.getDisplay().readAndDispatch ()) sShell.getDisplay().sleep ();
		 }
		 
		 //maybe closing
		 writeProperties();
	}
	
	private void readProperties() {
		try {
			File f = new File(System.getProperty("user.home"), ".stickloader");
			if (f.exists()) {		
				Properties props = new Properties();
				props.loadFromXML(new FileInputStream(f));
				
				lamePath = props.getProperty("lame.path");
				lameArgs = props.getProperty("lame.args");
				tempDir = new File(props.getProperty("dir.temp"));
				destDir = new File(props.getProperty("dir.destination"));
				
				// TODO Some verification
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeProperties() {
		try {
			File f = new File(System.getProperty("user.home"), ".stickloader");
				Properties props = new Properties();
				
				props.setProperty("lame.path", lamePath);
				props.setProperty("lame.args", lameArgs);
				props.setProperty("dir.temp", tempDir.getCanonicalPath());
				props.setProperty("dir.destination", destDir.getCanonicalPath());
				
				props.storeToXML(new FileOutputStream(f), "Properties of " + NAME + " v" +VERSION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void verifySettings() {
		if (lameArgs == null) lameArgs = new LameArgsDialog("").getArgs();
		if (tempDir == null) getTempDir();
		if (destDir == null) getDestDir();
		if (lamePath == null) getLamePath();
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
		gridData11.grabExcessVerticalSpace = false;
		gridData11.heightHint = -1;
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
		sShell.setText(NAME + " v" + VERSION);
		sShell.setSize(new org.eclipse.swt.graphics.Point(217,183));
		sShell.setLayout(gridLayout);
		label = new Label(sShell, SWT.NONE);
		label.setText("Encoding:");
		label.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.BOLD | SWT.ITALIC));
		label.setLayoutData(gridData1);
		encodedFileNameLabel = new CLabel(sShell, SWT.NONE);
		encodedFileNameLabel.setText("");
		encodedFileNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		encodedFileNameLabel.setLayoutData(gridData11);
		encodedFilesLabel = new CLabel(sShell, SWT.CENTER);
		encodedFilesLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		encodedFilesLabel.setLayoutData(gridData);
		encodingProgress = new ProgressBar(sShell, SWT.HORIZONTAL | SWT.SMOOTH);
		encodingProgress.setLayoutData(gridData12);
		label2 = new Label(sShell, SWT.NONE);
		copyFileNameLabel = new CLabel(sShell, SWT.NONE);
		copyFileNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		copyFileNameLabel.setLayoutData(gridData21);
		label2.setLayoutData(gridData3);
		label2.setText("Copying:");
		label2.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.BOLD | SWT.ITALIC));
		copyFilesLabel = new CLabel(sShell, SWT.CENTER);
		copyFilesLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		copyFilesLabel.setLayoutData(gridData2);
		copyingProgress = new ProgressBar(sShell, SWT.HORIZONTAL | SWT.SMOOTH);
		copyingProgress.setLayoutData(gridData22);
		createComposite();
		
		// added by myself
		sShell.setSize(sShell.getSize().x, sShell.computeSize(sShell.getSize().x, SWT.DEFAULT, false).y);
		//add popup
		menu = new Menu(sShell);
		for (org.eclipse.swt.widgets.Control c : sShell.getChildren()) {
			c.setMenu(menu);
		}
		sShell.setMenu(menu);
		
/*			createPopupMenu(sShell, SWT.NONE);
		MenuItem popupMenuItem1 = createMenuItem(popup, SWT.PUSH, "&About", 
		                                         null, -1, true, "doAbout");
		MenuItem popupMenuItem2 = createMenuItem(popup, SWT.PUSH, "&Noop", 
		                                         null, -1, true, "doNothing");	*/
	}
	
	private Menu getPopupMenu() {
		//Menu menu = new Menu(sShell);
		
		for (MenuItem item : menu.getItems()) {
			item.dispose();
		}
		
		MenuItem menuitem = new MenuItem(menu, SWT.None);
		menuitem.setText("Set LAME path (" + lamePath + ")");
		menuitem.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLamePath();
				menu = getPopupMenu();
			}
		
		});
		
		MenuItem argsItem = new MenuItem(menu, SWT.None);
		argsItem.setText("Set LAME arguments (" + lameArgs + ")");
		argsItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				lameArgs = new LameArgsDialog(lameArgs).getArgs();
				encoder.setLameArgs(lameArgs);
				menu = getPopupMenu();
			}
		});
		
		MenuItem menuitem2 = new MenuItem(menu, SWT.None);
		menuitem2.setText("Set TEMP dir (" + tempDir.getAbsolutePath() + ")");
		menuitem2.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getTempDir();
				menu = getPopupMenu();
			}
		});
		
		MenuItem menuitem3 = new MenuItem(menu, SWT.None);
		menuitem3.setText("Set destination dir (" + destDir.getAbsolutePath() + ")");
		menuitem3.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getDestDir();
				menu = getPopupMenu();
			}
		});
		
		return menu;
	}
	
	public void getLamePath() {
		FileDialog dia = new FileDialog(sShell, SWT.OPEN);
		if (lamePath != null) dia.setFilterPath((encoder).getLamePath());
		String filename = dia.open(); 
		if (filename!=null) {
			lamePath = filename;
		    if (encoder != null) encoder.setLamePath(filename);
		}
	}
	
	public void getDestDir() {
		DirectoryDialog dialog = new DirectoryDialog(sShell);
		if (destDir != null) dialog.setFilterPath(destDir.getAbsolutePath());
		dialog.setMessage("Please select the directory which you want to use as the destination directory (i.e. the mp3 player)");
		String s = dialog.open();
		if (s != null) {
			destDir = new File(s);
			if (copier != null) copier.setDestDir(destDir);
		}
	}
	
	public void getTempDir() {
		DirectoryDialog dialog = new DirectoryDialog(sShell);
		if (tempDir != null) dialog.setFilterPath(tempDir.getAbsolutePath());
		dialog.setMessage("Please select the directory which you want to use as the temporary directory");
		String s = dialog.open();
		if (s != null) {					
			tempDir = new File(s);
			if (encoder != null) encoder.setDestDir(tempDir);
			if (copier != null) copier.setTempDir(tempDir);
		}
	}
	
	public static void info(final String s) {
		if (!theApp.sShell.isDisposed())
		theApp.sShell.getDisplay().syncExec(new Runnable() {
			public void run() {
				TableItem item = new TableItem(theApp.debugTable, SWT.NONE);
				item.setText(s);
				theApp.debugTable.showItem(item);
				
				//theApp.styledText.setText(s);
				//theApp.debugList.add(s);
			}
		});
	}
	
	public static void debug(final String s) {
		if (!theApp.sShell.isDisposed())
		theApp.sShell.getDisplay().syncExec(new Runnable() {
			public void run() {
				//Color oldColor = theApp.debugList.getForeground();
				//theApp.debugList.setForeground(theApp.sShell.getDisplay().getSystemColor(SWT.COLOR_RED));
				TableItem item = new TableItem(theApp.debugTable, SWT.NONE);
				item.setForeground(theApp.sShell.getDisplay().getSystemColor(SWT.COLOR_RED));
				item.setText(s);
				theApp.debugTable.showItem(item);
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
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		composite = new ExpandableComposite(sShell, SWT.NONE, ExpandableComposite.TWISTIE);
		composite.setExpanded(false);
		composite.setText("Log");
		composite.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		createTable();
		composite.setLayoutData(gridData4);
		composite.setClient(debugTable);
		composite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent arg0) {
				if (arg0.getState()) sShell.setSize(sShell.getSize().x, sShell.getSize().y + backupTableSize);
					else {
						int before = sShell.getSize().y;
						sShell.setSize(sShell.getSize().x, sShell.computeSize(sShell.getSize().x, SWT.DEFAULT, false).y);
						int after = sShell.getSize().y;
						backupTableSize = before - after;
						if (backupTableSize < 100) backupTableSize = 100;
					}
			}
		});
	}

	/**
	 * This method initializes table	
	 *
	 */
	private void createTable() {
		debugTable = new Table(composite, SWT.NONE);
		debugTable.setHeaderVisible(false);
		debugTable.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		debugTable.setLinesVisible(false);
	}

	public static void main(String[] args) {
		new JMp3();
	}
}
