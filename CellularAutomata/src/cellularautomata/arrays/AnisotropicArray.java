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
import cellularautomata.Utils;

/**
 * An array with the shape of an asymmetric sub-region of an hypercubic shape with isotropic symmetry around a center position.
 * Its positions are those that meet this condition: side >= coord 1 >= coord 2... >= coord n >= 0
 * 
 * @author Jaume
 *
 */
public abstract class AnisotropicArray implements MultidimensionalArray {

	protected int dimension;
	protected int side;

	public AnisotropicArray(int dimension, int side) {
		if (dimension < 0) {
			throw new IllegalArgumentException("Dimension cannot be smaller than zero.");
		}
		if (side < 0) {
			throw new NegativeArraySizeException();
		} else if (side == 0) {
			throw new UnsupportedOperationException("Arrays with zero positions are not supported.");
		}
		this.dimension = dimension;
		this.side = side;
	}

	protected int getInternalArrayIndex(Coordinates indexes) {
		int internalIndex = 0;
		int indexCount = indexes.getCount();
		for (int i = 0, dim = dimension; i < indexCount; i++, dim--) {
			internalIndex += Utils.getAnisotropicGridPositionCount(dim, indexes.get(i));
		}
		return internalIndex;
	}
	
	@Override
	public long getPositionCount() {
		return Utils.getAnisotropicGridPositionCount(dimension, side);
	}
	
	@Override
	public void forEachIndex(Consumer<Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] indexes = new int[dimension];
		if (dimension == 0) {
			consumer.accept(new Coordinates(indexes));
		} else {
			int sideMinusOne = side - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					consumer.accept(new Coordinates(indexes));
				}
				int currentIndex = indexes[currentAxis];
				int max;
				if (currentAxis == 0) {
					max = sideMinusOne;
				} else {
					max = indexes[currentAxis - 1];
				}
				if (currentIndex < max) {
					currentIndex++;
					indexes[currentAxis] = currentIndex;
					currentAxis = dimensionMinusOne;
				} else {
					indexes[currentAxis] = 0;
					currentAxis--;
				}
			}
		}
	}

	public void forEachEdgeIndex(int edgeWidth, Consumer<Coordinates> consumer) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater than or equal to one.");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		if (dimension > 0 && side <= edgeWidth) {
			forEachIndex(consumer);
		} else {
			int[] indexes = new int[dimension];
			if (dimension > 0) {
				indexes[0] = side - edgeWidth;
			}
			int sideMinusOne = side - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					consumer.accept(new Coordinates(indexes));
				}
				int currentIndex = indexes[currentAxis];
				int max;
				if (currentAxis == 0) {
					max = sideMinusOne;
				} else {
					max = indexes[currentAxis - 1];
				}
				if (currentIndex < max) {
					currentIndex++;
					indexes[currentAxis] = currentIndex;
					currentAxis = dimensionMinusOne;
				} else {
					indexes[currentAxis] = 0;
					currentAxis--;
				}
			}
		}
	}

	@Override
	public int getDimension() {
		return dimension;
	}

	@Override
	public int getSize(int axis, Coordinates indexes) {
		if (axis > 0) {
			return indexes.get(axis - 1) + 1;
		}
		return side;
	}
	
	public int getSide() {
		return side;
	}
}
