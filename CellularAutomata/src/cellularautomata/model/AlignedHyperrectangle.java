/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
package cellularautomata.model;

import cellularautomata.Coordinates;

/**
 * A hyperrectangular region of dimension n, whose edges are parallel to the axes.
 * 
 * @author Jaume
 *
 */
public class AlignedHyperrectangle implements Model {
	
	private int[] upperBounds;
	private int[] lowerBounds;
	
	/**
	 * Creates an instance with the opposite vertices being vertex1 and vertex2.
	 * The containing grid dimension is obtained from the vertices' number of coordinates.
	 * 
	 * @param vertex1
	 * @param vertex2
	 */
	public AlignedHyperrectangle(Coordinates vertex1, Coordinates vertex2) {
		if (vertex1 == null || vertex2 == null) {
			throw new IllegalArgumentException("Vertices cannot be null.");
		}
		int gridDimension = vertex1.getCount();
		if (gridDimension != vertex2.getCount()) {
			throw new IllegalArgumentException("Vertices must have the same number of coordinates.");
		}
		//get rectangle bounds from vertices
		upperBounds = new int[gridDimension];
		lowerBounds = new int[gridDimension];
		for (int i = 0; i < gridDimension; i++) {
			int c1 = vertex1.get(i);
			int c2 = vertex2.get(i);
			if (c1 > c2) {
				upperBounds[i] = c1;
				lowerBounds[i] = c2;
			} else {
				upperBounds[i] = c2;
				lowerBounds[i] = c1;
			}
		}
	}

	@Override
	public int getGridDimension() {
		return upperBounds.length;
	}

	@Override
	public int getMaxCoordinate(int axis) {
		return upperBounds[axis];
	}

	@Override
	public int getMinCoordinate(int axis) {
		return lowerBounds[axis];
	}

	@Override
	public Boolean nextStep() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean isChanged() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getStep() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSubfolderPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		throw new UnsupportedOperationException();
	}

}
