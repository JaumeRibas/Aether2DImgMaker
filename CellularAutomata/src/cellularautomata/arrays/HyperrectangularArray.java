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
package cellularautomata.arrays;

import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.Utils;

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
			int size = sizes[i];
			if (size < 0) {
				throw new NegativeArraySizeException();
			} else if (size == 0) {
				throw new UnsupportedOperationException("Arrays with zero positions are not supported.");
			}
		}
		this.sizes = sizes;
	}
	
	/**
	 * Returns the size of the array on the given axis.
	 * The axis must be greater than or equal to zero and smaller than the array's dimension returned by {@link #getDimension()}.
	 * 
	 * @param axis the axis on which the size is requested.
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
	public void forEachIndex(Consumer<? super Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] upperBounds = sizes.clone();
		Utils.addToArray(upperBounds, -1);
		int[] lowerBounds = new int[upperBounds.length];
		//Arrays.fill(lowerBounds, 0);
		forEachIndexWithinBounds(upperBounds, lowerBounds, consumer);
	}
	
	@Override
	public void forEachEvenIndex(Consumer<? super Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] upperBounds = sizes.clone();
		Utils.addToArray(upperBounds, -1);
		int[] lowerBounds = new int[upperBounds.length];
		//Arrays.fill(lowerBounds, 0);
		forEachEvenIndexWithinBounds(upperBounds, lowerBounds, consumer);
	}
	
	@Override
	public void forEachOddIndex(Consumer<? super Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] upperBounds = sizes.clone();
		Utils.addToArray(upperBounds, -1);
		int[] lowerBounds = new int[upperBounds.length];
		//Arrays.fill(lowerBounds, 0);
		forEachOddIndexWithinBounds(upperBounds, lowerBounds, consumer);
	}
	
	/**
	 * Feeds every index of the edges of the array to a {@link Consumer}.
	 * 
	 * @param consumer
	 */
	public void forEachEdgeIndex(int edgeWidth, Consumer<? super Coordinates> consumer) {
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
	 * Feeds every index within the passed bounds to a {@link Consumer}. 
	 * 
	 * @param upperBounds
	 * @param lowerBounds
	 * @param consumer
	 */
	public static void forEachIndexWithinBounds(int[] upperBounds, int[] lowerBounds, Consumer<? super Coordinates> consumer) {
		int dimension = upperBounds.length;
		int[] indexes = lowerBounds.clone();
		if (dimension == 0) {
			consumer.accept(new Coordinates(indexes));
		} else {
			int currentAxis = 0;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					consumer.accept(new Coordinates(indexes));
				}
				int currentIndex = indexes[currentAxis];
				if (currentIndex < upperBounds[currentAxis]) {
					currentIndex++;
					indexes[currentAxis] = currentIndex;
					currentAxis = 0;
				} else {
					indexes[currentAxis] = lowerBounds[currentAxis];
					currentAxis++;
				}
			}
		}
	}
	
	/**
	 * Feeds every even index of the edges of the array to a {@link Consumer}.
	 * 
	 * @param consumer
	 */
	public void forEachEvenEdgeIndex(int edgeWidth, Consumer<? super Coordinates> consumer) {
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
			//for all even positions
			forEachEvenIndexWithinBounds(upperBounds, lowerBounds, consumer);
		} else {
			int edgeWidthMinusOne = edgeWidth - 1;
			for (int i = 0; i < dimension; i++) {
				//top edge
				int realUpperBound = upperBounds[i];
				upperBounds[i] = edgeWidthMinusOne;
				forEachEvenIndexWithinBounds(upperBounds, lowerBounds, consumer);
				upperBounds[i] = realUpperBound;
				//bottom edge
				lowerBounds[i] = upperBounds[i] - edgeWidthMinusOne;
				forEachEvenIndexWithinBounds(upperBounds, lowerBounds, consumer);
				//new bounds to prevent repeating positions
				upperBounds[i] = lowerBounds[i] - 1;
				lowerBounds[i] = edgeWidth;
			}
		}		
	}
	
	/**
	 * Feeds every even index within the passed bounds to a {@link Consumer}. 
	 * 
	 * @param upperBounds
	 * @param lowerBounds
	 * @param consumer
	 */
	public static void forEachEvenIndexWithinBounds(int[] upperBounds, int[] lowerBounds, Consumer<? super Coordinates> consumer) {
		int dimension = upperBounds.length;
		int[] indexes = lowerBounds.clone();
		if (dimension == 0) {
			consumer.accept(new Coordinates(indexes));
		} else {
			int currentAxis = 0;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					int lowerBound = lowerBounds[0];
					int upperBound = upperBounds[0];
					int currentIndex = lowerBound;
					indexes[0] = currentIndex;
					if (!Utils.isEvenPosition(indexes)) {
						currentIndex++;
					}
					for (; currentIndex <= upperBound; currentIndex += 2) {//TODO fix infinite loop if upperBound >= Integer.MAX_VALUE - 1
						indexes[0] = currentIndex;
						consumer.accept(new Coordinates(indexes));
					}
					currentAxis = 1;
				} else {
					int currentIndex = indexes[currentAxis];
					if (currentIndex < upperBounds[currentAxis]) {
						currentIndex++;
						indexes[currentAxis] = currentIndex;
						currentAxis = 0;
					} else {
						indexes[currentAxis] = lowerBounds[currentAxis];
						currentAxis++;
					}
				}
			}
		}
	}
	
	/**
	 * Feeds every odd index of the edges of the array to a {@link Consumer}.
	 * 
	 * @param consumer
	 */
	public void forEachOddEdgeIndex(int edgeWidth, Consumer<? super Coordinates> consumer) {
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
			//for all odd positions
			forEachOddIndexWithinBounds(upperBounds, lowerBounds, consumer);
		} else {
			int edgeWidthMinusOne = edgeWidth - 1;
			for (int i = 0; i < dimension; i++) {
				//top edge
				int realUpperBound = upperBounds[i];
				upperBounds[i] = edgeWidthMinusOne;
				forEachOddIndexWithinBounds(upperBounds, lowerBounds, consumer);
				upperBounds[i] = realUpperBound;
				//bottom edge
				lowerBounds[i] = upperBounds[i] - edgeWidthMinusOne;
				forEachOddIndexWithinBounds(upperBounds, lowerBounds, consumer);
				//new bounds to prevent repeating positions
				upperBounds[i] = lowerBounds[i] - 1;
				lowerBounds[i] = edgeWidth;
			}
		}		
	}
	
	/**
	 * Feeds every odd index within the passed bounds to a {@link Consumer}. 
	 * 
	 * @param upperBounds
	 * @param lowerBounds
	 * @param consumer
	 */
	public static void forEachOddIndexWithinBounds(int[] upperBounds, int[] lowerBounds, Consumer<? super Coordinates> consumer) {
		int dimension = upperBounds.length;
		int[] indexes = lowerBounds.clone();
		if (dimension == 0) {
			consumer.accept(new Coordinates(indexes));
		} else {
			int currentAxis = 0;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					int lowerBound = lowerBounds[0];
					int upperBound = upperBounds[0];
					int currentIndex = lowerBound;
					indexes[0] = currentIndex;
					if (Utils.isEvenPosition(indexes)) {
						currentIndex++;
					}
					for (; currentIndex <= upperBound; currentIndex += 2) {//TODO fix infinite loop if upperBound >= Integer.MAX_VALUE - 1
						indexes[0] = currentIndex;
						consumer.accept(new Coordinates(indexes));
					}
					currentAxis = 1;
				} else {
					int currentIndex = indexes[currentAxis];
					if (currentIndex < upperBounds[currentAxis]) {
						currentIndex++;
						indexes[currentAxis] = currentIndex;
						currentAxis = 0;
					} else {
						indexes[currentAxis] = lowerBounds[currentAxis];
						currentAxis++;
					}
				}
			}
		}
	}

	@Override
	public int getSize(int axis, Coordinates indexes) {
		return sizes[axis];
	}
	
}
