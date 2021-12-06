/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata2.grid;

import cellularautomata.grid.PartialCoordinates;

/**
 * An hyperrectangular region of dimension n, whose edges are parallel to the axes.
 * 
 * @author Jaume
 *
 */
public class AlignedHyperrectangle implements GridRegion {
	
	private int[] upperBounds;
	private int[] lowerBounds;
	
	/**
	 * Creates an instance with the opposite vertices being vertex1 and vertex2.
	 * The containing grid dimension is obtained from the vertices' number of coordinates.
	 * 
	 * @param vertex1
	 * @param vertex2
	 */
	public AlignedHyperrectangle(int[] vertex1, int[] vertex2) {
		if (vertex1 == null || vertex2 == null) {
			throw new IllegalArgumentException("Vertices cannot be null.");
		}
		int gridDimension = vertex1.length;
		if (gridDimension != vertex2.length) {
			throw new IllegalArgumentException("Vertices must have the same number of coordinates.");
		}
		//get rectangle bounds from vertices
		upperBounds = new int[gridDimension];
		lowerBounds = new int[gridDimension];
		for (int i = 0; i < gridDimension; i++) {
			if (vertex1[i] > vertex2[i]) {
				upperBounds[i] = vertex1[i];
				lowerBounds[i] = vertex2[i];
			} else {
				upperBounds[i] = vertex2[i];
				lowerBounds[i] = vertex1[i];
			}
		}
	}

	@Override
	public int getGridDimension() {
		return upperBounds.length;
	}

	@Override
	public int getUpperBound(int axis, PartialCoordinates coordinates) {
		//The returned bounds are independent from the passed coordinates
		return upperBounds[axis];
	}

	@Override
	public int getLowerBound(int axis, PartialCoordinates coordinates) {
		//The returned bounds are independent from the passed coordinates
		return lowerBounds[axis];
	}

}
