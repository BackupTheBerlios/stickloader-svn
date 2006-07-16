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

import java.util.LinkedList;

public class FileQueue extends LinkedList<Mp3File> {
	
	private static final long serialVersionUID = -1233639093117918185L;

	public int getFiles() {
		return size();
	}
	
	public Mp3File getNextFile() {
		return poll();
	}
	
	public void addFile(Mp3File file) {
		add(file);
	}

}
