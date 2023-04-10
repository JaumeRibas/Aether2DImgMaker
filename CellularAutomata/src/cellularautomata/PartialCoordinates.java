/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

import java.util.Arrays;

/**
 * A way to pass immutable partial coordinates to methods.
 * @author Jaume
 *
 */
public class PartialCoordinates {
	
	private final Integer[] coordinates;
	
	public PartialCoordinates(Integer... coordinates) {
		this.coordinates = coordinates.clone();
	}
	
	public Integer get(int axis) {
		return coordinates[axis];
	}

	public int getCount() {
		return coordinates.length;
	}
	
	public Integer[] getCopyAsArray() {
		return coordinates.clone();
	}
	
	public void copyIntoArray(Integer[] array) {
		System.arraycopy(coordinates, 0, array, 0, array.length);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");
		int length = coordinates.length;
		if (length != 0) {
			sb.append(coordinates[0]);
			for (int i = 1; i != length; i++) {
				sb.append(", ").append(coordinates[i]);
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public boolean equals(PartialCoordinates other) {
		if (other == null)
			return false;
		return Arrays.equals(coordinates, other.coordinates);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null || other.getClass() != this.getClass()) {
			return false;
		}
		return Arrays.equals(coordinates, ((PartialCoordinates)other).coordinates);
	}
	
}
