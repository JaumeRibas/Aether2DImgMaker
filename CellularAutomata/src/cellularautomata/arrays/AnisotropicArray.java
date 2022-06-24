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
package cellularautomata.arrays;

import java.util.function.Consumer;

import cellularautomata.Coordinates;

/**
 * An array with the shape of an asymmetric sub-region of an hypercubic shape with isotropic symmetry around a center position.
 * Its positions are those that meet this condition: side >= coord 1 >= coord 2... >= coord n >= 0
 * 
 * @author Jaume
 *
 */
public abstract class AnisotropicArray extends HypercubicArray {

	public AnisotropicArray(int dimension, int side) {
		super(dimension, side);
	}

	@Override
	protected int getInternalArrayIndex(Coordinates indexes) {
		int internalIndex = 0;
		int indexCount = indexes.getCount();
		for (int i = 0, dim = dimension; i < indexCount; i++, dim--) {
			internalIndex += getPositionCount(dim, indexes.get(i));
		}
		return internalIndex;
	}
	
	public static long getPositionCount(int dimension, int side) {
		switch (dimension) {
			case 0:
				return 1;
			case 1://this case could be omitted
				return side;
			default:
				long count = 0;
				dimension--;
				for (int i = 1; i <= side; i++) {
					count += getPositionCount(dimension, i);
				}
				return count;
		}
	}
	
	@Override
	public long getPositionCount() {
		return getPositionCount(dimension, side);
	}
	
	@Override
	public void forEachPosition(Consumer<Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] coordinates = new int[dimension];
		if (dimension == 0) {
			consumer.accept(new Coordinates(coordinates));
		} else {
			int sideMinusOne = side - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					consumer.accept(new Coordinates(coordinates));
				}
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
	public void forEachEdgeIndex(int edgeWidth, Consumer<Coordinates> consumer) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater than or equal to one.");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		if (dimension > 0 && side <= edgeWidth) {
			forEachPosition(consumer);
		} else {
			int[] coordinates = new int[dimension];
			if (dimension > 0) {
				coordinates[0] = side - edgeWidth;
			}
			int sideMinusOne = side - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					consumer.accept(new Coordinates(coordinates));
				}
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
