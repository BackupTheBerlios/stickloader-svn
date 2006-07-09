/*
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

 import java.io.File;

public class Mp3File {
	private File srcFile;
	private String path;
	
	public Mp3File(File srcFile, String path) {
		this.srcFile = srcFile;
		this.path = path;
	}

	public File getSrcFile() {
		return srcFile;
	}

	public String getPath() {
		return path;
	}
	 
	public String getTargetName() {
		String s = srcFile.getName();
		if (!s.toLowerCase().endsWith(".mp3")) s = s +".mp3";
		return s;
	}
}
