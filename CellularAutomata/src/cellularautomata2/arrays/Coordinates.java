/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata2.arrays;

import java.util.Arrays;

/**
 * A way to pass immutable coordinates to methods.
 * @author Jaume
 *
 */
public class Coordinates {
	
	private int[] coordinates;
	
	public Coordinates(int... coordinates) {
		this.coordinates = coordinates;
	}
	
	public int get(int axis) {
		return coordinates[axis];
	}
	
	public int getCount() {
		return coordinates.length;
	}
	
	public int[] getCopyAsArray() {
		return coordinates.clone();
	}
	
	public void copyIntoArray(int[] array) {
		System.arraycopy(coordinates, 0, array, 0, array.length);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(coordinates);
	}
	
}
