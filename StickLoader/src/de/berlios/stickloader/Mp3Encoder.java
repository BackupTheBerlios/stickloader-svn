/* Stickloader - http://stickloader.berlios.de
 * 
 * Created by Alexander Kaiser <mail@alexkaiser.de>
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import de.ueberdosis.mp3info.ID3Reader;
import de.ueberdosis.mp3info.ID3Tag;
import de.ueberdosis.mp3info.ID3Writer;

public class Mp3Encoder implements FileProcessor {

	private String lamePath;
	private String [] args; //= "--priority 1 -f --abr 100";

	private String filename;
	private File destDir;
	private int totalEnc = 0;
	
	private int progress = 0;
	
	private boolean cancelNow = false;
	
	public Mp3Encoder(File destDir, String lamePath, String args) {
		filename = "NOTHING";
		this.lamePath = lamePath;
		this.destDir = destDir;
		setLameArgs(args);
	}
	
	public String getCurrentFilename() {
		return filename;
	}

	public int getProgress() {
		return progress;
	}
	
	public void setDestDir(File f) {
		destDir = f;
	}
	
	public void setLameArgs(String s) {
		this.args = parseArgs("--priority --nohist --disptime 0.5 " + s.trim() + " ");
	}

	public boolean processFile(Mp3File file) {
		
		cancelNow = false;
		File targetDir = new File(destDir, file.getPath());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
			targetDir.deleteOnExit();
		}
		final File targetFile = new File(targetDir, file.getTargetName());
		
		filename = targetFile.getName();
		
		try {
			progress = 0;
			String [] newargs = new String[args.length+3];
			System.arraycopy(args, 0, newargs, 1, args.length);
			newargs[0] = lamePath;
			newargs[args.length+1] = file.getSrcFile().getAbsolutePath();
			newargs[args.length+2] = targetFile.getAbsolutePath();
			
			final Process ps = Runtime.getRuntime().exec(newargs);
			
			//Process ps = Runtime.getRuntime().exec(new String [] { lamePath, "--priority", "--nohist", "--disptime", "0.5", "-f", "--abr", "100", , targetFile.getAbsolutePath()});
			//final BufferedReader input = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			ps.getInputStream().close();
			final BufferedReader error = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
			ps.getOutputStream().close();
			Thread myThread = new Thread() {
				public void run() {
					String in = "", err = "";
					while (!isInterrupted()) {
						try {
							/*if (input.ready()) {
								in = input.readLine();
								System.out.println(in);
							}*/
							if (error.ready()) {
								if (cancelNow) {
									error.close();
									ps.destroy();
									targetFile.delete();
									return;
								}
								if (StickLoader.DEBUG) err = error.readLine();
								System.out.println(err);
								for (String s : err.split("[()]")) {
									if (s.endsWith("%")) {
										try {
											progress = Integer.parseInt(s.trim().replace("%", ""));
										} catch (Exception e) {
											//...
										}
									}
								}
								//System.err.println(err);
							}							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			myThread.start();			
			int returnValue = ps.waitFor();
			//System.out.println("LAME return: " + ps.waitFor());
				
			if (!cancelNow) totalEnc++;
			filename = "NOTHING";
			myThread.interrupt();
			if (cancelNow) {
				progress = 0;
				return false; 
			}
			else return (returnValue == 0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private String [] parseArgs(String args) {
		return args.split(" ");
	}
	
	
	public void setLamePath(String s) {
		lamePath = s;
	}
	
	public String getLamePath() {
		return lamePath;
	}
	
	public int getTotalFiles() {
		return totalEnc;
	}
	
	public void cancel() {
		cancelNow = true;
	}
}
