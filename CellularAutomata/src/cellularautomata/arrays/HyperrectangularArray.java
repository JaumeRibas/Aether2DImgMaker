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
package cellularautomata.arrays;

import java.util.function.Consumer;

import cellularautomata.grid.Coordinates;

/**
 * A multidimensional array with hyperrectangular shape.
 * 
 * @author Jaume
 *
 */
public abstract class HyperrectangularArray implements MultidimensionalArray {
	
	protected int[] sizes;
	
	public HyperrectangularArray() {}
	
	/**
	 * Creates a multidimensional array with hyperrectangular shape with the given sizes.
	 * The dimension of the array will be equal to the length of the sizes array.
	 * 
	 * @param sizes
	 */
	public HyperrectangularArray(int[] sizes) {
		if (sizes == null) {
			throw new IllegalArgumentException("Sizes array cannot be null.");
		}
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] < 0) {
				throw new NegativeArraySizeException();
			}
		}
		this.sizes = sizes;
	}
	
	/**
	 * Returns the size of the array on the given axis.
	 * The axis must be greater than or equal to zero and smaller than the array's dimension returned by {@link #getDimension()}.
	 * 
	 * @param axis the index of the axis on which the size is requested.
	 * @return the size
	 */
	public int getSize(int axis) {
		return sizes[axis];
	}
	
	protected int getInternalArrayIndex(Coordinates indexes) {
		int internalIndex = 0;
		for (int partialDimension = sizes.length - 1; partialDimension > -1; partialDimension--) {
			int[] partialSizes = new int[partialDimension + 1];
			partialSizes[partialDimension] = indexes.get(partialDimension);
			for (int j = partialDimension - 1; j > -1; j--) {
				partialSizes[j] = sizes[j];
			}
			internalIndex += getPositionCount(partialSizes);
		}
		return internalIndex;
	}
	
	@Override
	public long getPositionCount() {
		return getPositionCount(sizes);
	}
	
	public static long getPositionCount(int[] sizes) {
		long count = 1;
		for (int i = 0; i < sizes.length; i++) {
			count *= sizes[i];
		}
		return count;
	}

	@Override
	public int getDimension() {
		return sizes.length;
	}
	
	
	@Override
	public void forEachPosition(Consumer<Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int dimension = getDimension();
		int[] upperBounds = new int[dimension];
		int[] lowerBounds = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			upperBounds[i] = getSize(i) - 1;
			lowerBounds[i] = 0;
		}
		forEachIndexWithinBounds(upperBounds, lowerBounds, consumer);
	}
	
	/**
	 * Executes a {@link Consumer<Coordinates>} for every index of the edges of the array.
	 * @param consumer
	 */
	public void forEachEdgeIndex(int edgeWidth, Consumer<Coordinates> consumer) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater than or equal to one.");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int dimension = getDimension();
		int[] upperBounds = new int[dimension];
		int[] lowerBounds = new int[dimension];
		int doubleEdgeWidthMinusOne = 2*edgeWidth - 1;
		boolean anyUpperBoundLowerOrEqualToDoubleEdgeWidth = false;
		for (int i = 0; i < dimension; i++) {
			int upperBound = getSize(i) - 1;
			upperBounds[i] = upperBound;
			//lowerBounds[i] = 0; default value
			anyUpperBoundLowerOrEqualToDoubleEdgeWidth = 
					anyUpperBoundLowerOrEqualToDoubleEdgeWidth 
					|| upperBound <= doubleEdgeWidthMinusOne;
		}
		if (anyUpperBoundLowerOrEqualToDoubleEdgeWidth) {
			//for all positions
			forEachIndexWithinBounds(upperBounds, lowerBounds, consumer);
		} else {
			int edgeWidthMinusOne = edgeWidth - 1;
			for (int i = 0; i < dimension; i++) {
				//top edge
				int realUpperBound = upperBounds[i];
				upperBounds[i] = edgeWidthMinusOne;
				forEachIndexWithinBounds(upperBounds, lowerBounds, consumer);
				upperBounds[i] = realUpperBound;
				//bottom edge
				lowerBounds[i] = upperBounds[i] - edgeWidthMinusOne;
				forEachIndexWithinBounds(upperBounds, lowerBounds, consumer);
				//new bounds to prevent repeating positions
				upperBounds[i] = lowerBounds[i] - 1;
				lowerBounds[i] = edgeWidth;
			}
		}		
	}
	
	/**
	 * Feeds every index within the passed bounds to a {@link Consumer<Coordinates>}. 
	 * 
	 * @param upperBounds
	 * @param lowerBounds
	 * @param consumer
	 */
	public static void forEachIndexWithinBounds(int[] upperBounds, int[] lowerBounds, Consumer<Coordinates> consumer) {
		int dimension = upperBounds.length;
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		if (dimension == 0) {
			consumer.accept(immutableCoordinates);
		} else {
			System.arraycopy(lowerBounds, 0, coordinates, 0, coordinates.length);
			int currentAxis = 0;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					consumer.accept(immutableCoordinates);
				}
				int currentCoordinate = coordinates[currentAxis];
				if (currentCoordinate < upperBounds[currentAxis]) {
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					currentAxis = 0;
				} else {
					coordinates[currentAxis] = lowerBounds[currentAxis];
					currentAxis++;
				}
			}
		}
	}
	
}