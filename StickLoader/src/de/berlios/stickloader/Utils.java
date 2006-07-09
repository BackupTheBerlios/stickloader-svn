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

public class Utils {
	
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
	
	public static boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}
	
	public static boolean isOSX() {
		return System.getProperty("os.name").toLowerCase().contains("osx") || 
			   System.getProperty("os.name").toLowerCase().contains("os x");
	}
	
	public static boolean isExecutable(String fileName) {
		try {
			Runtime.getRuntime().exec(fileName).waitFor();
			return true;
		} catch (Exception e) {
			// exception -> filename is not executable
			return false;
		}
	}
}
