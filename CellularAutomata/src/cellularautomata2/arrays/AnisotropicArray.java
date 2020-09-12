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

/**
 * An array with the shape of an asymmetric sub-region of a square-like multidimensional shape with isotropic symmetry around a center position.
 * Its positions are those that meet this condition: side >= coord 1 >= coord 2... >= coord n >= 0
 * 
 * @author Jaume
 *
 */
public abstract class AnisotropicArray extends SquareArray {

	public AnisotropicArray(int dimension, int side) {
		super(dimension, side);
	}

	@Override
	protected int getInternalArrayIndex(Coordinates indexes) {
		int internalIndex = 0;
		int indexCount = indexes.getCount();
		for (int i = 0, dim = getDimension(); i < indexCount; i++, dim--) {
			internalIndex += getVolume(dim, indexes.get(i));
		}
		return internalIndex;
	}
	
	public static long getVolume(int dimension, int side) {
		if (dimension == 1) {
			return side;
		} else {
			long volume = 0;
			dimension--;
			for (int i = 1; i <= side; i++) {
				volume += getVolume(dimension, i);
			}
			return volume;
		}
	}
	
	@Override
	public long getVolume() {
		return getVolume(dimension, side);
	}
	
	@Override
	public void forEachIndex(PositionCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The command cannot be null.");
		}
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		int sideMinusOne = side - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				int previousCoordinate = coordinates[currentAxis - 1];
				for (int currentCoordinate = 0; currentCoordinate <= previousCoordinate; currentCoordinate++) {
					coordinates[currentAxis] = currentCoordinate;
					command.execute(immutableCoordinates);
				}
				currentAxis--;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				int max;
				if (currentAxis == 0) {
					max = sideMinusOne;
				} else {
					max = coordinates[currentAxis - 1];
				}
				if (currentCoordinate < max) {
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					currentAxis = dimensionMinusOne;
				} else {
					coordinates[currentAxis] = 0;
					currentAxis--;
				}
			}
		}
	}

	@Override
	public void forEachEdgeIndex(int edgeWidth, PositionCommand command) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater or equal to one.");
		}
		if (command == null) {
			throw new IllegalArgumentException("The command cannot be null.");
		}
		if (side <= edgeWidth) {
			forEachIndex(command);
		} else {
			int[] coordinates = new int[dimension];
			if (dimension > 0) {
				coordinates[0] = side - edgeWidth;
			}
			Coordinates immutableCoordinates = new Coordinates(coordinates);
			int sideMinusOne = side - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					int previousCoordinate = coordinates[currentAxis - 1];
					for (int currentCoordinate = 0; currentCoordinate <= previousCoordinate; currentCoordinate++) {
						coordinates[currentAxis] = currentCoordinate;
						command.execute(immutableCoordinates);
					}
					currentAxis--;
				} else {
					int currentCoordinate = coordinates[currentAxis];
					int max;
					if (currentAxis == 0) {
						max = sideMinusOne;
					} else {
						max = coordinates[currentAxis - 1];
					}
					if (currentCoordinate < max) {
						currentCoordinate++;
						coordinates[currentAxis] = currentCoordinate;
						currentAxis = dimensionMinusOne;
					} else {
						coordinates[currentAxis] = 0;
						currentAxis--;
					}
				}
			}
		}
	}
}
