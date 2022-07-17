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
package cellularautomata.model;

import cellularautomata.Coordinates;
import cellularautomata.arrays.MultidimensionalArray;

public class RegularGrid<Array_Type extends MultidimensionalArray> implements Model {
	
	protected Array_Type values;
	protected int[] minCoordinates;
	protected Coordinates zeroCoords;

	public RegularGrid(Array_Type values, int[] minCoordinates) {
		int dimension = values.getDimension();
		if (dimension != minCoordinates.length) {
			throw new IllegalArgumentException("Min coordinates length must be equal to the values array dimension.");
		}
		zeroCoords = new Coordinates(new int[dimension]); 
		long expectedPositionCount = 1;
		for (int axis = 0; axis < dimension; axis++) {
			int size = values.getSize(axis, zeroCoords);
			if (size == 0) {
				throw new IllegalArgumentException("Array sizes must be greater than zero.");
			}
			expectedPositionCount *= size;
		}		
		if (expectedPositionCount != values.getPositionCount()) {
			throw new IllegalArgumentException("Array sizes must be regular.");
		}
		this.values = values;
		this.minCoordinates = minCoordinates;
	}

	@Override
	public int getGridDimension() {
		return values.getDimension();
	}

	@Override
	public int getMaxCoordinate(int axis) {
		return minCoordinates[axis] + values.getSize(axis, zeroCoords) - 1;
	}

	@Override
	public int getMinCoordinate(int axis) {
		return minCoordinates[axis];
	}
	
	protected Coordinates getArrayIndex(Coordinates coordinates) {
		int[] coordArray = coordinates.getCopyAsArray();
		for (int i = 0; i < coordArray.length; i++) {
			coordArray[i] -= minCoordinates[i];
		}
		return new Coordinates(coordArray);
	}

}
