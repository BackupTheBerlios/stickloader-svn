/* Stickloader - http://stickloader.berlios.de
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopier implements FileProcessor {
	
	private String filename;
	private int totalCop = 0;
	private File destDir;
	private File tmpDir;
	private int progress;
	
	private boolean cancelNow = false;

	public FileCopier(File tmpDir, File dir) {
		destDir = dir;
		this.tmpDir = tmpDir;
		filename = "NOTHING";
	}
	
	public String getCurrentFilename() {
		return filename;
	}

	public int getProgress() {
		return progress;
	}
	
	private void makeDir(File dir) {
		if (!dir.exists()) {
			makeDir(dir.getParentFile());
			dir.mkdir();
			dir.deleteOnExit();
		}
	}

	public boolean processFile(Mp3File file) {
			cancelNow = false;
			File srcFile = new File(new File(tmpDir, file.getPath()), file.getTargetName());
			File targetDir = new File(destDir, file.getPath());
			makeDir(targetDir);
			File destFile = new File(targetDir, file.getTargetName());
			filename = destFile.getName();
			StickLoader.debug("copying "+filename);
			try {
				StickLoader.debug(destFile.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			if (destFile.exists()) destFile.delete();		
			
			byte [] buffer = new byte[8192];
			
			try {
				progress = 0;
				destFile.createNewFile();
				InputStream in = new BufferedInputStream( new FileInputStream(srcFile));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
				
				long max = srcFile.length();
				
				int length = 0;
				int pos = 0;
				while ((length = in.read(buffer)) >= 0) {
					out.write(buffer, 0, length);
					pos += length;
					progress = (int) (100*pos/max);
					
					if (cancelNow) {
						out.close();
						in.close();
						srcFile.delete();
						filename = "NOTHING";
						return false;
					}
				}
				in.close();
				out.flush();
				out.close();
				totalCop++;
				srcFile.delete();
				filename = "NOTHING";
				progress = 0;
				return true;
			} catch (FileNotFoundException exc) {
				exc.printStackTrace();
				srcFile.delete();
				filename = "NOTHING";
				progress = 0;
				return false;
			} catch (IOException exc) {
				exc.printStackTrace();
				srcFile.delete();
				filename = "NOTHING";
				progress = 0;
				return false;
			}
	}

	public int getTotalFiles() {
		return totalCop;
	}

	public void setDestDir(File destDir) {
		this.destDir = destDir;
	}

	public void setTempDir(File tmpDir) {
		this.tmpDir = tmpDir;
	}
	
	public void cancel() {
		cancelNow = true;
	}

}
