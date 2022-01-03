/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package cellularautomata;

public class Constants {
	public static final int ARRAY_SIZE_OVERHEAD = 16;
	
	/**
	 * Number of bytes in 1 GiB
	 */
	public static final long ONE_GB = 1073741824;
	
	/**
	 * Number of bytes in 1 MiB
	 */
	public static final long ONE_MB = 1048576;
	
	/**
	 * Number of bytes in 1 KiB
	 */
	public static final long ONE_KB = 1024;
	
	/**
	 * Maximum length of the string representation of the initial value of a model in its subfolder path.
	 */
	public static final int MAX_INITIAL_VALUE_LENGTH_IN_PATH = 50;
}
