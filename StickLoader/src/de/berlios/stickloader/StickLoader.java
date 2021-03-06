/* Stickloader - http://stickloader.berlios.de
 * 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.util.Vector;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.swt.widgets.Table;

import de.ueberdosis.mp3info.ID3Reader;
import de.ueberdosis.mp3info.ID3Tag;
import de.ueberdosis.mp3info.ID3Writer;

public class StickLoader {
	private static StickLoader theApp;
	
	final static public String NAME = "StickLoader";
	final static public String VERSION = "0.5";

	private FileQueue encodingQueue;
	private FileQueue copyQueue;
	
	private Mp3Encoder encoder;
	private FileCopier copier;
	
	private String lamePath;  //  @jve:decl-index=0:
	private File tempDir;// = new File("D:\\temp");  //  @jve:decl-index=0:
	private File destDir;// = new File("D:\\target");
	private String lameArgs = ""; // Defaultvalue
	//TODO: Put all lameArgs in one Vector
	
	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="182,38"
	private Label label = null;
	private CLabel encodedFileNameLabel = null;
	private CLabel encodedFilesLabel = null;
	private Label label2 = null;
	private CLabel copyFileNameLabel = null;
	private CLabel copyFilesLabel = null;
	private Menu menu = null;
	private LameArgsDialog lameArgsDialog;
	
	//boolean running;
	private ProgressBar encodingProgress = null;
	private ProgressBar copyingProgress = null;
	private ExpandableComposite composite = null;

	private Table debugTable = null;
	
	private int backupTableSize = 100;
	
	public static boolean DEBUG = false;

	public StickLoader() {
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
		
		debug(System.getProperty("user.dir"));
		debug(lamePath);
		
		 while (!sShell.isDisposed()) {
			 if (!sShell.getDisplay().readAndDispatch ()) sShell.getDisplay().sleep ();
		 }
		 
		 //maybe closing
		 writeProperties();
		 try{
			 Thread.sleep(5000);
		 } catch (Exception e) {
			 //...
		 }
		 System.exit(0); // now everything should be closed
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
		if (lameArgs == null) lameArgs = "";
		if (tempDir == null) {
			tempDir = new File(System.getProperty("java.io.tmpdir"));
		}
		if (destDir == null) getDestDir();
		if (destDir == null) destDir = new File(".");
		if (lamePath != null) {
			if (!Utils.isExecutable(lamePath)) lamePath = null;
		} 
		if (lamePath == null) 
			if (!findLame()) getLamePath();
		if (lamePath == null) lamePath= "";
	}
	
	/**
	 * look for LAME
	 *
	 */
	private boolean findLame() {
		if (lookForLame(new File(System.getProperty("user.dir")))) return true;
		String [] pathDirs = System.getenv("PATH").split(System.getProperty("path.separator"));
		for (String s : pathDirs) {
			if (lookForLame(new File(s))) return true;
		}
		
		return false;
	}
	
	private boolean lookForLame(File dir) {
		File f;
		if ((f = new File(dir, "lame")).exists()) {
			if (Utils.isExecutable(f.getAbsolutePath())){
				lamePath = f.getAbsolutePath();
				return true;
			}
			
		} else if ((f = new File(dir, "lame.exe")).exists()) {
			if (Utils.isExecutable(f.getAbsolutePath())){
				lamePath = f.getAbsolutePath();
				return true;
			}
		}
		return false;
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
				System.out.println("UIThread finished");
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
							sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else {
						debug("copyQueue not empty");
						Mp3File mp3 = copyQueue.poll();						
						if (copier.processFile(mp3)) info("File copied: \"" + mp3.getSrcFile().getName() + "\"");
						else error("Copy failed: \"" + mp3.getSrcFile().getName() + "\"");
					}
				}
				System.out.println("CopyThread finished");
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
						// copy id3-tag
						if (mp3.getSrcFile().getName().endsWith(".mp3")) {
							try {
								Vector <String> tempArgs = new Vector<String>();
								
								ID3Tag tag = ID3Reader.readTag(new RandomAccessFile(mp3.getSrcFile(), "r" ));
								
								// set temporary lameArgs
								tempArgs.add("--mp3input");

								if (tag.isValidTag()) {								
									if (!tag.getTitle().trim().equals("")) {
										tempArgs.add("--tt");
										tempArgs.add(tag.getTitle());
									}
									if (!tag.getArtist().trim().equals("")) {
										tempArgs.add("--ta");
										tempArgs.add(tag.getArtist()); 
									}
									if (!tag.getAlbum().trim().equals("")) {
										tempArgs.add("--tl");
										tempArgs.add(tag.getAlbum());
									}
									if (!tag.getYear().trim().equals("")) {
										tempArgs.add("--ty");
										tempArgs.add(tag.getYear());
									}
									if (!tag.getComment().trim().equals("")) {
										tempArgs.add("--tc");
										tempArgs.add(tag.getComment());
									}
									if (!tag.getTrackS().trim().equals("")) {
										tempArgs.add("--tn");
										tempArgs.add(tag.getTrackS());
									}
									
									tempArgs.add("--tg");
									debug("Genre: "  + tag.getGenre());
									tempArgs.add("" + tag.getGenre());									
								} else debug("No valid tag");

								encoder.setTempArgs(tempArgs);
									
							} catch (Exception e) {
								error("Exception while reading ID3: " + e.getMessage());
								if (DEBUG) e.printStackTrace();
							}
						}

						if (encoder.processFile(mp3)) {							
							copyQueue.addFile(mp3);
							info("File encoded: \"" + mp3.getSrcFile().getName() + "\"");
						} else {
							error("Encoding failed: " + mp3.getSrcFile().getName() + "\"");
						}
					}
				}
				System.out.println("EncodeThread finished");
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

		// ON_TOP does not work on Linux
		int shellProperties;
		if (Utils.isWindows()) 
			shellProperties = SWT.ON_TOP | SWT.SHELL_TRIM;
		 else shellProperties = SWT.SHELL_TRIM;

		sShell = new Shell(shellProperties);
		sShell.setText(NAME + " v" + VERSION);
		sShell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/de/berlios/stickloader/resources/icon.png")));
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
		
		sShell.addListener (SWT.Close, new Listener () {
			public void handleEvent (Event event) {
				if (!copier.getCurrentFilename().equals("NOTHING") ||
						!encoder.getCurrentFilename().equals("NOTHING") ||
						copyQueue.size() > 0 || encodingQueue.size() > 0)  {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox (sShell, style);
					messageBox.setText ("Cancel the processes");
					messageBox.setMessage ("There are still running processes. Do you really want to close the program?");
					event.doit = messageBox.open () == SWT.YES;
				}
			}
		});
		
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
		
		MenuItem menuItemLamePath = new MenuItem(menu, SWT.None);
		menuItemLamePath.setText("Set LAME path (" + lamePath + ")");
		menuItemLamePath.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLamePath();
				menu = getPopupMenu();
			}
		
		});
		
		MenuItem menuItemLameArgs = new MenuItem(menu, SWT.None);
		menuItemLameArgs.setText("Set LAME arguments (" + lameArgs + ")");
		menuItemLameArgs.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lameArgsDialog == null) {
					// don't put dialog under main window
					int x = 10, y = 100;
					int displayWidth = sShell.getDisplay().getBounds().width;
					int displayHeight = sShell.getDisplay().getBounds().height;
					if (sShell.getBounds().x > displayWidth/2) {
						// sShell right, dialog left
						x = 100;
					} else {
						x = sShell.getBounds().x + sShell.getBounds().width + 50;
						if (sShell.getBounds().y < displayHeight/2) y = sShell.getBounds().y;
					}
					lameArgsDialog = new LameArgsDialog(lameArgs, x, y);
				}
				
				lameArgsDialog.show();
				lameArgs = lameArgsDialog.getArgs();
				encoder.setLameArgs(lameArgs);
				menu = getPopupMenu();
			}
		});
		
		MenuItem menuItemTempDir = new MenuItem(menu, SWT.None);
		menuItemTempDir.setText("Set TEMP dir (" + tempDir.getAbsolutePath() + ")");
		menuItemTempDir.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getTempDir();
				menu = getPopupMenu();
			}
		});
		
		MenuItem menuItemDestDir = new MenuItem(menu, SWT.None);
		menuItemDestDir.setText("Set destination dir (" + destDir.getAbsolutePath() + ")");
		menuItemDestDir.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getDestDir();
				menu = getPopupMenu();
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem menuItemCancel = new MenuItem(menu, SWT.NONE);
		menuItemCancel.setText("Cancel processes");
		menuItemCancel.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancel();
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem menuItemAbout = new MenuItem(menu, SWT.NONE);
		menuItemAbout.setText("About Stickloader...");
		menuItemAbout.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				new AboutDialog().show();
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem menuItemClose = new MenuItem(menu, SWT.NONE);
		menuItemClose.setText("Close StickLoader");
		menuItemClose.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				sShell.close();
			}
		});
		
		return menu;
	}
	
	private void cancel() {
		if (!copier.getCurrentFilename().equals("NOTHING") ||
				!encoder.getCurrentFilename().equals("NOTHING") ||
				copyQueue.size() > 0 || encodingQueue.size() > 0)  {
			int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
			MessageBox messageBox = new MessageBox (sShell, style);
			messageBox.setText ("Cancel the processes");
			messageBox.setMessage ("There are still running processes. Do you really want to cancel them?");
			if (messageBox.open() == SWT.NO) return;
		}
		
		copier.cancel();
		encoder.cancel();
		encodingQueue.clear();
		for (Mp3File mp3 : copyQueue) {
			// delete all temporary files
			File f = new File(tempDir, mp3.getTargetName());
			if (f.exists()) f.delete();
		}
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
		if (DEBUG) {
			String errorPosition = "";
			StackTraceElement [] stackTrace = new Exception().getStackTrace();
			try {				
				errorPosition = stackTrace[1].getClassName() + 
					"." + stackTrace[1].getMethodName()
					+"("+stackTrace[1].getFileName() + ":" +
					stackTrace[1].getLineNumber()+")";
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (!theApp.sShell.isDisposed()) {
				theApp.sShell.getDisplay().syncExec(new Runnable() {
					public void run() {
						//Color oldColor = theApp.debugList.getForeground();
						//theApp.debugList.setForeground(theApp.sShell.getDisplay().getSystemColor(SWT.COLOR_RED));
						TableItem item = new TableItem(theApp.debugTable, SWT.NONE);
						item.setForeground(theApp.sShell.getDisplay().getSystemColor(SWT.COLOR_GREEN));
						item.setText(s);
						theApp.debugTable.showItem(item);
						//theApp.debugList.setForeground(oldColor);
					}
				});
			}
			System.out.println("DEBUG " + errorPosition + " : " + s);
		}
		
	}
	
	public static void error(final String s) {
		if (!theApp.sShell.isDisposed())
		theApp.sShell.getDisplay().syncExec(new Runnable() {
			public void run() {
				TableItem item = new TableItem(theApp.debugTable, SWT.NONE);
				item.setForeground(theApp.sShell.getDisplay().getSystemColor(SWT.COLOR_RED));
				item.setText(s);
				theApp.debugTable.showItem(item);
				
				//theApp.styledText.setText(s);
				//theApp.debugList.add(s);
			}
		});
	}
	
	public void addFile(File file) {
		addFile(file, ".");
	}
	
	public void addFile(File file, String path) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) addFile(f, path + Utils.FILE_SEPARATOR + file.getName());
		} else {
			if (file.getName().toLowerCase().endsWith(".mp3") || file.getName().toLowerCase().endsWith(".wav")) {
				Mp3File mp3 = new Mp3File(file, path);
				encodingQueue.add(mp3);
				debug("Add File: " + file.getAbsolutePath());
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
		//change library path, but doesn't work on linux
		String libraryPath = System.getProperty("java.library.path");
		boolean containsCurrentDir = false;
		for (String s : libraryPath.split(System.getProperty("path.separator"))) {
			if (s.equals(".")) containsCurrentDir = true;
		}
		if (new File("/usr/lib").exists()) {
			libraryPath = "/usr/lib" + System.getProperty("path.separator") + libraryPath;
		}
		if (new File("/usr/lib/jni").exists()) {
			libraryPath = "/usr/lib/jni" + System.getProperty("path.separator") + libraryPath;
		}
		// TODO: What other locations might be important?
		if (!containsCurrentDir) {
			libraryPath = "." + System.getProperty("path.separator") + libraryPath;
		}
		System.setProperty("java.library.path", libraryPath);
		
		for (String s : args) {
			if (s.trim().equals("-debug")) {
				System.out.println(System.getProperty("java.library.path"));
				DEBUG = true;
			}
		}
		new StickLoader();
	}
}
